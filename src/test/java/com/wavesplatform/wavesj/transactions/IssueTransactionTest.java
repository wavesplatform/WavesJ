package com.wavesplatform.wavesj.transactions;

import com.wavesplatform.wavesj.Base58;
import com.wavesplatform.wavesj.ByteString;
import com.wavesplatform.wavesj.PublicKeyAccount;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class IssueTransactionTest {

    IssueTransactionV1 txV1 = new IssueTransactionV1(new PublicKeyAccount("FM5ojNqW7e9cZ9zhPYGkpSP1Pcd8Z3e3MNKYVS5pGJ8Z", (byte) 'T'), "Gigacoin", "Gigacoin", 10000000000L, (byte) 8, true, 100000000, 1526287561757L, new ByteString("28kE1uN1pX2bwhzr9UHw5UuB9meTFEDFgeunNgy6nZWpHX4pzkGYotu8DhQ88AdqUG6Yy5wcXgHseKPBUygSgRMJ"));
    IssueTransactionV2 txV2 = new IssueTransactionV2(new PublicKeyAccount("FM5ojNqW7e9cZ9zhPYGkpSP1Pcd8Z3e3MNKYVS5pGJ8Z", (byte) 'T'), (byte) 'T', "Gigacoin", "Gigacoin", 10000000000L, (byte) 8, true, null, 100000000, 1526287561757L, Collections.singletonList(new ByteString("28kE1uN1pX2bwhzr9UHw5UuB9meTFEDFgeunNgy6nZWpHX4pzkGYotu8DhQ88AdqUG6Yy5wcXgHseKPBUygSgRMJ")));

    @Test
    public void bytesV1BytesTest() {
        assertEquals("YqCFajGfGn4xYNfEsDt2a9PExJCwueqw8rSLC4XSVnTgR6yc4T4Qwjf9b7binq3YZt41bH6freDmadjDiw1gr1rHzDBLUWVgFmnc3245vQ4", Base58.encode(txV1.getBytes()));
    }

    @Test
    public void bytesV1IdTest() {
        assertEquals("9ekQuYn92natMnMq8KqeGK3Nn7cpKd3BvPEGgD6fFyyz", txV1.getId().getBase58String());
    }

    @Test
    public void bytesV2BytesTest() {
        assertEquals("e3wJFxVovRUbxC26E8ZwLsmfJpqtJhBG96pcBhTvi8a6r4C6KPbicGzkaopqVYyaXVGCGyV21FrpUWvJbvkXw4VpdKakUtWKCCACqbgSUEvLJzB", Base58.encode(txV2.getBytes()));
    }

    @Test
    public void bytesV2IdTest() {
        assertEquals("2ykNAo5JrvNCcL8PtCmc9pTcNtKUy2PjJkrFdRvTfUf4", txV2.getId().getBase58String());
    }

    @Test
    public void signatureV1Test() {
        assertTrue(txV1.verifySignature());
    }
}
