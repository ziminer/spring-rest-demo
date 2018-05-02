package com.turbonomic.demo.spring;

import com.turbonomic.demo.spring.Echo.EchoResponse;
import com.turbonomic.demo.spring.EchoServiceGrpc.EchoServiceImplBase;

import io.grpc.stub.StreamObserver;

public class EchoRpcService extends EchoServiceImplBase {

    public void echo(Echo.EchoRequest request,
                     StreamObserver<EchoResponse> responseObserver) {
        responseObserver.onNext(EchoResponse.newBuilder()
            .setEcho(request.getEcho())
            .build());
        responseObserver.onCompleted();
    }

}
