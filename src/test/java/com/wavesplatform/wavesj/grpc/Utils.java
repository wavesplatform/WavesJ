package com.wavesplatform.wavesj.grpc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableList;
import com.wavesplatform.api.grpc.BlocksApiOuterClass;
import com.wavesplatform.protobuf.block.BlockOuterClass;
import com.wavesplatform.wavesj.Base58;
import com.wavesplatform.wavesj.grpc.entities.Block;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;

class Utils {

    private static File grpcFile = new File("grpc.txt");

    private static File restFile = new File("rest.json");

    static void initLogs() throws IOException {
        Files.deleteIfExists(grpcFile.toPath());
        Files.deleteIfExists(restFile.toPath());
    }

    static void print(BlocksApiOuterClass.BlockWithHeight block) throws IOException {
        List<String> lines = ImmutableList.of(
                "Height: " + block.getHeight(),
                "Signature: " + Base58.encode(block.getBlock().getSignature().toByteArray()));
        Files.write(grpcFile.toPath(), lines, UTF_8, APPEND, CREATE);
        print(block.getBlock());
    }

    private static void print(BlockOuterClass.Block block) throws IOException {
        List<String> lines = Collections.singletonList("TransactionCount: " + block.getTransactionsCount());
        Files.write(grpcFile.toPath(), lines, UTF_8, APPEND, CREATE);
        print(block.getHeader());
    }

    private static void print(BlockOuterClass.Block.Header header) throws IOException {
        String generatorPublKey = Base58.encode(header.getGenerator().toByteArray());
        List<String> lines = ImmutableList.of(
                "header:",
                "\tchainId: " + header.getChainId(),
                "\treference: " + Base58.encode(header.getReference().toByteArray()),
                "\tbaseTarget: " + header.getBaseTarget(),
                "\tgenerationSignature: " + Base58.encode(header.getGenerationSignature().toByteArray()),
                "\ttimestamp: " + header.getTimestamp(),
                "\tversion: " + header.getVersion(),
                "\tgenerator: " + generatorPublKey + " | " + RESTTestInterfaceImpl.getGeneratorByPublKey(generatorPublKey),
                "\tfeatures: " + header.getFeatureVotesList());
        Files.write(grpcFile.toPath(), lines, UTF_8, APPEND, CREATE);
    }

    static Block getBlockFromJson(String jsonBlock) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(jsonBlock, Block.class);
    }

    static List<Block> getBlocksFromJson(String jsonBlocks) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return Arrays.asList(objectMapper.readValue(jsonBlocks, Block[].class));
    }

    static boolean checkBlocksEquals(Block block, BlocksApiOuterClass.BlockWithHeight blockWithHeight) throws IOException {
        String jsonAddress = RESTTestInterfaceImpl.getGeneratorByPublKey(Base58.encode(blockWithHeight.getBlock().getHeader().getGenerator().toByteArray()));
        ObjectNode node = new ObjectMapper().readValue(jsonAddress, ObjectNode.class);
        return (block.getHeight() == blockWithHeight.getHeight()
                && block.getSignature().equals(Base58.encode(blockWithHeight.getBlock().getSignature().toByteArray()))
                && block.getTransactionCount() == blockWithHeight.getBlock().getTransactionsCount()
                && block.getReference().equals(Base58.encode(blockWithHeight.getBlock().getHeader().getReference().toByteArray()))
                && block.getTimestamp() == blockWithHeight.getBlock().getHeader().getTimestamp()
                && block.getVersion() == blockWithHeight.getBlock().getHeader().getVersion()
                && ("\"" + block.getGenerator() + "\"").equals(node.get("address").toString())
                && block.getBaseTarget() == blockWithHeight.getBlock().getHeader().getBaseTarget()
                && block.getGenerationSignature().equals(Base58.encode(blockWithHeight.getBlock().getHeader().getGenerationSignature().toByteArray())));
    }

    static File getRestFile() {
        return restFile;
    }

    static File getGrpcFile() {
        return grpcFile;
    }
}
