package com.wavesplatform.wavesj.json.dataservices;

import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.wavesplatform.wavesj.Transaction;
import com.wavesplatform.wavesj.json.WavesJsonMapper;
import com.wavesplatform.wavesj.json.WavesModule;

public class DataSrvModule extends WavesModule {

    public DataSrvModule(byte chainId, WavesJsonMapper objectMapper) {
        super(chainId, objectMapper);
    }

    @Override
    protected StdDeserializer<Transaction> initTransactionDeserializer(WavesJsonMapper objectMapper) {
        return new DataSrvTransactionDeser(objectMapper);
    }
}
