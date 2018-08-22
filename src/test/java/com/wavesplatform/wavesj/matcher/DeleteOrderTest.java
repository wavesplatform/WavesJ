package com.wavesplatform.wavesj.matcher;

import com.wavesplatform.wavesj.AssetPair;
import com.wavesplatform.wavesj.Base58;
import com.wavesplatform.wavesj.PrivateKeyAccount;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DeleteOrderTest {
    DeleteOrder deleteOrder = new DeleteOrder(PrivateKeyAccount.fromPrivateKey("CrppxhgtZZNd5wcVMwsudWJ78ZKLqETR8AmhtjeKDFZU", (byte) 'T'), new AssetPair("PHCN", "Aqy7PRU"), "H93RaJ6D9YxEWNJiiMsej23NVHLrxu6kMyFb7CgX2DZW");

    @Test
    public void bytesTest() {
        assertEquals("4W1eSfcBttw6kiyZhhe52DamKjdcQgqGapg1VFVp5pNqgyomPVMi6NRAr6cLiCi1dAQoUni7eQETMBsjMu1fNLbn", Base58.encode(deleteOrder.getBytes()));
    }
}
