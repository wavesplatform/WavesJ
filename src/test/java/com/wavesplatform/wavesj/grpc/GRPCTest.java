package com.wavesplatform.wavesj.grpc;

import com.google.protobuf.ByteString;
import com.wavesplatform.api.grpc.BlocksApiOuterClass;
import com.wavesplatform.wavesj.Base58;
import com.wavesplatform.wavesj.grpc.entities.Block;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;

public class GRPCTest {

    static String firstBlockReference = "67rpwLCuS5DGA8KGZXKsVQ7dnPb9goRLoKfgGbLfQg9WoLUgNY77E2jT11fem3coV9nAkguBACzrU1iyZM4B8roQ";

    static List<String> setPositiveIds = Arrays.asList("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");

    // max height = 87
    List<Integer[]> fromTo = Arrays.asList(
            new Integer[]{-90, -80},
            new Integer[]{-87, -50},
            new Integer[]{-87, 0},
            new Integer[]{-40, 1},
            new Integer[]{-30, 20},
            new Integer[]{-30, 57},
            new Integer[]{-29, 59},
            new Integer[]{-10, 0},
            new Integer[]{-10, 87},
            new Integer[]{0, 1},
            new Integer[]{0, 5},
            new Integer[]{1, 20},
            new Integer[]{24, 80},
            new Integer[]{60, 87},
            new Integer[]{40, 90},
            new Integer[]{87, 88},
            new Integer[]{86, 0},
            new Integer[]{80, -1},
            new Integer[]{80, -50},
            new Integer[]{40, -47});


    public static void main(String[] args) {
        ByteString generator = GRPCTestInterfaceImpl.getBlockByHeight(2, true).getBlock().getHeader().getGenerator();
        GRPCTestInterfaceImpl.getBlockByGenerator(generator, true)
                .forEachRemaining(block -> {
                    try {
                        Utils.print(block);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }

    private static void checkBlockDataUpdate() throws IOException {
        Utils.initLogs();
        final BlocksApiOuterClass.BlockWithHeight[] block = {GRPCTestInterfaceImpl.getBlockByHeight(GRPCTestInterfaceImpl.getHeight().getValue(), true)};
        Utils.print(block[0]);
        Runnable helloRunnable = () -> {
            BlocksApiOuterClass.BlockWithHeight lastBlock = GRPCTestInterfaceImpl.getBlockByHeight(GRPCTestInterfaceImpl.getHeight().getValue(), true);
            if (!block[0].equals(lastBlock)) {
                try {
                    Utils.print(lastBlock);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            block[0] = lastBlock;
        };

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(helloRunnable, 0, 100, TimeUnit.MILLISECONDS);
    }

    private static void ckeckEqualsByHeightRange(int from, int to) throws Exception {
        Utils.initLogs();
        List<String> lines = Collections.singletonList("-------------------------------------------------------");
        boolean isSuccess = true;
        for (int height = from; height <= to; height++) {
            try {
                Files.write(Utils.getGrpcFile().toPath(), lines, UTF_8, APPEND, CREATE);
                BlocksApiOuterClass.BlockWithHeight blockWithHeight = GRPCTestInterfaceImpl.getBlockByHeight(height, true);
                Utils.print(blockWithHeight);
                String jsonBlock = RESTTestInterfaceImpl.getBlockByHeight(height);
                Files.write(Utils.getRestFile().toPath(), Collections.singletonList(jsonBlock), UTF_8, APPEND, CREATE);
                if (!Utils.checkBlocksEquals(Utils.getBlockFromJson(jsonBlock), blockWithHeight)) {
                    throw new Exception("values are not equal on height: " + height);
                }
            } catch (Exception e) {
                isSuccess = false;
                e.printStackTrace();
            }
        }
        if (!isSuccess) {
            throw new Exception("Failure");
        }
    }

    private static void ckeckEqualsById(List<String> ids) throws Exception {
        Utils.initLogs();
        List<String> lines = Collections.singletonList("-------------------------------------------------------");
        for (String id : ids) {
            Files.write(Utils.getGrpcFile().toPath(), lines, UTF_8, APPEND, CREATE);
            BlocksApiOuterClass.BlockWithHeight blockWithHeight = GRPCTestInterfaceImpl.getBlockById(ByteString.copyFrom(Base58.decode(id)), true);
            Utils.print(blockWithHeight);
            String jsonBlock = RESTTestInterfaceImpl.getBlockById(id);
            Files.write(Utils.getRestFile().toPath(), Collections.singletonList(jsonBlock), UTF_8, APPEND, CREATE);
            if (!Utils.checkBlocksEquals(Utils.getBlockFromJson(jsonBlock), blockWithHeight)) {
                throw new Exception("values are not equal on id: " + id);
            }
        }
    }

    private static void ckeckEqualsByRef(List<String> references) throws Exception {
        Utils.initLogs();
        List<String> lines = Collections.singletonList("-------------------------------------------------------");
        for (String ref : references) {
            Files.write(Utils.getGrpcFile().toPath(), lines, UTF_8, APPEND, CREATE);
            BlocksApiOuterClass.BlockWithHeight blockWithHeight = GRPCTestInterfaceImpl.getBlockByReference(ByteString.copyFrom(Base58.decode(ref)), true);
            Utils.print(blockWithHeight);
            String jsonBlock = RESTTestInterfaceImpl.getBlockByRef(ref);
            Files.write(Utils.getRestFile().toPath(), Collections.singletonList(jsonBlock), UTF_8, APPEND, CREATE);
            if (!Utils.checkBlocksEquals(Utils.getBlockFromJson(jsonBlock), blockWithHeight)) {
                throw new Exception("values are not equal on id: " + ref);
            }
        }
    }

    // постоянная высота блокчейна 87 блоков
    private static void checkHeightRange(int from, int to) throws Exception {
        int height = 87;
        List<BlocksApiOuterClass.BlockWithHeight> blocks = new ArrayList<>();
        GRPCTestInterfaceImpl.getBlockRangeByFromTo(from, to, true).forEachRemaining(blocks::add);
        int posFrom = from, posTo = to;
        if (from <= 0) {
            posFrom = height - from;
        }
        if (to <= 0) {
            posTo = height - to;
        }

        List<Block> blocksFromRest = null;
        try {
            blocksFromRest = Utils.getBlocksFromJson(RESTTestInterfaceImpl.getBlocksByRange(posFrom, posTo));
            if (blocks.size() != blocksFromRest.size()) {
                throw new Exception("Blocks aren't equals on " + from + ":" + to);
            }
            for (int i = 0; i < blocks.size(); i++) {
                if (!Utils.checkBlocksEquals(blocksFromRest.get(i), blocks.get(i))) {
                    throw new Exception("Blocks aren't equals on " + from + ":" + to);
                }
            }
        } catch (Exception ex) {
            if (blocks.size() != 0) {
                throw new Exception("Blocks aren't equals on " + from + ":" + to);
            }
        }
    }
}
