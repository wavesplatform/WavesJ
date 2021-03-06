package com.wavesplatform.wavesj.json;

import com.wavesplatform.transactions.common.Recipient;
import com.wavesplatform.transactions.invocation.Function;
import com.wavesplatform.transactions.serializers.json.WavesTransactionsModule;
import com.wavesplatform.wavesj.info.TransactionInfo;
import com.wavesplatform.wavesj.info.TransactionWithStatus;
import com.wavesplatform.wavesj.json.deser.FunctionDeser;
import com.wavesplatform.wavesj.json.deser.RecipientDeser;
import com.wavesplatform.wavesj.json.deser.TransactionInfoDeser;
import com.wavesplatform.wavesj.json.deser.TransactionWithStatusDeser;

public class WavesJModule extends WavesTransactionsModule {
    public WavesJModule() {
        super();
        addDeserializer(Function.class, new FunctionDeser());
        addDeserializer(Recipient.class, new RecipientDeser());
        addDeserializer(TransactionWithStatus.class, new TransactionWithStatusDeser());
        addDeserializer(TransactionInfo.class, new TransactionInfoDeser());
    }
}
