package biz.anhld.anhphuongshop.productservice.repository;

import biz.anhld.anhphuongshop.productservice.entity.ReservationEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationEventRepository extends JpaRepository<ReservationEvent, Long> {
}
