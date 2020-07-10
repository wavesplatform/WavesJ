package com.wavesplatform.wavesj.json;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.wavesplatform.wavesj.*;
import com.wavesplatform.wavesj.json.deser.*;
import com.wavesplatform.wavesj.json.ser.*;
import com.wavesplatform.wavesj.matcher.Order;
import com.wavesplatform.wavesj.transactions.InvokeScriptTransaction;

public class WavesModule extends SimpleModule {
    public WavesModule(byte chainId, WavesJsonMapper objectMapper) {
        addDeserializer(PublicKeyAccount.class, new PublicKeyAccountDeser(chainId));
        addDeserializer(ByteString.class, new ByteStringDeser());
        addDeserializer(Alias.class, new AliasDeser(chainId));
        addDeserializer(Order.Status.class, new OrderStatusDeser());
        addDeserializer(Transaction.class, new TransactionDeserializer<Transaction>(objectMapper, Transaction.class));
        addDeserializer(TransactionStCh.class, new TransactionDeserializer<TransactionStCh>(objectMapper, TransactionStCh.class));
        addDeserializer(Order.class, new OrderDeserializer(objectMapper));

        addSerializer(PublicKeyAccount.class, new PublicKeyAccountSer());
        addSerializer(ByteString.class, new ByteStringSer());
        addSerializer(Alias.class, new AliasSer());
        addSerializer(AssetPair.class, new AssetPairSer());
        addSerializer(Order.Type.class, new OrderTypeSer());
        addSerializer(InvokeScriptTransaction.BinaryArg.class, new InvocationBinaryArgSer());
    }
}
