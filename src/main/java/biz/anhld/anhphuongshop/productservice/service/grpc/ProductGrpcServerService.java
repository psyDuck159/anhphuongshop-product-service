package biz.anhld.anhphuongshop.productservice.service.grpc;

import biz.anhld.anhphuongshop.grpc.*;
import biz.anhld.anhphuongshop.productservice.entity.Inventory;
import biz.anhld.anhphuongshop.productservice.entity.Product;
import biz.anhld.anhphuongshop.productservice.exception.BadRequestException;
import biz.anhld.anhphuongshop.productservice.repository.InventoryRepository;
import biz.anhld.anhphuongshop.productservice.repository.ProductRepository;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@GrpcService
@RequiredArgsConstructor
public class ProductGrpcServerService extends ProductGrpcServiceGrpc.ProductGrpcServiceImplBase {
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;

    @Override
    public void getProductsBatch(
            ProductBatchRequest request,
            StreamObserver<ProductBatchResponse> responseObserver
    ) {
        if (request.getProductIdsList().isEmpty()) {
            responseObserver.onNext(ProductBatchResponse.newBuilder().build());
            responseObserver.onCompleted();
            return;
        }

        List<Long> ids = request.getProductIdsList();
        List<Product> products = productRepository.findByIdIn(ids);

        List<ProductResponse> productResponses = products.stream().map(p -> {
                    ProductResponse.Builder builder = ProductResponse.newBuilder()
                            .setProductId(p.getId())
                            .setName(p.getName())
                            .setPrice(p.getPrice());
                    if (p.getImage() != null) {
                        builder.setImage(p.getImage());
                    }
                    return builder.build();
                }
        ).collect(Collectors.toList());

        ProductBatchResponse response = ProductBatchResponse.newBuilder()
                .addAllProducts(productResponses)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Transactional
    @Override
    public void reduceStock(ReduceStockRequest request, StreamObserver<ReduceStockResponse> responseObserver) {
        LocalDateTime now = LocalDateTime.now();
        for (StockItem item : request.getItemsList()) {
            Inventory inventory = inventoryRepository.findByProductIdWithLock(item.getProductId())
                    .orElseThrow(() -> new BadRequestException("Inventory not found for product: " + item.getProductId()));
            if (inventory.getQuantityAvailable() < item.getQuantity()) {
                throw new BadRequestException("Insufficient stock for product: " + item.getProductId());
            }
            inventory.setQuantityAvailable(inventory.getQuantityAvailable() - item.getQuantity());
            inventory.setQuantitySold(inventory.getQuantitySold() + item.getQuantity());
            inventory.setUpdatedAt(now);
            inventoryRepository.save(inventory);
        }
        responseObserver.onNext(ReduceStockResponse.newBuilder().setSuccess(true).build());
        responseObserver.onCompleted();
    }
}
