package com.wavesplatform.wavesj;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public abstract class ApiJson {
    public abstract Map<String, Object> getData();

    /**
     * Returns JSON-encoded transaction data.
     * @return a JSON string
     */
    /**
     * Returns JSON-encoded transaction data.
     * @return a JSON string
     */
    public String getJson() {
        /// add version to json and bytes
        /// Add v2-producing methods where needed
        /// test sending with 0 fees
        /// setProof -> withProof ?
//        Byte type = (Byte) data.get("type");
//        if (type != null && type != EXCHANGE) {
//            toJson.put("version", type > ALIAS ? 1 : 2);
//        }
        try {
            return new ObjectMapper().writeValueAsString(getData());
        } catch (JsonProcessingException e) {
            // not expected to ever happen
            return null;
        }
    }
}
