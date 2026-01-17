package biz.anhld.anhphuongshop.productservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.List;

@Getter
@AllArgsConstructor
public class BasePageResponse<T> {
  private int page;
  private int size;
  private long totalElements;
  private int totalPages;
  private List<T> data;
  
}
