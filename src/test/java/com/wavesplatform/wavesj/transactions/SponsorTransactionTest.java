package com.wavesplatform.wavesj.transactions;

import com.wavesplatform.wavesj.Base58;
import com.wavesplatform.wavesj.ByteString;
import com.wavesplatform.wavesj.PublicKeyAccount;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertEquals;

public class SponsorTransactionTest {
    SponsorTransaction tx = new SponsorTransaction(new PublicKeyAccount("FM5ojNqW7e9cZ9zhPYGkpSP1Pcd8Z3e3MNKYVS5pGJ8Z", (byte) 'T'), "9ekQuYn92natMnMq8KqeGK3Nn7cpKd3BvPEGgD6fFyyz", 100000, 100000000, 1520945679531L, Collections.singletonList(new ByteString("3QrF81WkwGhbNvKcwpAVyBPL1MLuAG5qmR6fmtK9PTYQoFKGsFg1Rtd2kbMBuX2ZfiFX58nR1XwC19LUXZUmkXE7")));

    @Test
    public void bytesBytesTest() {
        assertEquals("3CKxGzNmb7shpaDFwdovUs4ro1deJaJLzN3yGVgcQKFAu9twWaBLAmjF41p628LkMAwe4ksHdqiqQWCNjaB3FVMbNVu8bzURhqGic3MGGWsBSFriCeewxP5CtPC", Base58.encode(tx.getBodyBytes()));
    }

    @Test
    public void bytesIdTest() {
        assertEquals("Gobt7AiyQAfduRkW8Mk3naWbzH67Zsv9rdmgRNmon1Mb", tx.getId().getBase58String());
    }
}
