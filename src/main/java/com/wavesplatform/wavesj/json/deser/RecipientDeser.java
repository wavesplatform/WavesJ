package com.wavesplatform.wavesj.json.deser;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.wavesplatform.transactions.account.Address;
import com.wavesplatform.transactions.common.Alias;
import com.wavesplatform.transactions.common.Recipient;

import java.io.IOException;

public class RecipientDeser extends JsonDeserializer<Recipient> {

    @Override
    public Recipient deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getValueAsString();

        return Address.isValid(value) ? Address.as(value) : Alias.as(value);
    }

}
