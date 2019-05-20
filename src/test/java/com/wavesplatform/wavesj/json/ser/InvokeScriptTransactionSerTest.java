package com.wavesplatform.wavesj.json.ser;

import com.wavesplatform.wavesj.json.InvokeScriptTransactionTxTestData;
import com.wavesplatform.wavesj.transactions.InvokeScriptTransaction;
import org.junit.Test;

import java.io.IOException;


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
public class InvokeScriptTransactionSerTest extends TransactionSerTest {

    @Test
    public void testV1SerTxFull() throws IOException {
        serializationRoadtripTest(
                InvokeScriptTransactionTxTestData.txFull(),
                InvokeScriptTransaction.class);
    }

    @Test
    public void testV1SerTxNoFunctionalCall() throws IOException {
        serializationRoadtripTest(
                InvokeScriptTransactionTxTestData.txNoFunctionCall(),
                InvokeScriptTransaction.class);
    }

    @Test
    public void testV1SerTxNoPayment() throws IOException {
        serializationRoadtripTest(
                InvokeScriptTransactionTxTestData.txNoPayment(),
                InvokeScriptTransaction.class);
    }

    @Test
    public void testV1SerTxNoFunctionalCallAndPayment() throws IOException {
        serializationRoadtripTest(
                InvokeScriptTransactionTxTestData.txNoFunctionCallAndPayment(),
                InvokeScriptTransaction.class);
    }

}
