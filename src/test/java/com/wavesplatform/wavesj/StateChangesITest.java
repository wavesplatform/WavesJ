package com.wavesplatform.wavesj;

import com.wavesplatform.wavesj.transactions.InvokeScriptTransaction;
import com.wavesplatform.wavesj.transactions.InvokeScriptTransactionStCh;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Iterator;

public class StateChangesITest extends BaseITest {

    @Test
    public void invocationStateChanges() throws IOException {
        Transaction tx = node.getStateChanges("FPUbH4VEGeao5oj4Ebq2znGc9sKvNaVc6H2vjPorr9bM");
        Assert.assertEquals(InvokeScriptTransaction.CONTRACT_INVOKE, tx.getType());
        InvokeScriptTransactionStCh inv = (InvokeScriptTransactionStCh) tx;
        Assert.assertNotNull(inv.getStateChanges());
        Assert.assertEquals(2, inv.getStateChanges().getData().size());
        Assert.assertEquals(1, inv.getStateChanges().getTransfers().size());
    }

    @Test(expected = IOException.class)
    public void failTransferStateChanges() throws IOException {
        node.getStateChanges("CajkMdDA4A2e5QZkHF3vfRAPqPvhpKZ79H4STgKWpBVM");
    }

    @Test(expected = IOException.class)
    public void failDataStateChanges() throws IOException {
        node.getStateChanges("DDnQDdzGSLNwTRF4euxuCjoE2p87BF1pi66bop2MYjvW");
    }

    @Test
    public void successAllAddressStateChanges() throws IOException {
        Iterator<TransactionStCh> it = node.getAllAddressStateChanges("3MpRhvzNbdQj2NErTX9w5642hyz7ht5aRza", 5);
        int totalCount = 0;
        int stateChangesCount = 0;
        while (it.hasNext()) {
            TransactionStCh tx = it.next();
            totalCount++;

            if (tx.isStateChangesSupported()) {
                stateChangesCount++;
                Assert.assertNotNull(tx.getStateChanges());
                Assert.assertEquals (1, tx.getStateChanges().getData().size());
                Assert.assertEquals(0, tx.getStateChanges().getTransfers().size());
                DataEntry<?> data1 = tx.getStateChanges().getData().iterator().next();

                String txId = tx.getId().getBase58String();
                if ("Gk9mBL4JNYAfBeW9HEmFaF6dK1WE8bi9NWhZ5Qn6wVTP".equals(txId)) {
                    Assert.assertEquals("$voteFor", data1.getKey());
                    assetLongDataValue(800L, data1);
                } else if ("CTiJiM98Nh1QZ1CvaF68ZDEMpBLsWYN2WqxfFNqNVsa4".equals(txId)) {
                    Assert.assertEquals("$voteAgainst", data1.getKey());
                    assetLongDataValue(200L, data1);
                } else if ("7qRC6UbpKsfxiPPQgBxYjHBXSFTABgbDksjtH2AujtGA".equals(txId)) {
                    Assert.assertEquals("$voteAgainst", data1.getKey());
                    assetLongDataValue(520L, data1);
                }
            }
        }
        Assert.assertEquals(8, totalCount);
        Assert.assertEquals(3, stateChangesCount);
    }

    private static void assetLongDataValue(long expected, DataEntry<?> actualData) {
        Assert.assertEquals(expected, ((Long) actualData.getValue()).longValue());
    }

    @Override
    public void enableCleanUp() {
        cleanUp = false;
    }
}
