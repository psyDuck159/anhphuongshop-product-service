package biz.anhld.anhphuongshop.productservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StockImportItemRequest {
    @NotNull(message = "Product ID is required")
    private Long productId;

    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;

    @Min(value = 0, message = "Cost price must be non-negative")
    private long costPrice;
}
