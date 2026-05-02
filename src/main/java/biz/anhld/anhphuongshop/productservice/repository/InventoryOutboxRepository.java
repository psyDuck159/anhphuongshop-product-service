package biz.anhld.anhphuongshop.productservice.repository;

import biz.anhld.anhphuongshop.productservice.entity.InventoryOutbox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryOutboxRepository extends JpaRepository<InventoryOutbox, Long> {
}
