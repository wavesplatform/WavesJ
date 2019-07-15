package com.wavesplatform.wavesj.grpc;

import com.wavesplatform.api.grpc.BlocksApiGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

class BaseApi {

    private static final ManagedChannel channel = ManagedChannelBuilder.forAddress("127.0.0.1", 6870)
            .usePlaintext()
            .build();

    /*private static final ManagedChannel channel = ManagedChannelBuilder.forAddress("pool.testnet.wavesnodes.com", 6870)
            .usePlaintext()
            .build();*/

    static final BlocksApiGrpc.BlocksApiBlockingStub blocksApiBlockingStub = BlocksApiGrpc.newBlockingStub(channel);
}
