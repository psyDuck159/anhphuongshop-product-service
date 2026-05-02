package biz.anhld.anhphuongshop.productservice.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "reservations", indexes = {
    @Index(name = "idx_reservation_idempotency_key", columnList = "idempotency_key", unique = true),
    @Index(name = "idx_reservation_order_id", columnList = "order_id"),
    @Index(name = "idx_reservation_status_exp", columnList = "status, expires_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO, generator = "productsvc.reservations_id_seq")
  private Long id;

  @Column(name = "inventory_id")
  private Long inventoryId;

  @Column(name = "order_id")
  private Long orderId;

  @Column(name = "idempotency_key")
  private String idempotencyKey;

  private Integer quantity;

  private String status; // PENDING, CONFIRMED, RELEASED, EXPIRED

  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "expires_at")
  private LocalDateTime expiresAt;

  @Column(name = "confirmed_at")
  private LocalDateTime confirmedAt;

  @Column(name = "released_at")
  private LocalDateTime releasedAt;
}
