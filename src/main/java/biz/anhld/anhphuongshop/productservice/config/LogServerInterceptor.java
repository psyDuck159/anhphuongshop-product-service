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

        logger.info("gRPC call: {}", call.getMethodDescriptor().getFullMethodName());

        return next.startCall(new ForwardingServerCall.SimpleForwardingServerCall<>(call) {
            @Override
            public void close(Status status, Metadata trailers) {
                if (!status.isOk()) {
                    logger.warn("gRPC call {} failed: {} - {}",
                            call.getMethodDescriptor().getFullMethodName(),
                            status.getCode(), status.getDescription());
                }
                super.close(status, trailers);
            }
        }, headers);
    }
}
