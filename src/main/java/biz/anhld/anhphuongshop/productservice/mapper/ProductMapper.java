package biz.anhld.anhphuongshop.productservice.mapper;

import biz.anhld.anhphuongshop.productservice.dto.ProductDTO;
import biz.anhld.anhphuongshop.productservice.entity.Product;
import biz.anhld.anhphuongshop.productservice.dto.ProductListItem;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper extends BaseMapper<Product, ProductDTO> {

  @Mapping(source = "category.name", target = "categoryName")
    // Nếu bạn muốn xử lý trường hợp category bị null để tránh NullPointerException:
    // @Mapping(source = "category.name", target = "categoryName", defaultValue = "Uncategorized")
  ProductListItem toListItem(Product product);

  List<ProductListItem> toProductListItems(List<Product> products);
}
