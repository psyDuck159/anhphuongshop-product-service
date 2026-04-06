package biz.anhld.anhphuongshop.productservice.controller;

import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import biz.anhld.anhphuongshop.productservice.service.ProductService;
import jakarta.validation.Valid;
import jakarta.ws.rs.core.Response;
import biz.anhld.anhphuongshop.productservice.dto.ProductDTO;
import biz.anhld.anhphuongshop.productservice.dto.ProductListItem;
import biz.anhld.anhphuongshop.productservice.dto.BasePageResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
  private ProductService productService;

  public ProductController(ProductService productService) {
    this.productService = productService;
  }

  @PreAuthorize("hasAuthority('ADMIN')")
  @PostMapping
  public ResponseEntity<?> createProduct(@Valid @RequestBody ProductDTO productDTO) {
    // Implementation goes here
    productService.createProduct(productDTO);
    return ResponseEntity.ok().build();
  }

  @GetMapping
  public ResponseEntity<BasePageResponse<ProductListItem>> getProducts(
    @RequestParam(name = "search", required = false) String search,
    @RequestParam(name = "categoryId", required = false) Long categoryId,
    @PageableDefault Pageable pageable
  ) {
    return ResponseEntity.ok(productService.getProducts(search, categoryId, pageable));
  }

  @GetMapping("/{slug}")
  public ResponseEntity<ProductDTO> getProductBySlug(@PathVariable String slug) {
    return ResponseEntity.ok(productService.getProductBySlug(slug));
  }

  @PreAuthorize("hasAuthority('ADMIN')")
  @PutMapping("/{id}")
  public ResponseEntity<ProductDTO> updateProduct(
      @PathVariable Long id,
      @Valid @RequestBody ProductDTO productDTO
  ) throws Exception {
    return ResponseEntity.ok(productService.updateProduct(id, productDTO));
  }

  @PreAuthorize("hasAuthority('ADMIN')")
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteProduct(@PathVariable Long id) throws Exception {
    productService.softDelete(id);
    return ResponseEntity.noContent().build();
  }
}
