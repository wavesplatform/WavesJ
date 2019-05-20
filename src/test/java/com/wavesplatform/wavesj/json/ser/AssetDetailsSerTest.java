package com.wavesplatform.wavesj.json.ser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wavesplatform.wavesj.AssetDetails;
import com.wavesplatform.wavesj.json.WavesJsonMapper;
import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;

import static org.junit.Assert.assertEquals;

public class AssetDetailsSerTest {
    ObjectMapper mapper = new WavesJsonMapper((byte) 'T');
    AssetDetails assetDetails = new AssetDetails("58tShg8yZAEnAzkAAwkLh2Rx1wWxf7e6pnmaJXEQWHyf", 562800L, 1557908790127L, "G9rStAuSaNjMi9KZNVfHymhCUeaWLFqAy88VtTAJre3q", "Gaming", "A test asset for some games.", 8, true, 0L, false, null);

    @Test
    public void assetDetailsSerializeTest() throws IOException {
        serializationRoadtripTest(assetDetails);
    }

    private AssetDetails serializationRoadtripTest(AssetDetails assetDetails) throws IOException {
        StringWriter sw = new StringWriter();
        mapper.writeValue(sw, assetDetails);
        AssetDetails deserialized = mapper.readValue(sw.toString(), AssetDetails.class);
        assertEquals(deserialized, assetDetails);
        assertEquals(deserialized.getAssetId(), assetDetails.getAssetId());
        return deserialized;
    }
}
