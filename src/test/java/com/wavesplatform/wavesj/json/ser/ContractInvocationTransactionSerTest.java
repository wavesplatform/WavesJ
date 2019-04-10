package com.wavesplatform.wavesj.json.ser;

import com.wavesplatform.wavesj.*;
import com.wavesplatform.wavesj.transactions.ContractInvocationTransaction;
import com.wavesplatform.wavesj.transactions.ContractInvocationTransaction.FunctionCall;
import com.wavesplatform.wavesj.transactions.ContractInvocationTransaction.Payment;
import org.junit.Test;

import java.io.IOException;
import static com.wavesplatform.wavesj.Asset.toWavelets;
import static java.util.Collections.singletonList;

/**
 * Account 1:
 *      address=3MtEyGGB3XQ6zWB71cCN98fotHvjxxwNMu4
 *      public=4QZkF9ejEsao1M8pNDAjoNqGsLsT3E6koXbNtCFxscce
 *      private=3C8rjfZJnh2EHhKccvggPsoMXLw53DriT8EnjyRPJdhh
 *      seed=sunset noodle trap mule mango can spring garment slot august photo champion paper host more
 *
 * Account 2:
 *      address=3Mvqinkpz45gprXcpgcMb9yqUv4jpBGMQMw
 *      public=H9S6sPxueb6z1PB46VZJD6FbaTxsNfT8GHv5PPHbvDHx
 *      private=HAWqLZA98pJPvwGZuomKUkNpgfzKt4HhfMdFWPXwjxuX
 *      seed=creek extend car eight fat hole farm they behave element bag allow absurd clinic harbor
 */
public class ContractInvocationTransactionSerTest extends TransactionSerTest {

    ContractInvocationTransaction tx = new ContractInvocationTransaction(
            Account.TESTNET,
                new PublicKeyAccount("4QZkF9ejEsao1M8pNDAjoNqGsLsT3E6koXbNtCFxscce", Account.TESTNET),
                "3Mvqinkpz45gprXcpgcMb9yqUv4jpBGMQMw",
                        new FunctionCall("deposit")
                        .addArg(10L)
                        .addArg("STRING_ARG")
                        .addArg(true)
                        .addArg(new ByteString("4QZkF9")),
                singletonList(new Payment(toWavelets(10), null)),
                toWavelets(0.005),
                null,
                1526983936610L,
                singletonList(new ByteString("59e1LnALZD7JssScwso6Rj9geZvUvRYEgDQe3xb312gKEqHQRMewgFJsAdcGcCAUhQPwpt5yfA7i42kdukwQNEJg"))
            );

    @Test
    public void V1SerializationTest() throws IOException {
        serializationRoadtripTest(tx, ContractInvocationTransaction.class);
    }
}
