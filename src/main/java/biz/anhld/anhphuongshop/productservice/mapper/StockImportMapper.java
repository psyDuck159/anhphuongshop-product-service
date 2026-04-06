package biz.anhld.anhphuongshop.productservice.mapper;

import biz.anhld.anhphuongshop.productservice.dto.StockImportDTO;
import biz.anhld.anhphuongshop.productservice.dto.StockImportItemDTO;
import biz.anhld.anhphuongshop.productservice.entity.StockImport;
import biz.anhld.anhphuongshop.productservice.entity.StockImportItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StockImportMapper {

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    StockImportItemDTO toItemDTO(StockImportItem item);

    @Mapping(target = "totalItems", expression = "java(entity.getItems() == null ? 0 : entity.getItems().stream().mapToInt(biz.anhld.anhphuongshop.productservice.entity.StockImportItem::getQuantity).sum())")
    @Mapping(source = "items", target = "items")
    StockImportDTO toDTO(StockImport entity);
}
