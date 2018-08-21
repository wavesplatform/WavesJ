package com.wavesplatform.wavesj.json.ser;

import com.wavesplatform.wavesj.ByteString;
import com.wavesplatform.wavesj.PublicKeyAccount;
import com.wavesplatform.wavesj.transactions.SetScriptTransaction;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;

public class SetScriptTransactionSerTest extends TransactionSerTest {
    SetScriptTransaction tx = new SetScriptTransaction(new PublicKeyAccount("FM5ojNqW7e9cZ9zhPYGkpSP1Pcd8Z3e3MNKYVS5pGJ8Z", (byte) 'T'), null, (byte) 'T', 100000, 1526983936610L, Collections.singletonList(new ByteString("tcTr672rQ5gXvcA9xCGtQpkHC8sAY1TDYqDcQG7hQZAeHcvvHFo565VEv1iD1gVa3ZuGjYS7hDpuTnQBfY2dUhY")));

    @Test
    public void V1SerializeTest() throws IOException {
        serializationRoadtripTest(tx, SetScriptTransaction.class);
    }
}
