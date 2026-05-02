package biz.anhld.anhphuongshop.productservice.entity;

import java.time.LocalDateTime;
import java.util.Map;

import org.hibernate.annotations.Type;

import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "reservation_events", schema = "productsvc")
@Getter @Setter
public class ReservationEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "productsvc.reservation_events_id_seq")
    private Long id;
    @ManyToOne(optional = false)
    @JoinColumn(name = "reservation_id", nullable = false)  
    private Reservation reservation;
    @Column(name = "event_type")
    private String eventType; // CREATED, CONFIRMED, CANCELED
    @Type(JsonBinaryType.class) // Dùng Hypersistence để map JSONB vào Map hoặc POJO
    @Column(columnDefinition = "jsonb")
    private Map<String, Object>  payload; // JSON hoặc mô tả chi tiết sự kiện
    @Column(name = "published")
    private Boolean published; // Đánh dấu đã được xử lý và gửi đi
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "published_at")
    private LocalDateTime publishedAt;

}