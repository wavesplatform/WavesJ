package com.wavesplatform.wavesj.json.deser;

import com.wavesplatform.wavesj.Account;
import com.wavesplatform.wavesj.ByteString;
import com.wavesplatform.wavesj.PublicKeyAccount;
import com.wavesplatform.wavesj.transactions.InvokeScriptTransaction;
import org.junit.Test;

import java.io.IOException;

import static com.wavesplatform.wavesj.Asset.toWavelets;
import static java.util.Collections.singletonList;

public class InvokeScriptTransactionDeserTest extends TransactionDeserTest {

    private InvokeScriptTransaction tx = new InvokeScriptTransaction(
            Account.TESTNET,
            new PublicKeyAccount("4QZkF9ejEsao1M8pNDAjoNqGsLsT3E6koXbNtCFxscce", Account.TESTNET),
            "3Mvqinkpz45gprXcpgcMb9yqUv4jpBGMQMw",
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

    @Test
    public void V1DeserializeTest() throws IOException {
        deserializationTest("{\"chainId\":84," +
                "\"senderPublicKey\":\"4QZkF9ejEsao1M8pNDAjoNqGsLsT3E6koXbNtCFxscce\"," +
                "\"dApp\":\"3Mvqinkpz45gprXcpgcMb9yqUv4jpBGMQMw\"," +
                "\"call\":{" +
                    "\"function\":\"deposit\"," +
                    "\"args\":[" +
                        "{\"value\":10,\"type\":\"integer\"}," +
                        "{\"value\":\"STRING_ARG\",\"type\":\"string\"}," +
                        "{\"value\":true,\"type\":\"boolean\"}," +
                        "{\"type\":\"binary\",\"value\":\"base64:hUKqkA==\"}" +
                    "]}," +
                "\"payment\":[{\"amount\":1000000000,\"assetId\":null}]," +
                "\"fee\":500000," +
                "\"feeAssetId\":null," +
                "\"timestamp\":1526983936610," +
                "\"proofs\":[\"59e1LnALZD7JssScwso6Rj9geZvUvRYEgDQe3xb312gKEqHQRMewgFJsAdcGcCAUhQPwpt5yfA7i42kdukwQNEJg\"]," +
                "\"type\":16," +
                "\"version\":1}", tx, InvokeScriptTransaction.class);
    }
}