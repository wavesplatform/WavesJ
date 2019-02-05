package com.wavesplatform.wavesj.transactions;

import com.wavesplatform.wavesj.Base58;
import com.wavesplatform.wavesj.ByteString;
import com.wavesplatform.wavesj.PublicKeyAccount;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ReissueTransactionTest {
    ReissueTransactionV1 txV1 = new ReissueTransactionV1(new PublicKeyAccount("FM5ojNqW7e9cZ9zhPYGkpSP1Pcd8Z3e3MNKYVS5pGJ8Z", (byte) 'T'), "9ekQuYn92natMnMq8KqeGK3Nn7cpKd3BvPEGgD6fFyyz", 100000000L, true, 100000000L, 1526287561757L, new ByteString("3LnRMrjkk7RoV35PTwcdB4yW2rqUqXaKAh8DnPk5tNWABvhVQ9oqdTk3zM8b9AbGtry7WEcQZtevfK92DCFaa6hA"));
    ReissueTransactionV2 txV2 = new ReissueTransactionV2(new PublicKeyAccount("FM5ojNqW7e9cZ9zhPYGkpSP1Pcd8Z3e3MNKYVS5pGJ8Z", (byte) 'T'), (byte) 'T', "9ekQuYn92natMnMq8KqeGK3Nn7cpKd3BvPEGgD6fFyyz", 100000000L, true, 100000000L, 1526287561757L, Collections.singletonList(new ByteString("4DFEtUwJ9gjMQMuEXipv2qK7rnhhWEBqzpC3ZQesW1Kh8D822t62e3cRGWNU3N21r7huWnaty95wj2tZxYSvCfro")));

    @Test
    public void bytesV1BytesTest() {
        assertEquals("v28eNU7Ymezfbiqh3RPDacGgmL5zmrd4KHWiu2rhCSqKW6SFRvhAn9zshcrRCDXAWnsXRxwmj879dqizpSnqP7vCrKxH7Ukr8Vhbggv4GthAbXTwGFawfrWWCt", Base58.encode(txV1.getBodyBytes()));
    }

    @Test
    public void bytesV1IdTest() {
        assertEquals("2y8pNQteNQnY5JWtrZGLUv3tD6GFT6DDzBWttVTwBa2t", txV1.getId().getBase58String());
    }

    @Test
    public void bytesV2BytesTest() {
        assertEquals("GJ4bKV5tSXgcBC53FcdTV2Uz4ooC45aKYN4R9head7ArXsmGUCxyDSquAUtPmvrnaEFWFdVRX95bzXQoMZYLDUiT33ak8f2y3jWXNH3oyjrZmuiPbJyYTYybi4oXJ", Base58.encode(txV2.getBodyBytes()));
    }

    @Test
    public void bytesV2IdTest() {
        assertEquals("HbQ7gMoDyRxSU6LbLLBVNTbxASaR8rm4Zck6eYvWVUkB", txV2.getId().getBase58String());
    }

    @Test
    public void signatureV1Test() {
        assertTrue(txV1.verifySignature());
    }
}
