package biz.anhld.anhphuongshop.productservice.service;

import java.util.List;

import org.springframework.stereotype.Service;
import biz.anhld.anhphuongshop.productservice.entity.Category;
import biz.anhld.anhphuongshop.productservice.repository.CategoryRepository;

@Service
public class CategoryService {
  private CategoryRepository categoryRepository;

  public CategoryService(CategoryRepository categoryRepository) {
    this.categoryRepository = categoryRepository;
  }

  public void createCategory(Category category) {
    categoryRepository.save(category);  
  }

  public List<Category> getAllCategories() {
    return categoryRepository.findAll();
  }


}
