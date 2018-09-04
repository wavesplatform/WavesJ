package com.wavesplatform.wavesj.transactions;

import com.wavesplatform.wavesj.Asset;
import com.wavesplatform.wavesj.Base58;
import com.wavesplatform.wavesj.ByteString;
import com.wavesplatform.wavesj.PublicKeyAccount;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TransferTransactionTest {
    TransferTransactionV1 txV1 = new TransferTransactionV1(new PublicKeyAccount("FM5ojNqW7e9cZ9zhPYGkpSP1Pcd8Z3e3MNKYVS5pGJ8Z", (byte) 'T'), "3My3KZgFQ3CrVHgz6vGRt8687sH4oAA1qp8", 1900000, Asset.WAVES, 100000, Asset.WAVES, new ByteString("4t2Xazb2SX"), 1526552510868L, new ByteString("eaV1i3hEiXyYQd6DQY7EnPg9XzpAvB9VA3bnpin2qJe4G36GZXaGnYKCgSf9xiQ61DcAwcBFzjSXh6FwCgazzFz"));
    TransferTransactionV2 txV2 = new TransferTransactionV2(new PublicKeyAccount("FM5ojNqW7e9cZ9zhPYGkpSP1Pcd8Z3e3MNKYVS5pGJ8Z", (byte) 'T'), "3My3KZgFQ3CrVHgz6vGRt8687sH4oAA1qp8", 100000000, Asset.WAVES, 100000000, Asset.WAVES, new ByteString("4t2Xazb2SX"), 1526641218066L, Collections.singletonList(new ByteString("4bfDaqBcnK3hT8ywFEFndxtS1DTSYfncUqd4s5Vyaa66PZHawtC73rDswUur6QZu5RpqM7L9NFgBHT1vhCoox4vi")));

    @Test
    public void bytesV1BytesTest() {
        assertEquals("5xTbFB85oEXsuV8u7Q7yjTnhWogdL2Kg5bMZKtCUsVB8PkMpXHDZB8UBDrhFM3P9kMHcw6S4DuRSXHvABRu8d4x5qPG5YcyWxF7or95t1rB1zPBD1v7HitFMw9AsLZF9", Base58.encode(txV1.getBytes()));
    }

    @Test
    public void bytesV1IdTest() {
        assertEquals("FLszEaqasJptohmP6zrXodBwjaEYq4jRP2BzdPPjvukk", txV1.getId().getBase58String());
    }

    @Test
    public void bytesV2BytesTest() {
        assertEquals("KA5dHBQkDB6Qu8xJPXJzDdZq3b7kwSsXjjjGWasc54aDKicGQo3qNiMbM4ZUYruNLydSD5vM4aBoq1gbpum6qRk4fSaMPwUC5gwwoeJdRxCGHSPtJMgFzq4rKs8krD1g7", Base58.encode(txV2.getBytes()));
    }

    @Test
    public void bytesV2IdTest() {
        assertEquals("2qMiGUpNMuRpeyTnXLa1mLuVP1cYEtxys55cQbDaXd5g", txV2.getId().getBase58String());
    }

    @Test
    public void signatureV1Test() {
        assertTrue(txV1.verifySignature());
    }
}
