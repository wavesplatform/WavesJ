package com.wavesplatform.wavesj.transactions;

import com.wavesplatform.wavesj.Base64;
import com.wavesplatform.wavesj.ByteString;
import com.wavesplatform.wavesj.DataEntry;
import com.wavesplatform.wavesj.PublicKeyAccount;
import com.wavesplatform.wavesj.json.ser.TransactionSerTest;

import java.util.Arrays;

import static com.wavesplatform.wavesj.Asset.toWavelets;
import static java.util.Collections.singletonList;


public final class InvokeScriptTransactionTestData {
    private static final byte chainId = TransactionSerTest.chainId;

    public static final PublicKeyAccount SENDER_PUB =
            new PublicKeyAccount("4QZkF9ejEsao1M8pNDAjoNqGsLsT3E6koXbNtCFxscce", chainId);

    public static final PublicKeyAccount RECIPIENT_PUB =
            new PublicKeyAccount("H9S6sPxueb6z1PB46VZJD6FbaTxsNfT8GHv5PPHbvDHx", chainId);

    public static InvokeScriptTransaction txFull() {
        return new InvokeScriptTransaction(
                chainId,
                SENDER_PUB,
                RECIPIENT_PUB.getAddress(),
                new InvokeScriptTransaction.FunctionCall("deposit")
                        .addArg(10L)
                        .addArg("STRING_ARG")
                        .addArg(true)
                        .addArg(new ByteString("4QZkF9")),
                singletonList(new InvokeScriptTransaction.Payment(toWavelets(10), null)),
                toWavelets(0.005),
                null,
                1526983936610L,
                singletonList(new ByteString("59e1LnALZD7JssScwso6Rj9geZvUvRYEgDQe3xb312gKEqHQRMewgFJsAdcGcCAUhQPwpt5yfA7i42kdukwQNEJg"))
        );
    }

    public static String txFullJson() {
        return "{\"chainId\":84," +
                "\"senderPublicKey\":\"4QZkF9ejEsao1M8pNDAjoNqGsLsT3E6koXbNtCFxscce\"," +
                "\"dApp\":\"3Mvqinkpz45gprXcpgcMb9yqUv4jpBGMQMw\"," +
                "\"call\":{" +
                    "\"function\":\"deposit\"," +
                    "\"args\":[" +
                        "{\"value\":10,\"type\":\"integer\"}," +
                        "{\"value\":\"STRING_ARG\",\"type\":\"string\"}," +
                        "{\"value\":true,\"type\":\"boolean\"}," +
                        "{\"value\":\"base64:hUKqkA==\",\"type\":\"binary\"}" +
                "]}," +
                "\"payment\":[{\"amount\":1000000000,\"assetId\":null}]," +
                "\"fee\":500000," +
                "\"feeAssetId\":null," +
                "\"timestamp\":1526983936610," +
                "\"proofs\":[\"59e1LnALZD7JssScwso6Rj9geZvUvRYEgDQe3xb312gKEqHQRMewgFJsAdcGcCAUhQPwpt5yfA7i42kdukwQNEJg\"]," +
                "\"type\":16," +
                "\"version\":1," +
                "\"height\":1234}";
    }

    public static InvokeScriptTransaction txNoFunctionCall() {
        return new InvokeScriptTransaction(
                chainId,
                SENDER_PUB,
                RECIPIENT_PUB.getAddress(),
                null,
                singletonList(new InvokeScriptTransaction.Payment(toWavelets(10), null)),
                toWavelets(0.005),
                null,
                1526983936610L,
                singletonList(new ByteString("2BaoLeTH3CXiwvQHdmuwEUNQsPtAHrrZv2fWxEsWh4TJyhJvRs27VVusEzpaCiW1oX9eY6eB57qUiSsw2GaJpF8w"))
        );
    }

    public static String txNoFunctionCallJson() {
        return "{\"chainId\":84," +
                "\"dApp\":\"3Mvqinkpz45gprXcpgcMb9yqUv4jpBGMQMw\",\n" +
                "\"senderPublicKey\":\"4QZkF9ejEsao1M8pNDAjoNqGsLsT3E6koXbNtCFxscce\"," +
                "\"payment\":[{\"amount\":1000000000,\"assetId\":null}]," +
                "\"fee\":500000," +
                "\"feeAssetId\":null," +
                "\"timestamp\":1526983936610," +
                "\"proofs\":[\"2BaoLeTH3CXiwvQHdmuwEUNQsPtAHrrZv2fWxEsWh4TJyhJvRs27VVusEzpaCiW1oX9eY6eB57qUiSsw2GaJpF8w\"]," +
                "\"type\":16," +
                "\"version\":1," +
                "\"height\":\"1234\"}";
    }

    public static InvokeScriptTransaction txNoPayment() {
        return new InvokeScriptTransaction(
                chainId,
                SENDER_PUB,
                RECIPIENT_PUB.getAddress(),
                new InvokeScriptTransaction.FunctionCall("deposit")
                        .addArg(10L)
                        .addArg("STRING_ARG")
                        .addArg(true)
                        .addArg(new ByteString("4QZkF9")),
                null,
                toWavelets(0.005),
                null,
                1526983936610L,
                singletonList(new ByteString("4JFJ2w7qqVaD4npGobojPHQ7fwAXw2q4fJDYTjJsqUQT9ZRKuDr5doWgGuR2o1skbeUzLhxaNKEiqv7KHvh5pJyT"))
        );
    }

    public static String txNoPaymentJson() {
        return "{\"chainId\":84," +
                "\"dApp\":\"3Mvqinkpz45gprXcpgcMb9yqUv4jpBGMQMw\"," +
                "\"senderPublicKey\":\"4QZkF9ejEsao1M8pNDAjoNqGsLsT3E6koXbNtCFxscce\"," +
                "\"call\":{" +
                    "\"function\":\"deposit\"," +
                    "\"args\":[" +
                        "{\"value\":10,\"type\":\"integer\"}," +
                        "{\"value\":\"STRING_ARG\",\"type\":\"string\"}," +
                        "{\"value\":true,\"type\":\"boolean\"}," +
                        "{\"value\":\"base64:hUKqkA==\",\"type\":\"binary\"}]}," +
                "\"payment\":[],"+
                "\"fee\":500000," +
                "\"feeAssetId\":null," +
                "\"timestamp\":1526983936610," +
                "\"proofs\":[\"4JFJ2w7qqVaD4npGobojPHQ7fwAXw2q4fJDYTjJsqUQT9ZRKuDr5doWgGuR2o1skbeUzLhxaNKEiqv7KHvh5pJyT\"]," +
                "\"type\":16," +
                "\"version\":1," +
                "\"height\":1234}";
    }

    public static InvokeScriptTransaction txNoFunctionCallAndPayment() {
        return new InvokeScriptTransaction(
                chainId,
                SENDER_PUB,
                RECIPIENT_PUB.getAddress(),
                null,
                null,
                toWavelets(0.005),
                null,
                1526983936610L,
                singletonList(new ByteString("FSCsS76cewPEcYidPe8wffaiPPE3kPfvnPRupgsDw3v7QNuEBmZY24kgvm2ar5CQDmVovdGFLyUFZZWs1PbEPgv"))
        );
    }

    public static String txNoFunctionCallAndPaymentJson() {
        return "{\"chainId\":84," +
                "\"dApp\":\"3Mvqinkpz45gprXcpgcMb9yqUv4jpBGMQMw\"," +
                "\"senderPublicKey\":\"4QZkF9ejEsao1M8pNDAjoNqGsLsT3E6koXbNtCFxscce\"," +
                "\"fee\":500000," +
                "\"feeAssetId\":null," +
                "\"timestamp\":1526983936610," +
                "\"proofs\":[\"FSCsS76cewPEcYidPe8wffaiPPE3kPfvnPRupgsDw3v7QNuEBmZY24kgvm2ar5CQDmVovdGFLyUFZZWs1PbEPgv\"]," +
                "\"type\":16," +
                "\"version\":1," +
                "\"height\":1234}";
    }

    public static InvokeScriptTransactionStCh txWithStateChanges() {
        return new InvokeScriptTransactionStCh(
                chainId,
                new PublicKeyAccount("4f8jYJccCjarVgc8FHn5ms5YLkpi6PfHpxTnmjQZMkJk", chainId),
                "3MqQ9ihYKGehfUnXYf5WmkYSZUD71ByeCQe",
                new InvokeScriptTransaction.FunctionCall("withdraw")
                        .addArg("CjX7LkS5G9Yq3XR2VPBkRY4Su8WbaR4KmjLqDh4Jz1VM")
                        .addArg(new ByteString(Base64.decode("base64:gF+ENGvrNAadLgWxh/L1omQFi45C4JARpWd9QJYkk63Gszyq9nDfZqkMQ1xKyCNe/HNBlFyXKhy/tNtjNo5JUxTd0vvr8Dd6lEynFzsJDG3OSrWvSS3az59gWU0uyUXZtIiyibrIgW1KnTfLXfJtJ+BGEcrSn4/tjN6eSSzHR5KVuBVBugQKxgyettwcfo5N/EKnTtpFcTE5q9rzz/GLf84cRcUw5z5WhpUDmKGEy3ro6OoNYjiKb6r6L4tMauNJnVeMod2OqEMaYUSSaHEazx7ufGSse4M+quSza5Qxi+QcoTHPCRjsyO3p3qxsSiQjYFcmgSYMcf6zWSSVTwUjDQ=="))),
                null,
                500000,
                null,
                1561727253494L,
                singletonList(new ByteString("4vUu8wkcvXvGX8S9RK2KQd6NzaQ8FqJgUK1zE9oMJxW3CEbT132si8FvcJ6UriYMKfSBkz8WPHnF6jLhNbYt1Ts2")),
                new InvokeScriptTransactionStCh.StateChanges(
                        Arrays.<DataEntry<?>>asList(
                                new DataEntry.StringEntry("CjX7LkS5G9Yq3XR2VPBkRY4Su8WbaR4KmjLqDh4Jz1VM",
                                        "03WON_03124_44144YmNQFhw3ZaT3PoS3pz8YQmwfzWsr5QJXBZh1EbVrZ_06562004_09380000000_014"),
                                new DataEntry.LongEntry("$RESERVED_AMOUNT",
                                        0)),
                        singletonList(new InvokeScriptTransactionStCh.OutTransfer(
                                "3N4yERKjhFqYXKV1D2RJLWCzmDqerCCvpNq",
                                380000000L, null))
                )
        );
    }

    public static String txWithStateChangesJson() {
        return "{\"chainId\":84,\n" +
                "  \"senderPublicKey\": \"4f8jYJccCjarVgc8FHn5ms5YLkpi6PfHpxTnmjQZMkJk\",\n" +
                "  \"fee\": 500000,\n" +
                "  \"type\": 16,\n" +
                "  \"version\": 1,\n" +
                "  \"stateChanges\": {\n" +
                "    \"data\": [\n" +
                "      {\n" +
                "        \"type\": \"string\",\n" +
                "        \"value\": \"03WON_03124_44144YmNQFhw3ZaT3PoS3pz8YQmwfzWsr5QJXBZh1EbVrZ_06562004_09380000000_014\",\n" +
                "        \"key\": \"CjX7LkS5G9Yq3XR2VPBkRY4Su8WbaR4KmjLqDh4Jz1VM\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"type\": \"integer\",\n" +
                "        \"value\": 0,\n" +
                "        \"key\": \"$RESERVED_AMOUNT\"\n" +
                "      }\n" +
                "    ],\n" +
                "    \"transfers\": [\n" +
                "      {\n" +
                "        \"address\": \"3N4yERKjhFqYXKV1D2RJLWCzmDqerCCvpNq\",\n" +
                "        \"asset\": null,\n" +
                "        \"amount\": 380000000\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  \"call\": {\n" +
                "    \"function\": \"withdraw\",\n" +
                "    \"args\": [\n" +
                "      {\n" +
                "        \"type\": \"string\",\n" +
                "        \"value\": \"CjX7LkS5G9Yq3XR2VPBkRY4Su8WbaR4KmjLqDh4Jz1VM\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"type\": \"binary\",\n" +
                "        \"value\": \"base64:gF+ENGvrNAadLgWxh/L1omQFi45C4JARpWd9QJYkk63Gszyq9nDfZqkMQ1xKyCNe/HNBlFyXKhy/tNtjNo5JUxTd0vvr8Dd6lEynFzsJDG3OSrWvSS3az59gWU0uyUXZtIiyibrIgW1KnTfLXfJtJ+BGEcrSn4/tjN6eSSzHR5KVuBVBugQKxgyettwcfo5N/EKnTtpFcTE5q9rzz/GLf84cRcUw5z5WhpUDmKGEy3ro6OoNYjiKb6r6L4tMauNJnVeMod2OqEMaYUSSaHEazx7ufGSse4M+quSza5Qxi+QcoTHPCRjsyO3p3qxsSiQjYFcmgSYMcf6zWSSVTwUjDQ==\"\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  \"dApp\": \"3MqQ9ihYKGehfUnXYf5WmkYSZUD71ByeCQe\",\n" +
                "  \"sender\": \"3N4Aib5iubWiGMzdTh6wWiVDVbo32oeVUmH\",\n" +
                "  \"feeAssetId\": null,\n" +
                "  \"proofs\": [\n" +
                "    \"4vUu8wkcvXvGX8S9RK2KQd6NzaQ8FqJgUK1zE9oMJxW3CEbT132si8FvcJ6UriYMKfSBkz8WPHnF6jLhNbYt1Ts2\"\n" +
                "  ],\n" +
                "  \"payment\": [],\n" +
                "  \"id\": \"FPUbH4VEGeao5oj4Ebq2znGc9sKvNaVc6H2vjPorr9bM\",\n" +
                "  \"timestamp\": 1561727253494,\n" +
                "  \"height\": 562005\n" +
                "}";
    }
}
