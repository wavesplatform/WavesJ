package com.wavesplatform.wavesj.transactions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wavesplatform.wavesj.*;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static com.wavesplatform.wavesj.ByteUtils.KBYTE;
import static org.junit.Assert.assertEquals;

public class SetScriptTransactionTest {
    SetScriptTransaction tx = new SetScriptTransaction(new PublicKeyAccount("FM5ojNqW7e9cZ9zhPYGkpSP1Pcd8Z3e3MNKYVS5pGJ8Z", (byte) 'T'), null, (byte) 'T' , 100000, 1526983936610L, Collections.singletonList(new ByteString("tcTr672rQ5gXvcA9xCGtQpkHC8sAY1TDYqDcQG7hQZAeHcvvHFo565VEv1iD1gVa3ZuGjYS7hDpuTnQBfY2dUhY")));

    @Test
    public void bytesBytesTest() {
        assertEquals("484WYm5XGZ17c6wUFmYAmSB1xyBXWDfF7DNFk5K3HbQKfPEgFjzr5iWQ1m8NuUpacqcAsxD", Base58.encode(tx.getBytes()));
    }

    @Test
    public void bytesIdTest() {
        assertEquals("Cst37pKJ19WnUZSD6mjqywosMJDbqatuYm2sFAbXrysE", tx.getId().getBase58String());
    }
}
