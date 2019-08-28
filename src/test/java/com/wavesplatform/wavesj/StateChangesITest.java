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
        Iterator<Transaction> it = node.getAllAddressStateChanges("3MpRhvzNbdQj2NErTX9w5642hyz7ht5aRza", 5);
        int count = 0;
        while (it.hasNext()) {
            Transaction tx = it.next();
            count++;
            if ("Gk9mBL4JNYAfBeW9HEmFaF6dK1WE8bi9NWhZ5Qn6wVTP".equals(tx.getId().getBase58String())) {
                InvokeScriptTransactionStCh inv = (InvokeScriptTransactionStCh) tx;
                Assert.assertNotNull(inv.getStateChanges());
                Assert.assertEquals(1, inv.getStateChanges().getData().size());
                Assert.assertEquals(0, inv.getStateChanges().getTransfers().size());
                DataEntry<?> data1 = inv.getStateChanges().getData().iterator().next();
                Assert.assertEquals("$voteFor", data1.getKey());
                Assert.assertEquals(800L, ((Long) data1.getValue()).longValue());
            }
        }
        Assert.assertEquals(8, count);
    }

    @Override
    public void enableCleanUp() {
        cleanUp = false;
    }
}
