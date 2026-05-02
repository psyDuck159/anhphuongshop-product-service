package biz.anhld.anhphuongshop.productservice.service;

import biz.anhld.anhphuongshop.productservice.dto.inventory.ConfirmReservationRequest;
import biz.anhld.anhphuongshop.productservice.dto.inventory.ReleaseReservationRequest;
import biz.anhld.anhphuongshop.productservice.dto.inventory.ReserveInventoryRequest;
import biz.anhld.anhphuongshop.productservice.dto.inventory.ReserveInventoryResponse;
import biz.anhld.anhphuongshop.productservice.entity.Inventory;
import biz.anhld.anhphuongshop.productservice.entity.InventoryOutbox;
import biz.anhld.anhphuongshop.productservice.entity.Reservation;
import biz.anhld.anhphuongshop.productservice.entity.ReservationEvent;
import biz.anhld.anhphuongshop.productservice.exception.BadRequestException;
import biz.anhld.anhphuongshop.productservice.repository.InventoryOutboxRepository;
import biz.anhld.anhphuongshop.productservice.repository.InventoryRepository;
import biz.anhld.anhphuongshop.productservice.repository.ReservationEventRepository;
import biz.anhld.anhphuongshop.productservice.repository.ReservationRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class InventoryService {

    private static final int RESERVATION_TTL_MINUTES = 15;

    private final InventoryRepository inventoryRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationEventRepository reservationEventRepository;
    private final InventoryOutboxRepository inventoryOutboxRepository;
    private final ObjectMapper objectMapper;

    public InventoryService(
            InventoryRepository inventoryRepository,
            ReservationRepository reservationRepository,
            ReservationEventRepository reservationEventRepository,
            InventoryOutboxRepository inventoryOutboxRepository,
            ObjectMapper objectMapper) {
        this.inventoryRepository = inventoryRepository;
        this.reservationRepository = reservationRepository;
        this.reservationEventRepository = reservationEventRepository;
        this.inventoryOutboxRepository = inventoryOutboxRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public ReserveInventoryResponse reserve(ReserveInventoryRequest request) {
        // idempotency check — return existing reservation if key already processed
        Optional<Reservation> existing = reservationRepository.findByIdempotencyKey(request.getIdempotencyKey());
        if (existing.isPresent()) {
            Reservation r = existing.get();
            return toResponse(r);
        }

        // SELECT FOR UPDATE on inventory row
        Inventory inventory = inventoryRepository
                .findByProductIdWithLock(request.getProductId())
                .orElseThrow(() -> new BadRequestException("Inventory not found for product: " + request.getProductId()));

        if (inventory.getQuantityAvailable() < request.getQuantity()) {
            throw new BadRequestException("Insufficient stock: available=" + inventory.getQuantityAvailable()
                    + ", requested=" + request.getQuantity());
        }

        // insert reservation
        LocalDateTime now = LocalDateTime.now();
        Reservation reservation = new Reservation();
        reservation.setInventoryId(inventory.getId());
        reservation.setOrderId(request.getOrderId());
        reservation.setIdempotencyKey(request.getIdempotencyKey());
        reservation.setQuantity(request.getQuantity());
        reservation.setStatus("PENDING");
        reservation.setCreatedAt(now);
        reservation.setExpiresAt(now.plusMinutes(RESERVATION_TTL_MINUTES));
        reservation = reservationRepository.save(reservation);

        // update inventory
        inventory.setQuantityReserved(inventory.getQuantityReserved() + request.getQuantity());
        inventory.setQuantityAvailable(inventory.getQuantityAvailable() - request.getQuantity());
        inventory.setUpdatedAt(now);
        inventoryRepository.save(inventory);

        // insert reservation_event
        Map<String, Object> eventPayload = Map.of(
                "reservationId", reservation.getId(),
                "inventoryId", inventory.getId(),
                "orderId", request.getOrderId(),
                "quantity", request.getQuantity()
        );
        ReservationEvent event = new ReservationEvent();
        event.setReservation(reservation);
        event.setEventType("CREATED");
        event.setPayload(eventPayload);
        event.setPublished(false);
        event.setCreatedAt(now);
        reservationEventRepository.save(event);

        // insert inventory_outbox
        InventoryOutbox outbox = new InventoryOutbox();
        outbox.setAggregateId(inventory.getId());
        outbox.setEventType("RESERVATION_CREATED");
        outbox.setPayload(buildOutboxPayload(reservation));
        outbox.setTimestamp(System.currentTimeMillis());
        inventoryOutboxRepository.save(outbox);

        return toResponse(reservation);
    }

    @Transactional
    public ReserveInventoryResponse confirm(ConfirmReservationRequest request) {
        Reservation reservation = reservationRepository.findById(request.getReservationId())
                .orElseThrow(() -> new BadRequestException("Reservation not found: " + request.getReservationId()));

        if ("CONFIRMED".equals(reservation.getStatus())) {
            return toResponse(reservation);
        }
        if (!"PENDING".equals(reservation.getStatus())) {
            throw new BadRequestException("Reservation not in PENDING state: " + reservation.getStatus());
        }

        LocalDateTime now = LocalDateTime.now();
        if (reservation.getExpiresAt().isBefore(now)) {
            throw new BadRequestException("Reservation expired");
        }

        Inventory inventory = inventoryRepository.findByIdWithLock(reservation.getInventoryId())
                .orElseThrow(() -> new BadRequestException("Inventory not found: " + reservation.getInventoryId()));

        reservation.setStatus("CONFIRMED");
        reservation.setConfirmedAt(now);
        reservationRepository.save(reservation);

        inventory.setQuantityReserved(inventory.getQuantityReserved() - reservation.getQuantity());
        inventory.setQuantitySold(inventory.getQuantitySold() + reservation.getQuantity());
        inventory.setUpdatedAt(now);
        inventoryRepository.save(inventory);

        ReservationEvent event = new ReservationEvent();
        event.setReservation(reservation);
        event.setEventType("CONFIRMED");
        event.setPayload(Map.of(
                "reservationId", reservation.getId(),
                "inventoryId", inventory.getId(),
                "orderId", reservation.getOrderId(),
                "quantity", reservation.getQuantity()
        ));
        event.setPublished(false);
        event.setCreatedAt(now);
        reservationEventRepository.save(event);

        InventoryOutbox outbox = new InventoryOutbox();
        outbox.setAggregateId(inventory.getId());
        outbox.setEventType("RESERVATION_CONFIRMED");
        outbox.setPayload(buildOutboxPayload(reservation));
        outbox.setTimestamp(System.currentTimeMillis());
        inventoryOutboxRepository.save(outbox);

        return toResponse(reservation);
    }

    @Transactional
    public ReserveInventoryResponse release(ReleaseReservationRequest request) {
        Reservation reservation = reservationRepository.findById(request.getReservationId())
                .orElseThrow(() -> new BadRequestException("Reservation not found: " + request.getReservationId()));

        if ("RELEASED".equals(reservation.getStatus())) {
            return toResponse(reservation);
        }
        if (!"PENDING".equals(reservation.getStatus())) {
            throw new BadRequestException("Reservation not in PENDING state: " + reservation.getStatus());
        }

        LocalDateTime now = LocalDateTime.now();

        Inventory inventory = inventoryRepository.findByIdWithLock(reservation.getInventoryId())
                .orElseThrow(() -> new BadRequestException("Inventory not found: " + reservation.getInventoryId()));

        reservation.setStatus("RELEASED");
        reservation.setReleasedAt(now);
        reservationRepository.save(reservation);

        inventory.setQuantityReserved(inventory.getQuantityReserved() - reservation.getQuantity());
        inventory.setQuantityAvailable(inventory.getQuantityAvailable() + reservation.getQuantity());
        inventory.setUpdatedAt(now);
        inventoryRepository.save(inventory);

        ReservationEvent event = new ReservationEvent();
        event.setReservation(reservation);
        event.setEventType("CANCELED");
        event.setPayload(Map.of(
                "reservationId", reservation.getId(),
                "inventoryId", inventory.getId(),
                "orderId", reservation.getOrderId(),
                "quantity", reservation.getQuantity()
        ));
        event.setPublished(false);
        event.setCreatedAt(now);
        reservationEventRepository.save(event);

        InventoryOutbox outbox = new InventoryOutbox();
        outbox.setAggregateId(inventory.getId());
        outbox.setEventType("RESERVATION_CANCELED");
        outbox.setPayload(buildOutboxPayload(reservation));
        outbox.setTimestamp(System.currentTimeMillis());
        inventoryOutboxRepository.save(outbox);

        return toResponse(reservation);
    }

    @Transactional
    public void confirmByOrderId(Long orderId) {
        List<Reservation> pending = reservationRepository.findByOrderId(orderId).stream()
                .filter(r -> "PENDING".equals(r.getStatus()))
                .toList();
        for (Reservation r : pending) {
            confirm(new ConfirmReservationRequest() {{ setReservationId(r.getId()); }});
        }
    }

    @Transactional
    public void releaseByOrderId(Long orderId) {
        List<Reservation> pending = reservationRepository.findByOrderId(orderId).stream()
                .filter(r -> "PENDING".equals(r.getStatus()))
                .toList();
        for (Reservation r : pending) {
            release(new ReleaseReservationRequest() {{ setReservationId(r.getId()); }});
        }
    }

    private String buildOutboxPayload(Reservation reservation) {
        try {
            return objectMapper.writeValueAsString(Map.of(
                    "reservationId", reservation.getId(),
                    "inventoryId", reservation.getInventoryId(),
                    "orderId", reservation.getOrderId(),
                    "idempotencyKey", reservation.getIdempotencyKey(),
                    "quantity", reservation.getQuantity(),
                    "status", reservation.getStatus()
            ));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize outbox payload", e);
        }
    }

    private ReserveInventoryResponse toResponse(Reservation r) {
        return new ReserveInventoryResponse(
                r.getId(),
                r.getInventoryId(),
                r.getOrderId(),
                r.getIdempotencyKey(),
                r.getQuantity(),
                r.getStatus()
        );
    }
}
