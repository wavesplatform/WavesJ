package com.wavesplatform.wavesj.json.ser;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.wavesplatform.wavesj.AssetPair;

import java.io.IOException;

public class AssetPairSer extends JsonSerializer<AssetPair> {
    @Override
    public void serialize(AssetPair assetPair, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeObject(assetPair.toJsonObject());
    }
}
