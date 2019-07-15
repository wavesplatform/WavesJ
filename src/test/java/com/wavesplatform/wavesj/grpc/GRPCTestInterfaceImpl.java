package com.wavesplatform.wavesj.grpc;

import com.google.protobuf.ByteString;
import com.google.protobuf.Empty;
import com.google.protobuf.UInt32Value;
import com.wavesplatform.api.grpc.BlocksApiOuterClass;

import java.util.Iterator;

class GRPCTestInterfaceImpl extends BaseApi {

    static UInt32Value getHeight() {
        return blocksApiBlockingStub.getCurrentHeight(Empty.newBuilder().build());
    }


    static BlocksApiOuterClass.BlockWithHeight getBlockByHeight(int height, boolean includeTransactions) {
        return blocksApiBlockingStub.getBlock(BlocksApiOuterClass.BlockRequest.newBuilder().setHeight(height).setIncludeTransactions(includeTransactions).build());
    }

    static BlocksApiOuterClass.BlockWithHeight getBlockById(ByteString id, boolean includeTransactions) {
        return blocksApiBlockingStub.getBlock(BlocksApiOuterClass.BlockRequest.newBuilder().setBlockId(id).setIncludeTransactions(includeTransactions).build());
    }

    static BlocksApiOuterClass.BlockWithHeight getBlockByReference(ByteString reference, boolean includeTransactions) {
        return blocksApiBlockingStub.getBlock(BlocksApiOuterClass.BlockRequest.newBuilder().setReference(reference).setIncludeTransactions(includeTransactions).build());
    }

    static Iterator<BlocksApiOuterClass.BlockWithHeight> getBlockRangeByFromTo(int from, int to, boolean includeTransactions) {
        return blocksApiBlockingStub.getBlockRange(BlocksApiOuterClass.BlockRangeRequest.newBuilder().setFromHeight(from).setToHeight(to).setIncludeTransactions(includeTransactions).build());
    }

    static Iterator<BlocksApiOuterClass.BlockWithHeight> getBlockByGenerator(ByteString generator, boolean includeTransaction) {
        return blocksApiBlockingStub.getBlockRange(BlocksApiOuterClass.BlockRangeRequest.newBuilder().setGenerator(generator).setIncludeTransactions(includeTransaction).build());
    }

}
