package com.wavesplatform.wavesj.json.deser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wavesplatform.wavesj.ByteString;
import com.wavesplatform.wavesj.PublicKeyAccount;
import com.wavesplatform.wavesj.json.WavesJsonMapper;
import com.wavesplatform.wavesj.transactions.SetScriptTransaction;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;

import static org.junit.Assert.assertEquals;

public class SetScriptTransactionDeserTest extends TransactionDeserTest {
    ObjectMapper mapper = new WavesJsonMapper((byte) 'T');

    SetScriptTransaction tx = new SetScriptTransaction(new PublicKeyAccount("FM5ojNqW7e9cZ9zhPYGkpSP1Pcd8Z3e3MNKYVS5pGJ8Z", (byte) 'T'), null, (byte) 'T', 100000, 1526983936610L, Collections.singletonList(new ByteString("tcTr672rQ5gXvcA9xCGtQpkHC8sAY1TDYqDcQG7hQZAeHcvvHFo565VEv1iD1gVa3ZuGjYS7hDpuTnQBfY2dUhY")));

    @Test
    public void V1DeserializeTest() throws IOException {
        SetScriptTransaction deserialized = mapper.readValue("{\"height\":1234,\"type\":13,\"id\":\"Cst37pKJ19WnUZSD6mjqywosMJDbqatuYm2sFAbXrysE\",\"sender\":\"3N5GRqzDBhjVXnCn44baHcz2GoZy5qLxtTh\",\"senderPublicKey\":\"FM5ojNqW7e9cZ9zhPYGkpSP1Pcd8Z3e3MNKYVS5pGJ8Z\",\"fee\":100000,\"timestamp\":1526983936610,\"proofs\":[\"tcTr672rQ5gXvcA9xCGtQpkHC8sAY1TDYqDcQG7hQZAeHcvvHFo565VEv1iD1gVa3ZuGjYS7hDpuTnQBfY2dUhY\"],\"version\":1,\"script\":null, \"chainId\": 84}", SetScriptTransaction.class);
        assertEquals(deserialized, tx);
        assertEquals(deserialized.getId(), tx.getId());
        assertEquals(1234, deserialized.getHeight());
    }
}
