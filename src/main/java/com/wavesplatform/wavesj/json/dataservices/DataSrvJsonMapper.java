package com.wavesplatform.wavesj.json.dataservices;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.wavesplatform.wavesj.json.WavesJsonMapper;

public class DataSrvJsonMapper extends WavesJsonMapper {

    public DataSrvJsonMapper(byte chainId) {
        super(chainId);
    }

    @Override
    protected void registerModules() {
        registerModule(new DataSrvModule(chainId, this));
    }

    @Override
    protected void configProperties() {
        super.configProperties();
        enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
    }
}
