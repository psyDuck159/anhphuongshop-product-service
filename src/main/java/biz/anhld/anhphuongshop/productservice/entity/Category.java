package biz.anhld.anhphuongshop.productservice.entity;

import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

@Entity
@Table(name = "categories", schema = "productsvc")
@Getter @Setter
public class Category {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO, generator = "productsvc.categories_id_seq")
  private Long id;
  @NotBlank(message = "Name is mandatory")
  private String name;
  private String image;
}
