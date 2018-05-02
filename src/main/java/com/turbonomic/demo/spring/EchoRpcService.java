package com.turbonomic.demo.spring;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.turbonomic.demo.spring.EchoOuterClass.*;
import com.turbonomic.demo.spring.EchoServiceGrpc.EchoServiceImplBase;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;

public class EchoRpcService extends EchoServiceImplBase {

    private final Map<Long, Echo> echoMap = Collections.synchronizedMap(new HashMap<>());

    public void getEcho(GetEchoRequest request,
                        StreamObserver<GetEchoResponse> responseObserver) {
        final GetEchoResponse.Builder respBuilder = GetEchoResponse.newBuilder();
        final Echo echo = echoMap.get(request.getId());
        if (echo != null) {
            respBuilder.setEcho(echo);
        }
        responseObserver.onNext(respBuilder.build());
        responseObserver.onCompleted();
    }

    public void createEcho(CreateEchoRequest request,
                           StreamObserver<CreateEchoResponse> responseObserver) {
        if (echoMap.containsKey(request.getId())) {
            responseObserver.onError(Status.ALREADY_EXISTS.withDescription("Duplicate id: " +
                    request.getId()).asException());
        } else {
            final Echo echo = Echo.newBuilder()
                    .setId(request.getId())
                    .setContent(request.getContent())
                    .build();
            echoMap.put(request.getId(), echo);
            responseObserver.onNext(CreateEchoResponse.newBuilder()
                    .setEcho(echo)
                    .build());
        }
        responseObserver.onCompleted();
    }

    public void updateEcho(UpdateEchoRequest request,
                           StreamObserver<UpdateEchoResponse> responseObserver) {
        final UpdateEchoResponse.Builder respBuilder = UpdateEchoResponse.newBuilder();
        final Echo newEcho = echoMap.computeIfPresent(request.getId(),
            (id, oldEcho) -> oldEcho.toBuilder()
                .setContent(request.getNewContent())
                .build());
        if (newEcho != null) {
            respBuilder.setEcho(newEcho);
        }
        responseObserver.onNext(respBuilder.build());
        responseObserver.onCompleted();
    }

    public void deleteEcho(DeleteEchoRequest request,
                           StreamObserver<DeleteEchoResponse> responseObserver) {
        final DeleteEchoResponse.Builder respBuilder = DeleteEchoResponse.newBuilder();
        final Echo echo = echoMap.remove(request.getId());
        if (echo != null) {
            respBuilder.setEcho(echo);
        }
        responseObserver.onNext(respBuilder.build());
        responseObserver.onCompleted();
    }

}
