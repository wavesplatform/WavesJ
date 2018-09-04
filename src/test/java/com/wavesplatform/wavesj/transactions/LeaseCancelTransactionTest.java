package com.wavesplatform.wavesj.transactions;

import com.wavesplatform.wavesj.Base58;
import com.wavesplatform.wavesj.ByteString;
import com.wavesplatform.wavesj.PublicKeyAccount;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LeaseCancelTransactionTest {
    LeaseCancelTransactionV1 txV1 = new LeaseCancelTransactionV1(new PublicKeyAccount("FM5ojNqW7e9cZ9zhPYGkpSP1Pcd8Z3e3MNKYVS5pGJ8Z", (byte) 'T'), "EXhjYjy8a1dURbttrGzfcft7cddDnPnoa3vqaBLCTFVY", 1000000, 1526646300260L, new ByteString("4T76AXcksn2ixhyMNu4m9UyY54M3HDTw5E2HqUsGV4phogs2vpgBcN5oncu4sbW4U3KU197yfHMxrc3kZ7e6zHG3"));
    LeaseCancelTransactionV2 txV2 = new LeaseCancelTransactionV2(new PublicKeyAccount("FM5ojNqW7e9cZ9zhPYGkpSP1Pcd8Z3e3MNKYVS5pGJ8Z", (byte) 'T'), (byte) 'T', "DJWkQxRyJNqWhq9qSQpK2D4tsrct6eZbjSv3AH4PSha6", 1000000, 1526646300260L, Collections.singletonList(new ByteString("3h5SQLbCzaLoTHUeoCjXUHB6qhNUfHZjQQVsWTRAgTGMEdK5aeULMVUfDq63J56kkHJiviYTDT92bLGc8ELrUgvi")));

    @Test
    public void bytesV1BytesTest() {
        assertEquals("URuZWvAEPdjNuAJQ7huAC21xMvWbaSdsYB7BGbKVG4qWwZfmfGnmuhiu53U8iNjw8cF6V1dfLNGkdP5p7KVmuVLFr5LpYPwcknzMUdoBcELV5G", Base58.encode(txV1.getBytes()));
    }

    @Test
    public void bytesV1IdTest() {
        assertEquals("7hmabbFS8a2z79a29pzZH1s8LHxrsEAnnLjJxNdZ1gGw", txV1.getId().getBase58String());
    }

    @Test
    public void bytesV2BytesTest() {
        assertEquals("9ScU6Wx17Vqw6zjXMGMudEVKFyEHuNH8775MDW8P9H7uUoLn5Xvx7aGMCCFRp5jyHL62mKspzr8547kYzmQ6q6b6qyjXCVxHZsdRNj614244JbzKp", Base58.encode(txV2.getBytes()));
    }

    @Test
    public void bytesV2IdTest() {
        assertEquals("4nvUUiQjTH7D2LFyzaxs8JwaZYZHDggJgq1iP99TvVDM", txV2.getId().getBase58String());
    }

    @Test
    public void signatureV1Test() {
        assertTrue(txV1.verifySignature());
    }
}
