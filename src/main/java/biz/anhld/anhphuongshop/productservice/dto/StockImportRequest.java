package biz.anhld.anhphuongshop.productservice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class StockImportRequest {
    private String note;

    @NotEmpty(message = "Import must have at least one item")
    @Valid
    private List<StockImportItemRequest> items;
}
