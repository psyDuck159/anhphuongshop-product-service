package biz.anhld.anhphuongshop.productservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "stock_import_items", schema = "productsvc")
@Getter
@Setter
public class StockImportItem {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_import_id")
    private StockImport stockImport;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id")
    private Product product;

    private Integer quantity;

    private Long costPrice;
}
