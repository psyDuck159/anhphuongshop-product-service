package biz.anhld.anhphuongshop.productservice.service.grpc;

import biz.anhld.anhphuongshop.grpc.*;
import biz.anhld.anhphuongshop.productservice.entity.Product;
import biz.anhld.anhphuongshop.productservice.exception.BadRequestException;
import biz.anhld.anhphuongshop.productservice.repository.ProductRepository;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@GrpcService
@RequiredArgsConstructor
public class ProductGrpcServerService extends ProductGrpcServiceGrpc.ProductGrpcServiceImplBase {
    private final ProductRepository productRepository;


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

        // 1. Lấy danh sách ID từ request
        List<Long> ids = request.getProductIdsList();
        List<Product> products = productRepository.findByIdIn(ids);

        // 2. Giả sử lấy dữ liệu từ DB (ở đây tôi tạo dummy data)
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

        // 3. Đóng gói vào BatchResponse
        ProductBatchResponse response = ProductBatchResponse.newBuilder()
                .addAllProducts(productResponses) // Dùng addAll để thêm cả danh sách
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Transactional
    @Override
    public void reduceStock(ReduceStockRequest request, StreamObserver<ReduceStockResponse> responseObserver) {
        for (StockItem item : request.getItemsList()) {
            int updated = productRepository.decreaseStock(item.getProductId(), item.getQuantity());
            if (updated == 0) {
                throw new BadRequestException("Insufficient stock for product id: " + item.getProductId());
            }
        }
        responseObserver.onNext(ReduceStockResponse.newBuilder().setSuccess(true).build());
        responseObserver.onCompleted();
    }
}
