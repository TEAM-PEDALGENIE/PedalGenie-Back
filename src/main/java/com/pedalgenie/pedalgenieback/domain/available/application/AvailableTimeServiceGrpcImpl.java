package com.pedalgenie.pedalgenieback.domain.available.application;

import com.pedalgenie.pedalgenieback.domain.available.grpc.AvailableTimeServiceGrpc;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
@RequiredArgsConstructor
public class AvailableTimeServiceGrpcImpl extends AvailableTimeServiceGrpc.AvailableTimeServiceImplBase {

}
