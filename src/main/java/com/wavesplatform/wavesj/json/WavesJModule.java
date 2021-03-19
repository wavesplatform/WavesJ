package com.wavesplatform.wavesj.json;

import com.wavesplatform.transactions.serializers.json.WavesTransactionsModule;
import com.wavesplatform.wavesj.TransactionDebugInfo;
import com.wavesplatform.wavesj.TransactionInfo;
import com.wavesplatform.wavesj.json.deser.TransactionDebugInfoDeser;
import com.wavesplatform.wavesj.json.deser.TransactionInfoDeser;

public class WavesJModule extends WavesTransactionsModule {
    public WavesJModule() {
        super();
        addDeserializer(TransactionDebugInfo.class, new TransactionDebugInfoDeser());
        addDeserializer(TransactionInfo.class, new TransactionInfoDeser());
    }
}
