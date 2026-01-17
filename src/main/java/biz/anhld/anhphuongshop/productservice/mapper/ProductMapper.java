package biz.anhld.anhphuongshop.productservice.mapper;

import biz.anhld.anhphuongshop.productservice.dto.ProductDTO;
import biz.anhld.anhphuongshop.productservice.entity.Product;
import biz.anhld.anhphuongshop.productservice.dto.ProductListItem;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper extends BaseMapper<Product, ProductDTO> {

  @Mapping(target = "categoryName", source = "category.name")
  List<ProductListItem> toProductListItems(List<Product> products);
}
