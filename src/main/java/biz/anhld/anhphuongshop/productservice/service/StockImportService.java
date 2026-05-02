package biz.anhld.anhphuongshop.productservice.service;

import biz.anhld.anhphuongshop.productservice.dto.BasePageResponse;
import biz.anhld.anhphuongshop.productservice.dto.StockImportDTO;
import biz.anhld.anhphuongshop.productservice.dto.StockImportRequest;
import biz.anhld.anhphuongshop.productservice.entity.Inventory;
import biz.anhld.anhphuongshop.productservice.entity.Product;
import biz.anhld.anhphuongshop.productservice.entity.StockImport;
import biz.anhld.anhphuongshop.productservice.entity.StockImportItem;
import biz.anhld.anhphuongshop.productservice.exception.BadRequestException;
import biz.anhld.anhphuongshop.productservice.mapper.StockImportMapper;
import biz.anhld.anhphuongshop.productservice.repository.InventoryRepository;
import biz.anhld.anhphuongshop.productservice.repository.ProductRepository;
import biz.anhld.anhphuongshop.productservice.repository.StockImportRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class StockImportService {

    private final StockImportRepository stockImportRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final StockImportMapper stockImportMapper;

    public StockImportService(
        StockImportRepository stockImportRepository,
        ProductRepository productRepository,
        InventoryRepository inventoryRepository,
        StockImportMapper stockImportMapper
    ) {
        this.stockImportRepository = stockImportRepository;
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
        this.stockImportMapper = stockImportMapper;
    }

    @Transactional
    public StockImportDTO createImport(StockImportRequest request, String createdBy) throws BadRequestException {
        StockImport stockImport = new StockImport();
        stockImport.setImportDate(LocalDateTime.now());
        stockImport.setNote(request.getNote());
        stockImport.setCreatedBy(createdBy);

        List<StockImportItem> items = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        for (var itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                .orElseThrow(() -> new BadRequestException("Product not found: " + itemRequest.getProductId()));

            StockImportItem item = new StockImportItem();
            item.setStockImport(stockImport);
            item.setProduct(product);
            item.setQuantity(itemRequest.getQuantity());
            item.setCostPrice(itemRequest.getCostPrice());
            items.add(item);

            Inventory inventory = inventoryRepository.findByProductIdWithLock(product.getId())
                .orElseThrow(() -> new BadRequestException("Inventory not found for product: " + product.getId()));
            inventory.setQuantityTotal(inventory.getQuantityTotal() + itemRequest.getQuantity());
            inventory.setQuantityAvailable(inventory.getQuantityAvailable() + itemRequest.getQuantity());
            inventory.setUpdatedAt(now);
            inventoryRepository.save(inventory);
        }

        stockImport.setItems(items);
        StockImport saved = stockImportRepository.save(stockImport);
        return stockImportMapper.toDTO(saved);
    }

    public BasePageResponse<StockImportDTO> getImports(Pageable pageable) {
        Page<StockImport> page = stockImportRepository.findAllByOrderByImportDateDesc(pageable);
        List<StockImportDTO> data = page.getContent().stream()
            .map(stockImportMapper::toDTO)
            .toList();
        return new BasePageResponse<>(page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages(), data);
    }

    public StockImportDTO getImportById(Long id) throws BadRequestException {
        StockImport stockImport = stockImportRepository.findById(id)
            .orElseThrow(() -> new BadRequestException("Stock import not found: " + id));
        return stockImportMapper.toDTO(stockImport);
    }
}
