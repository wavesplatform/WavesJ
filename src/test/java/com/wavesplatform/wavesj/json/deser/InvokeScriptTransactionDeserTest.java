package com.wavesplatform.wavesj.json.deser;

import com.wavesplatform.wavesj.transactions.InvokeScriptTransactionStCh;
import com.wavesplatform.wavesj.transactions.InvokeScriptTransactionTestData;
import com.wavesplatform.wavesj.transactions.InvokeScriptTransaction;
import org.junit.Test;

import java.io.IOException;

public class InvokeScriptTransactionDeserTest extends TransactionDeserTest {

    @Test
    public void V1DeserializeTest() throws IOException {
        deserializationTest(
                InvokeScriptTransactionTestData.txFullJson(),
                InvokeScriptTransactionTestData.txFull(),
                InvokeScriptTransaction.class);
    }

    @Test
    public void testV1DeserTxNoFunctionCall() throws IOException {
        deserializationTest(
                InvokeScriptTransactionTestData.txNoFunctionCallJson(),
                InvokeScriptTransactionTestData.txNoFunctionCall(),
                InvokeScriptTransaction.class);
    }

    @Test
    public void testV1DeserTxNoPayment() throws IOException {
        deserializationTest(
                InvokeScriptTransactionTestData.txNoPaymentJson(),
                InvokeScriptTransactionTestData.txNoPayment(),
                InvokeScriptTransaction.class);
    }

    @Test
    public void testDeserForTxWithStateChanges() throws IOException {
        deserializationTest(
                InvokeScriptTransactionTestData.txWithStateChangesJson(),
                InvokeScriptTransactionTestData.txWithStateChanges(),
                InvokeScriptTransactionStCh.class
        );
    }
}