package biz.anhld.anhphuongshop.productservice.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import biz.anhld.anhphuongshop.productservice.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
  
}
