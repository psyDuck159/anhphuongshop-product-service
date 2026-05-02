package biz.anhld.anhphuongshop.productservice.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.OneToOne;
import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "products", schema = "productsvc")
@Getter
@Setter
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "productsvc.products_id_seq")
    private Long id;

    private String name;

    private Long price;

    private String description;

    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Inventory inventory;

    private String image;

    private String slug;

    private Boolean deleted = false;

    @ManyToOne(fetch = jakarta.persistence.FetchType.EAGER, optional = true)
    private Category category;

}
