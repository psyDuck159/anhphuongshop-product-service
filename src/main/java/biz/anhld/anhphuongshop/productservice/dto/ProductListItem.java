package biz.anhld.anhphuongshop.productservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ProductListItem {
  private long id;

	private String name;

	private long price;

  private int stock;

  private String image;

  private String slug;

  private String categoryName;
}
