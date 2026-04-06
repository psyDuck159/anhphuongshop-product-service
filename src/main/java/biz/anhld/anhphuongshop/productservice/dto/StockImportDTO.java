package biz.anhld.anhphuongshop.productservice.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class StockImportDTO {
    private Long id;
    private LocalDateTime importDate;
    private String note;
    private String createdBy;
    private int totalItems;
    private List<StockImportItemDTO> items;
}
