package com.wavesplatform.wavesj.json.deser;

import com.wavesplatform.wavesj.json.ContractInvocationTxTestData;
import com.wavesplatform.wavesj.transactions.ContractInvocationTransaction;
import org.junit.Test;

import java.io.IOException;

public class ContractInvocationTransactionDeserTest extends TransactionDeserTest {

    @Test
    public void V1DeserializeTest() throws IOException {
        deserializationTest(
                ContractInvocationTxTestData.txFullJson(),
                ContractInvocationTxTestData.txFull(),
                ContractInvocationTransaction.class);
    }

    @Test
    public void testV1DeserTxNoFunctionCall() throws IOException {
        deserializationTest(
                ContractInvocationTxTestData.txNoFunctionCallJson(),
                ContractInvocationTxTestData.txNoFunctionCall(),
                ContractInvocationTransaction.class);
    }

    @Test
    public void testV1DeserTxNoPayment() throws IOException {
        deserializationTest(
                ContractInvocationTxTestData.txNoPaymentJson(),
                ContractInvocationTxTestData.txNoPayment(),
                ContractInvocationTransaction.class);
    }

    @Test
    public void testV1DeserTxNoFunctionCallAndPayment() throws IOException {
        deserializationTest(
                ContractInvocationTxTestData.txNoFunctionCallAndPaymentJson(),
                ContractInvocationTxTestData.txNoFunctionCallAndPayment(),
                ContractInvocationTransaction.class);
    }
}