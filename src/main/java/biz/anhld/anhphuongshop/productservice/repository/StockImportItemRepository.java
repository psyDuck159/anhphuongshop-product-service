package biz.anhld.anhphuongshop.productservice.repository;

import biz.anhld.anhphuongshop.productservice.entity.StockImportItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockImportItemRepository extends JpaRepository<StockImportItem, Long> {}
