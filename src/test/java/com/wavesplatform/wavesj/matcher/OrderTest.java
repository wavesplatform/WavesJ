package com.wavesplatform.wavesj.matcher;

import com.wavesplatform.wavesj.AssetPair;
import com.wavesplatform.wavesj.Base58;
import com.wavesplatform.wavesj.PrivateKeyAccount;
import com.wavesplatform.wavesj.PublicKeyAccount;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class OrderTest {
    private Order ordV1 = new OrderV1(
            PrivateKeyAccount.fromPrivateKey("CrppxhgtZZNd5wcVMwsudWJ78ZKLqETR8AmhtjeKDFZU", (byte)'T'),
            new PublicKeyAccount("G67KDhqHdjNNb2tnHRgNbDppQEM9ySXdiBip577n2Xoj", (byte)'T'),
            Order.Type.BUY,
            new AssetPair("PHCN", "Aqy7PRU"),
            1583290045643L, 3411800000000L, 1534932075909L, 1536178324108L, 4272607605316276L);

    @Test
    public void v1BytesTest() {
        assertEquals("qjMXAJL38hGRZxmn9Rrf9EdspTdHegtWXBDQy7FzZJePFmzQ2qoYZW6TAppssXXpbQPzpsiPFEJvkUzJ6BBhv6tg21o8QZ4JQB3P7MuUFB9v43ofpZmnZbJ2oX8q99YhDij33zvk5XWHCYceqAJKXtXn9SWh9", Base58.encode(ordV1.getBodyBytes()));
    }

    @Test
    public void v1IdTest() {
        assertEquals("H93RaJ6D9YxEWNJiiMsej23NVHLrxu6kMyFb7CgX2DZW", ordV1.getId().getBase58String());
    }

}
