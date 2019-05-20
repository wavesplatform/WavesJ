package com.wavesplatform.wavesj;

import com.wavesplatform.api.grpc.BlocksApiGrpc;
import com.wavesplatform.api.grpc.BlocksApiOuterClass;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class GRPCTest {
    public static void main(String[] args) {
        final ManagedChannel channel = ManagedChannelBuilder.forAddress("mainnet-aws-fr-3.wavesnodes.com", 6870)
                .usePlaintext()
                .build();

        final BlocksApiGrpc.BlocksApiBlockingStub blocksApiBlockingStub = BlocksApiGrpc.newBlockingStub(channel);

        final BlocksApiOuterClass.BlockWithHeight block = blocksApiBlockingStub.getBlock(BlocksApiOuterClass.BlockRequest.newBuilder().setHeight(-1).build());
        System.out.println(block);
    }
}
