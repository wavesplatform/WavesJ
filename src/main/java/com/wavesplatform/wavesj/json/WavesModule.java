package com.wavesplatform.wavesj.json;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.wavesplatform.wavesj.Alias;
import com.wavesplatform.wavesj.ByteString;
import com.wavesplatform.wavesj.PublicKeyAccount;
import com.wavesplatform.wavesj.Transaction;
import com.wavesplatform.wavesj.json.deser.AliasDeser;
import com.wavesplatform.wavesj.json.deser.ByteStringDeser;
import com.wavesplatform.wavesj.json.deser.PublicKeyAccountDeser;
import com.wavesplatform.wavesj.json.deser.TransactionDeser;
import com.wavesplatform.wavesj.json.ser.AliasSer;
import com.wavesplatform.wavesj.json.ser.ByteStringSer;
import com.wavesplatform.wavesj.json.ser.PublicKeyAccountSer;

public class WavesModule extends SimpleModule {
    public WavesModule(byte chainId) {
        addDeserializer(PublicKeyAccount.class, new PublicKeyAccountDeser(chainId));
        addDeserializer(Transaction.class, new TransactionDeser());
        addDeserializer(ByteString.class, new ByteStringDeser());
        addDeserializer(Alias.class, new AliasDeser());

        addSerializer(PublicKeyAccount.class, new PublicKeyAccountSer());
        addSerializer(ByteString.class, new ByteStringSer());
        addSerializer(Alias.class, new AliasSer());
    }
}
