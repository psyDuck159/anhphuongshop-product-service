package biz.anhld.anhphuongshop.productservice.repository;

import biz.anhld.anhphuongshop.productservice.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    Optional<Reservation> findByIdempotencyKey(String idempotencyKey);

    List<Reservation> findByOrderId(Long orderId);
}
