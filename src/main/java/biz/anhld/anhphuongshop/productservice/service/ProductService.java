package biz.anhld.anhphuongshop.productservice.service;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

import biz.anhld.anhphuongshop.productservice.entity.Product;
import biz.anhld.anhphuongshop.productservice.dto.ProductDTO;
import biz.anhld.anhphuongshop.productservice.repository.ProductRepository;
import biz.anhld.anhphuongshop.productservice.dto.ProductListItem;
import biz.anhld.anhphuongshop.productservice.dto.BasePageResponse; 
import biz.anhld.anhphuongshop.productservice.mapper.ProductMapper;
import biz.anhld.anhphuongshop.productservice.repository.CategoryRepository;
import biz.anhld.anhphuongshop.productservice.exception.BadRequestException;

import java.util.List;
import biz.anhld.anhphuongshop.productservice.entity.Category;

@Service
public class ProductService {
  private ProductRepository productRepository;
  private ProductMapper productMapper;
  private CategoryRepository categoryRepository;

  public ProductService(
    ProductRepository productRepository, 
    ProductMapper productMapper,
    CategoryRepository categoryRepository
  ) {
    this.productRepository = productRepository;
    this.productMapper = productMapper;
    this.categoryRepository = categoryRepository;
  }

  public void createProduct(ProductDTO productDTO) throws BadRequestException {
    Product product = productMapper.toEntity(productDTO);
    if (product.getId() != null) {
      product.setId(null); // Ensure ID is null for new entity
    }

    if (product.getCategory() != null) {
      Category category = categoryRepository.findById(product.getCategory().getId())
        .orElseThrow(() -> new BadRequestException("Invalid category ID"));
      product.setCategory(category);
    }
    productRepository.save(product);
  }

  public BasePageResponse<ProductListItem> getProducts(
    String search,
    Long categoryId,
    Pageable pageable
  ) {
    // Implementation goes here
    Page<Product> productPage = productRepository.searchProducts(search, categoryId, pageable);
    List<ProductListItem> items = productMapper.toProductListItems(productPage.getContent());

    return new BasePageResponse<>(
      productPage.getNumber(), 
      productPage.getSize(), 
      productPage.getTotalElements(), 
      productPage.getTotalPages(), 
      items);
  }

  public ProductDTO getProductBySlug(String slug) throws BadRequestException {
    // Implementation goes here
    Product product = productRepository.findBySlug(slug)
      .orElseThrow(() -> new BadRequestException("Product not found with slug: " + slug));
    return productMapper.toDto(product);
  }

  @org.springframework.transaction.annotation.Transactional
  public ProductDTO updateProduct(Long id, ProductDTO dto) throws BadRequestException {
    Product product = productRepository.findActiveById(id)
      .orElseThrow(() -> new BadRequestException("Product not found: " + id));

    product.setName(dto.getName());
    product.setPrice(dto.getPrice());
    product.setDescription(dto.getDescription());
    product.setStock(dto.getStock());
    product.setImage(dto.getImage());
    product.setSlug(dto.getSlug());

    if (dto.getCategory() != null && dto.getCategory().getId() != 0) {
      Category category = categoryRepository.findById(dto.getCategory().getId())
        .orElseThrow(() -> new BadRequestException("Invalid category ID"));
      product.setCategory(category);
    }

    productRepository.save(product);
    return productMapper.toDto(product);
  }

  @org.springframework.transaction.annotation.Transactional
  public void softDelete(Long id) throws BadRequestException {
    productRepository.findActiveById(id)
      .orElseThrow(() -> new BadRequestException("Product not found: " + id));
    productRepository.softDelete(id);
  }
}
