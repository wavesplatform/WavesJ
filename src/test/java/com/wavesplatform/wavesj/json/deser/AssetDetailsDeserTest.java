package com.wavesplatform.wavesj.json.deser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wavesplatform.wavesj.AssetDetails;
import com.wavesplatform.wavesj.json.WavesJsonMapper;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class AssetDetailsDeserTest{
    ObjectMapper mapper = new WavesJsonMapper((byte) 'T');

    AssetDetails assetDetails = new AssetDetails("58tShg8yZAEnAzkAAwkLh2Rx1wWxf7e6pnmaJXEQWHyf", 562800L, 1557908790127L, "G9rStAuSaNjMi9KZNVfHymhCUeaWLFqAy88VtTAJre3q", "Gaming", "A test asset for some games.", 8, true, 0L, false, null);

    @Test
    public void asssetDetailsDeserializeTest () throws IOException {
        deserializationTest("{\"assetId\":\"58tShg8yZAEnAzkAAwkLh2Rx1wWxf7e6pnmaJXEQWHyf\",\"issueHeight\":562800,\"issueTimestamp\":1557908790127,\"issuer\":\"G9rStAuSaNjMi9KZNVfHymhCUeaWLFqAy88VtTAJre3q\",\"name\":\"Gaming\",\"description\":\"A test asset for some games.\",\"decimals\":8,\"reissuable\":true,\"quantity\":0,\"scripted\":false,\"minSponsoredAssetFee\":null}", assetDetails);
    }

    private AssetDetails deserializationTest(String json, AssetDetails assetDetails) throws IOException {
        AssetDetails deserialized = mapper.readValue(json, AssetDetails.class);
        assertEquals(deserialized, assetDetails);
        assertEquals(deserialized.getAssetId(), assetDetails.getAssetId());
        return deserialized;
    }
}
