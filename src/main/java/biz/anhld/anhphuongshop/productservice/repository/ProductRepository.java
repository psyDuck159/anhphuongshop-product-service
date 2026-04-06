package biz.anhld.anhphuongshop.productservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import biz.anhld.anhphuongshop.productservice.entity.Product;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Modifying;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
  @Query("SELECT p FROM Product p LEFT JOIN FETCH p.category WHERE p.slug = :slug AND (p.deleted IS NULL OR p.deleted = false)")
  Optional<Product> findBySlug(@Param("slug") String slug);

  @Query("SELECT p FROM Product p WHERE p.id = :id AND (p.deleted IS NULL OR p.deleted = false)")
  Optional<Product> findActiveById(@Param("id") Long id);

  @Modifying
  @Query("UPDATE Product p SET p.deleted = true WHERE p.id = :id")
  void softDelete(@Param("id") Long id);

  List<Product> findByIdIn(List<Long> productIds);

  @Modifying
  @Query("UPDATE Product p SET p.stock = p.stock - :quantity WHERE p.id = :id AND p.stock >= :quantity")
  int decreaseStock(@Param("id") Long id, @Param("quantity") int quantity);

  @Modifying
  @Query("UPDATE Product p SET p.stock = p.stock + :quantity WHERE p.id = :id")
  int increaseStock(@Param("id") Long id, @Param("quantity") int quantity);

  @Query("SELECT DISTINCT p FROM Product p " +
         "LEFT JOIN FETCH p.category c " +
         "WHERE (LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) OR :search IS NULL OR :search = '') " +
         "AND (:categoryId IS NULL OR c.id = :categoryId) " +
         "AND (p.deleted IS NULL OR p.deleted = false)")
  Page<Product> searchProducts(
    @Param("search") String search,
    @Param("categoryId") Long categoryId,
    Pageable pageable
  );
}
