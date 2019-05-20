package com.wavesplatform.wavesj.json.deser;

import com.wavesplatform.wavesj.json.InvokeScriptTransactionTxTestData;
import com.wavesplatform.wavesj.transactions.InvokeScriptTransaction;
import org.junit.Test;

import java.io.IOException;

public class InvokeScriptTransactionDeserTest extends TransactionDeserTest {

    @Test
    public void V1DeserializeTest() throws IOException {
        deserializationTest(
                InvokeScriptTransactionTxTestData.txFullJson(),
                InvokeScriptTransactionTxTestData.txFull(),
                InvokeScriptTransaction.class);
    }

    @Test
    public void testV1DeserTxNoFunctionCall() throws IOException {
        deserializationTest(
                InvokeScriptTransactionTxTestData.txNoFunctionCallJson(),
                InvokeScriptTransactionTxTestData.txNoFunctionCall(),
                InvokeScriptTransaction.class);
    }

    @Test
    public void testV1DeserTxNoPayment() throws IOException {
        deserializationTest(
                InvokeScriptTransactionTxTestData.txNoPaymentJson(),
                InvokeScriptTransactionTxTestData.txNoPayment(),
                InvokeScriptTransaction.class);
    }

}