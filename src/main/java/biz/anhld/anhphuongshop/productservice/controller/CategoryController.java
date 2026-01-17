package biz.anhld.anhphuongshop.productservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import biz.anhld.anhphuongshop.productservice.entity.Category;
import biz.anhld.anhphuongshop.productservice.service.CategoryService;
import jakarta.validation.Valid;
import jakarta.ws.rs.core.Response;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {
  private CategoryService categoryService;

  public CategoryController(CategoryService categoryService) {
    this.categoryService = categoryService;
  }

  @PreAuthorize("hasAuthority('ADMIN')")
  @PostMapping
  public ResponseEntity<?> createCategory(@Valid @RequestBody Category category) {
    categoryService.createCategory(category);
    return ResponseEntity.ok().build();
  }

  @GetMapping
  public ResponseEntity<?> getAllCategories() {
    return ResponseEntity.ok(categoryService.getAllCategories());
  }

}
