package com.wavesplatform.wavesj;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class AllTxIteratorITest extends BaseITest {

    @Test
    public void testDynamicLoad() {
        TestNode node = new TestNode();
        Iterator<Transaction> it = node.getAllAddressStateChanges("3MpRhvzNbdQj2NErTX9w5642hyz7ht5aRza", 3);
        int txCount = 0;
        while (it.hasNext()) {
            it.next();
            ++txCount;
        }
        Assert.assertEquals(8, txCount);
        Assert.assertEquals(3, TestTxIterator.fetchesCount);
    }

    @Override
    public void enableCleanUp() {
        cleanUp = false;
    }

    private static class TestNode extends Node {
        @Override
        public Iterator<Transaction> getAllAddressStateChanges(String address, int pageSize) {
            return new TestTxIterator(address, pageSize, new AllTxIterator.TransactionsLazyLoader<List<? extends Transaction>>() {
                @Override
                public List<? extends Transaction> load(String address, int limit, String after) throws IOException {
                    return TestNode.this.getAddressStateChanges(address, limit, after);
                }
            });
        }
    }

    private static class TestTxIterator extends AllTxIterator {
        static int fetchesCount = 0;

        TestTxIterator(String address, int pageSize, TransactionsLazyLoader<List<? extends Transaction>> txSupplier) {
            super(address, pageSize, txSupplier);
        }

        @Override
        protected void fetchNext() {
            super.fetchNext();
            ++fetchesCount;
        }
    }
}
