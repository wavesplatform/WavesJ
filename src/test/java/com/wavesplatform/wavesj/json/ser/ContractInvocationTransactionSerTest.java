package com.wavesplatform.wavesj.json.ser;

import com.wavesplatform.wavesj.json.ContractInvocationTxTestData;
import com.wavesplatform.wavesj.transactions.ContractInvocationTransaction;
import org.junit.Test;

import java.io.IOException;

public class ContractInvocationTransactionSerTest extends TransactionSerTest {

    @Test
    public void testV1SerTxFull() throws IOException {
        serializationRoadtripTest(
                ContractInvocationTxTestData.txFull(),
                ContractInvocationTransaction.class);
    }

    @Test
    public void testV1SerTxNoFunctionalCall() throws IOException {
        serializationRoadtripTest(
                ContractInvocationTxTestData.txNoFunctionCall(),
                ContractInvocationTransaction.class);
    }

    @Test
    public void testV1SerTxNoPayment() throws IOException {
        serializationRoadtripTest(
                ContractInvocationTxTestData.txNoPayment(),
                ContractInvocationTransaction.class);
    }

    @Test
    public void testV1SerTxNoFunctionalCallAndPayment() throws IOException {
        serializationRoadtripTest(
                ContractInvocationTxTestData.txNoFunctionCallAndPayment(),
                ContractInvocationTransaction.class);
    }
}
