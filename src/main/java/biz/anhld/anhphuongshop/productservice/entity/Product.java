package biz.anhld.anhphuongshop.productservice.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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

    private Integer stock;

    private String image;

    private String slug;

    @ManyToOne(fetch = jakarta.persistence.FetchType.EAGER, optional = true)
    private Category category;

}
