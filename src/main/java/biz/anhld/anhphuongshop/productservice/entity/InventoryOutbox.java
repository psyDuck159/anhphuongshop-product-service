package biz.anhld.anhphuongshop.productservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "inventory_outbox", schema = "productsvc")
@Getter @Setter
public class InventoryOutbox {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "productsvc.inventory_outbox_id_seq")
    private Long id;
    @Column(name = "aggregate_id")
    private Long aggregateId; // ID của Inventory liên quan đến sự kiện
    @Column(name = "event_type")
    private String eventType; // e.g., "RESERVATION_CREATED", "RESERVATION_CONFIRMED", "RESERVATION_CANCELED"
    @Column(name = "payload", columnDefinition = "JSON")
    private String payload; // JSON or detailed description of the event
    @Column(name = "timestamp")
    private Long timestamp;

}