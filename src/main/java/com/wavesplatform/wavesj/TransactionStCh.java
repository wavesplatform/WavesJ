package com.wavesplatform.wavesj;

import com.wavesplatform.wavesj.transactions.StateChanges;

/**
 * Interface to represent transaction types which support and track state changes
 */
public interface TransactionStCh extends Transaction {
    StateChanges getStateChanges();
}
