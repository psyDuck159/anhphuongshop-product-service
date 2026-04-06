package biz.anhld.anhphuongshop.productservice.repository;

import biz.anhld.anhphuongshop.productservice.entity.StockImport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockImportRepository extends JpaRepository<StockImport, Long> {
    Page<StockImport> findAllByOrderByImportDateDesc(Pageable pageable);
}
