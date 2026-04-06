package biz.anhld.anhphuongshop.productservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "stock_imports", schema = "productsvc")
@Getter
@Setter
public class StockImport {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private LocalDateTime importDate;

    private String note;

    private String createdBy;

    @OneToMany(mappedBy = "stockImport", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<StockImportItem> items;
}
