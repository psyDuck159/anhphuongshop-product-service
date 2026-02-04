package biz.anhld.anhphuongshop.productservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import biz.anhld.anhphuongshop.productservice.entity.Product;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
  Optional<Product> findBySlug(String slug);

  List<Product> findByIdIn(List<Long> productIds);

  @Query("SELECT DISTINCT p FROM Product p " +
         "LEFT JOIN FETCH p.category c " +
         "WHERE (LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) OR :search IS NULL OR :search = '') " +
         "AND (:categoryId IS NULL OR c.id = :categoryId)")
  Page<Product> searchProducts(
    @Param("search") String search, 
    @Param("categoryId") Long categoryId, 
    Pageable pageable
  );
}
