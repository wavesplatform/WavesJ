package com.wavesplatform.wavesj.json;

import com.wavesplatform.wavesj.ByteString;
import com.wavesplatform.wavesj.PublicKeyAccount;
import com.wavesplatform.wavesj.json.ser.TransactionSerTest;
import com.wavesplatform.wavesj.transactions.InvokeScriptTransaction;

import static com.wavesplatform.wavesj.Asset.toWavelets;
import static java.util.Collections.singletonList;


public final class InvokeScriptTransactionTxTestData {
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
}
