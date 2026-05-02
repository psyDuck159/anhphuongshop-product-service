package biz.anhld.anhphuongshop.productservice.dto.inventory;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReserveInventoryResponse {
    private Long reservationId;
    private Long inventoryId;
    private Long orderId;
    private String idempotencyKey;
    private Integer quantity;
    private String status;
}
