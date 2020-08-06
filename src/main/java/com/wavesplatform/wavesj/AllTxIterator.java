package com.wavesplatform.wavesj;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class AllTxIterator<T extends TransactionDebugInfo> implements Iterator<T>, Iterable<T>  {

    private static final Logger LOGGER = LoggerFactory.getLogger("com.wavesplatform.wavesj");

    private int dataSize;
    private int pageSize;
    private boolean hasNext;
    private TransactionDebugInfo lastSuccessNext;
    private String address;
    private Iterator<T> dataIt;
    private TransactionsLazyLoader<List<T>> txSupplier;

    public AllTxIterator(String address, int pageSize, TransactionsLazyLoader<List<T>> txSupplier) {
        this.address = address;
        this.pageSize = pageSize;
        this.txSupplier = txSupplier;
        this.lastSuccessNext = null;
        fetchNext();
    }

    protected void fetchNext() {
        try {
            String lastId = lastSuccessNext != null ? lastSuccessNext.tx().id().toString() : null;
            LOGGER.debug("Dynamic load for remaining transactions: address={} page_size={} after_tx={}",
                    address, pageSize, lastId);
            List<T> txs = txSupplier.load(address, pageSize, lastId);
            LOGGER.debug("Dynamic load for remaining transactions: loaded={}", txs.size());
            dataIt = txs.iterator();
            dataSize = txs.size();
            hasNext = dataIt.hasNext();
        } catch (IOException ex) {
            throw new WrappedIOException(ex);
        }
    }

    @Override
    public boolean hasNext() {
        return hasNext;
    }

    @Override
    public T next() {
        T next;
        if (hasNext) {
            next = dataIt.next();
            lastSuccessNext = next;
            hasNext = dataIt.hasNext();
            if (!hasNext
                    && dataSize >= pageSize) { // condition to avoid addition request
                fetchNext();
            }
            return next;
        } else {
            throw new NoSuchElementException("No more results");
        }
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("remove");
    }

    @Override
    public Iterator<T> iterator() {
        return this;
    }

    public interface TransactionsLazyLoader<T> {
        T load(String address, int limit, String after) throws IOException;
    }

    static class WrappedIOException extends RuntimeException {
        private IOException ioEx;

        public WrappedIOException(IOException ioEx) {
            this.ioEx = ioEx;
        }

        public IOException unwrap() {
            return ioEx;
        }
    }
}
