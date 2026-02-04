package biz.anhld.anhphuongshop.productservice.config;

import io.grpc.*;
import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@GrpcGlobalServerInterceptor // Annotation này giúp Spring tự động đăng ký interceptor cho mọi gRPC service
public class LogServerInterceptor implements ServerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(LogServerInterceptor.class);

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call,
            Metadata headers,
            ServerCallHandler<ReqT, RespT> next) {

        // 1. Log tên phương thức được gọi
        logger.info("Service: {}, Method: {}",
                call.getMethodDescriptor().getServiceName(),
                call.getMethodDescriptor().getFullMethodName());

        // 2. Log Header (nếu cần xem Token truyền lên)
        logger.info("Headers received: {}", headers.toString());

        return next.startCall(call, headers);
    }
}
