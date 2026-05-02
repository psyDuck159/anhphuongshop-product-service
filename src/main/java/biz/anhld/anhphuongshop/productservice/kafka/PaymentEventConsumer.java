package biz.anhld.anhphuongshop.productservice.kafka;

import biz.anhld.anhphuongshop.productservice.service.InventoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventConsumer {

    private final InventoryService inventoryService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "anhphuongshop-payment-topic", groupId = "${spring.kafka.consumer.group-id}")
    public void onPaymentEvent(@Payload String message) {
        try {
            Map<?, ?> event = objectMapper.readValue(message, Map.class);
            String type = (String) event.get("type");
            Long orderId = ((Number) event.get("orderId")).longValue();

            if ("payment.succeed".equals(type)) {
                log.info("Confirming reservations for orderId={}", orderId);
                inventoryService.confirmByOrderId(orderId);
            }
            // payment.fail compensation (release) is handled by order-service
        } catch (Exception e) {
            log.error("Failed to process payment event: {} - {}", message, e.getMessage());
        }
    }
}
