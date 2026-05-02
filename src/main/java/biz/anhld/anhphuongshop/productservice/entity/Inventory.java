package biz.anhld.anhphuongshop.productservice.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.OneToOne;
import jakarta.persistence.JoinColumn;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "inventory", schema = "productsvc", indexes={
    @jakarta.persistence.Index(name = "idx_inventory_product_id", columnList = "product_id", unique=true)
})
@Getter @Setter
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "productsvc.inventory_id_seq")
    private Long id;

    // @Column(name = "product_id")
    // private Long productId;

    @OneToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    private Product product;

    @Column(name = "quantity_total")
    private Integer quantityTotal;

    @Column(name = "quantity_reserved")
    private Integer quantityReserved;

    @Column(name = "quantity_available")
    private Integer quantityAvailable;

    @Column(name = "quantity_sold")
    private Integer quantitySold;

    @Column(name = "low_stock_threshold")
    private Integer lowStockThreshold;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}