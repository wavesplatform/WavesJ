package com.wavesplatform.wavesj.transactions;

import com.wavesplatform.wavesj.Base58;
import com.wavesplatform.wavesj.ByteString;
import com.wavesplatform.wavesj.PublicKeyAccount;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LeaseTransactionTest {
    LeaseTransactionV1 txV1 = new LeaseTransactionV1(new PublicKeyAccount("FM5ojNqW7e9cZ9zhPYGkpSP1Pcd8Z3e3MNKYVS5pGJ8Z", (byte) 'T'), "3NCBMxgdghg4tUhEEffSXy11L6hUi6fcBpd", 10000000, 1000000, 1526646300260L, new ByteString("iy3TmfbFds7pc9cDDqfjEJhfhVyNtm3GcxoVz8L3kJFvgRPUmiqqKLMeJGYyN12AhaQ6HvE7aF1tFgaAoCCgNJJ"));
    LeaseTransactionV2 txV2 = new LeaseTransactionV2(new PublicKeyAccount("FM5ojNqW7e9cZ9zhPYGkpSP1Pcd8Z3e3MNKYVS5pGJ8Z", (byte) 'T'), "3NCBMxgdghg4tUhEEffSXy11L6hUi6fcBpd", 10000000, 1000000, 1526646497465L, Collections.singletonList(new ByteString("5Fr3yLwvfKGDsFLi8A8JbHqToHDojrPbdEGx9mrwbeVWWoiDY5pRqS3rcX1rXC9ud52vuxVdBmGyGk5krcgwFu9q")));

    @Test
    public void bytesV1BytesTest() {
        assertEquals("9H2GQKHx21Xa47hH5HonVij1LXtQ4xJ1xBpeJSQkbmV8EfdJD6HbiUeYn9baeDsgqW9B24K9HwVJBTLeEf5D5s8waRkghBpS3YMvTRMmNibB5GMD5", Base58.encode(txV1.getBytes()));
    }

    @Test
    public void bytesV1IdTest() {
        assertEquals("EXhjYjy8a1dURbttrGzfcft7cddDnPnoa3vqaBLCTFVY", txV1.getId().getBase58String());
    }

    @Test
    public void bytesV2BytesTest() {
        assertEquals("3XBHy9st6hgbJeKxBrufL8DKScRFaqjkiQPQJarkMLmLn7hTKFhHFavSwkbycx6XJNx8zN2onJWcgjePoPpyTr1R6tiTeHBQbJxmhxdFZnwv93yq74g8", Base58.encode(txV2.getBytes()));
    }

    @Test
    public void bytesV2IdTest() {
        assertEquals("UL85wuJDXXe6BtQUob4KNb72kTaf8RN9Gp1NajvGMeU", txV2.getId().getBase58String());
    }

    @Test
    public void signatureV1Test() {
        assertTrue(txV1.verifySignature());
    }
}
