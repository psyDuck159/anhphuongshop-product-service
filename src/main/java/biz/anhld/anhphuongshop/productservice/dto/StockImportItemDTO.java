package biz.anhld.anhphuongshop.productservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StockImportItemDTO {
    private Long id;
    private Long productId;
    private String productName;
    private int quantity;
    private long costPrice;
}
