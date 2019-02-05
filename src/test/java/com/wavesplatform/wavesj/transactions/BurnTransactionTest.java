package com.wavesplatform.wavesj.transactions;

import com.wavesplatform.wavesj.Base58;
import com.wavesplatform.wavesj.ByteString;
import com.wavesplatform.wavesj.PublicKeyAccount;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BurnTransactionTest {
    BurnTransactionV1 txV1 = new BurnTransactionV1(new PublicKeyAccount("FM5ojNqW7e9cZ9zhPYGkpSP1Pcd8Z3e3MNKYVS5pGJ8Z", (byte) 'T'), "9ekQuYn92natMnMq8KqeGK3Nn7cpKd3BvPEGgD6fFyyz", 10000000000L, 100000000L, 1526287561757L, new ByteString("uapJcAJQryBhWThU43rYgMNmvdT7kY747vx5BBgxr2KvaeTRx8Vsuh4yu1JxBymU9LnAoo1zjQcPrWSuhi6dVPE"));
    BurnTransactionV2 txV2 = new BurnTransactionV2(new PublicKeyAccount("FM5ojNqW7e9cZ9zhPYGkpSP1Pcd8Z3e3MNKYVS5pGJ8Z", (byte) 'T'), (byte) 'T', "9ekQuYn92natMnMq8KqeGK3Nn7cpKd3BvPEGgD6fFyyz", 10000000000L, 100000000L, 1526287561757L, Collections.singletonList(new ByteString("3NcEv6tcVMuXkTJwiqW4J3GMCTe8iSLY7neEfNZonp59eTQEZXYPQWs565CRUctDrvcbtmsRgWvnN7BnFZ1AVZ1H")));

    @Test
    public void bytesV1BytesTest() {
        assertEquals("F5AFK6ucP8sS7zuwXNLQMe7xa6j76y5WDFgTqSrscXmL6J4gnHq4EqkNmMpQveP2H2siXYB3MQaxLNiBL7AmvPBS8kbx1iLuveFFmRmJrZvNL8t1iA9Ar5FFi", Base58.encode(txV1.getBodyBytes()));
    }

    @Test
    public void bytesV1IdTest() {
        assertEquals("Ci1q7y7Qq2C2GDH7YVXsQ8w5vRRKYeoYTp9J76AXw8TZ", txV1.getId().getBase58String());
    }

    @Test
    public void bytesV2BytesTest() {
        assertEquals("5A6cv8hmu4Qt69gjZHggeF8q9qc86vu8kqm7FgVoMp9np9c2SsU94DyfjnndHd8QagduX1RJqYYbNJ1idvw1AoBYAcSYkXsU1ZHVLo9QcDMR6RfF6MwtPFm2bC1r", Base58.encode(txV2.getBodyBytes()));
    }

    @Test
    public void bytesV2IdTest() {
        assertEquals("6QA1sLV53euVCX5fFemNuEyRVdQ5JYo5dWDsCmtKADRc", txV2.getId().getBase58String());
    }

    @Test
    public void signatureV1Test() {
        assertTrue(txV1.verifySignature());
    }
}
