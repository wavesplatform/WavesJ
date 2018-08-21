package com.wavesplatform.wavesj.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.wavesplatform.wavesj.*;
import com.wavesplatform.wavesj.json.deser.*;
import com.wavesplatform.wavesj.json.ser.*;
import com.wavesplatform.wavesj.matcher.Order;

public class WavesModule extends SimpleModule {
    public WavesModule(byte chainId, ObjectMapper objectMapper) {
        addDeserializer(PublicKeyAccount.class, new PublicKeyAccountDeser(chainId));
        addDeserializer(ByteString.class, new ByteStringDeser());
        addDeserializer(Alias.class, new AliasDeser(chainId));
        addDeserializer(Order.Type.class, new OrderTypeDeser());
        addDeserializer(Order.Status.class, new OrderStatusDeser());
        addDeserializer(Transaction.class, new TransactionDeserializer(objectMapper));

        addSerializer(PublicKeyAccount.class, new PublicKeyAccountSer());
        addSerializer(ByteString.class, new ByteStringSer());
        addSerializer(Alias.class, new AliasSer());
        addSerializer(AssetPair.class, new AssetPairSer());
        addSerializer(Order.Type.class, new OrderTypeSer());
    }
}
