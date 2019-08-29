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
        int txCount = 0;
        for (TransactionStCh tx: node.getAllAddressStateChanges("3MpRhvzNbdQj2NErTX9w5642hyz7ht5aRza", 3)) {
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
        public Iterable<TransactionStCh> getAllAddressStateChanges(String address, int pageSize) {
            return new TestTxIterator(address, pageSize, new AllTxIterator.TransactionsLazyLoader<List<TransactionStCh>>() {
                @Override
                public List<TransactionStCh> load(String address, int limit, String after) throws IOException {
                    return TestNode.this.getAddressStateChanges(address, limit, after) ;
                }
            });
        }
    }

    private static class TestTxIterator extends AllTxIterator<TransactionStCh> {
        static int fetchesCount = 0;

        public TestTxIterator(String address, int pageSize, TransactionsLazyLoader<List<TransactionStCh>> txSupplier) {
            super(address, pageSize, txSupplier);
        }

        @Override
        protected void fetchNext() {
            super.fetchNext();
            ++fetchesCount;
        }
    }
}
