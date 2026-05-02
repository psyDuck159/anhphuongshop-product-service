package biz.anhld.anhphuongshop.productservice.dto.inventory;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReserveInventoryRequest {
    private Long productId;
    private Long orderId;
    private Integer quantity;
    private String idempotencyKey;
}