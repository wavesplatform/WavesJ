package com.wavesplatform.wavesj.transactions;

import com.wavesplatform.wavesj.Alias;
import com.wavesplatform.wavesj.Signable;
import com.wavesplatform.wavesj.Transaction;
import com.wavesplatform.wavesj.WithId;

public interface AliasTransaction extends Transaction, Signable, WithId {
    static final byte ALIAS = 10;

    Alias getAlias();
}
