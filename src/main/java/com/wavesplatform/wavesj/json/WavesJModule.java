package com.wavesplatform.wavesj.json;

import com.wavesplatform.wavesj.TransactionDebugInfo;
import com.wavesplatform.wavesj.TransactionInfo;
import com.wavesplatform.wavesj.json.deser.*;
import im.mak.waves.transactions.serializers.json.WavesTransactionsModule;

public class WavesJModule extends WavesTransactionsModule {
    public WavesJModule() {
        super();
        addDeserializer(TransactionDebugInfo.class, new TransactionDebugInfoDeser());
        addDeserializer(TransactionInfo.class, new TransactionInfoDeser());
    }
}
