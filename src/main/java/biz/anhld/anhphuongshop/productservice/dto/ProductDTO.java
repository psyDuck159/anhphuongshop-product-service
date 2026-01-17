package biz.anhld.anhphuongshop.productservice.dto;

import lombok.Getter;
import lombok.Setter;
import biz.anhld.anhphuongshop.productservice.entity.Category;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@Getter @Setter
public class ProductDTO {
  private long id;

  @NotBlank(message = "Name is mandatory")
	private String name;

  @Min(value = 0, message = "Price must be non-negative")
	private long price;

	private String description;

  @Min(value = 0, message = "Stock must be non-negative")
  private int stock;

  private String image;

  @NotBlank(message = "Slug is mandatory")
  private String slug;

  private Category category;
}
