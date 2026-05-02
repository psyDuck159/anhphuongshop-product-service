package biz.anhld.anhphuongshop.productservice.controller;

import biz.anhld.anhphuongshop.productservice.dto.inventory.ConfirmReservationRequest;
import biz.anhld.anhphuongshop.productservice.dto.inventory.ReleaseReservationRequest;
import biz.anhld.anhphuongshop.productservice.dto.inventory.ReserveInventoryRequest;
import biz.anhld.anhphuongshop.productservice.dto.inventory.ReserveInventoryResponse;
import biz.anhld.anhphuongshop.productservice.service.InventoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getInventory(@PathVariable Long id) {
        return ResponseEntity.ok("Inventory details for ID: " + id);
    }

    /**
     * reserve + return reservationId
     */
    @PostMapping("/reserve")
    public ResponseEntity<ReserveInventoryResponse> reserveInventory(@RequestBody ReserveInventoryRequest request) {
        ReserveInventoryResponse response = inventoryService.reserve(request);
        return ResponseEntity.ok(response);
    }

    /**
     * commit after payment
     */
    @PostMapping("/confirm")
    public ResponseEntity<ReserveInventoryResponse> confirmReservation(@RequestBody ConfirmReservationRequest request) {
        return ResponseEntity.ok(inventoryService.confirm(request));
    }

    /**
     * rollback on failure
     */
    @PostMapping("/release")
    public ResponseEntity<ReserveInventoryResponse> releaseReservation(@RequestBody ReleaseReservationRequest request) {
        return ResponseEntity.ok(inventoryService.release(request));
    }

    @PostMapping("/confirm-by-order")
    public ResponseEntity<Void> confirmByOrder(@RequestBody Map<String, Long> body) {
        inventoryService.confirmByOrderId(body.get("orderId"));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/release-by-order")
    public ResponseEntity<Void> releaseByOrder(@RequestBody Map<String, Long> body) {
        inventoryService.releaseByOrderId(body.get("orderId"));
        return ResponseEntity.ok().build();
    }
}
