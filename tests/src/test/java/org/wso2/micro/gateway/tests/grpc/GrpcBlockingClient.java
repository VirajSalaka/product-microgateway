package org.wso2.micro.gateway.tests.grpc;

import io.grpc.Channel;
import io.grpc.StatusRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GrpcBlockingClient {
    private static final Logger log = LoggerFactory.getLogger(GrpcServer.class);
    private final TestServiceGrpc.TestServiceBlockingStub blockingStub;

    public GrpcBlockingClient(Channel channel) {
        blockingStub = TestServiceGrpc.newBlockingStub(channel);
    }

    public String testCall() {
        TestRequest request = TestRequest.newBuilder().setTestReqString("grpc-call").build();
        TestResponse response;
        try{
            response = blockingStub.testCall(request);
        } catch (StatusRuntimeException e) {
            log.error("RPC failed: {0}", e.getStatus());
            return "error";
        }
    }
}
