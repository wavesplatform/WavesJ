package com.wavesplatform.wavesj.transactions;

import com.wavesplatform.wavesj.Alias;
import com.wavesplatform.wavesj.Base58;
import com.wavesplatform.wavesj.ByteString;
import com.wavesplatform.wavesj.PublicKeyAccount;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AliasTransactionTest {
    AliasTransactionV1 txV1 = new AliasTransactionV1(new PublicKeyAccount("FM5ojNqW7e9cZ9zhPYGkpSP1Pcd8Z3e3MNKYVS5pGJ8Z", (byte) 'T'), new Alias("myalias", (byte) 'T'), 100000, 1526910778245L, new ByteString("CC1jQ4qkuVfMvB2Kpg2Go6QKXJxUFC8UUswUxBsxwisrR8N5s3Yc8zA6dhjTwfWKfdouSTAnRXCxTXb3T6pJq3T"));
    AliasTransactionV2 txV2 = new AliasTransactionV2(new PublicKeyAccount("FM5ojNqW7e9cZ9zhPYGkpSP1Pcd8Z3e3MNKYVS5pGJ8Z", (byte) 'T'), new Alias("myalias", (byte) 'T'), 100000, 1526910778245L, Collections.singletonList(new ByteString("26U7rQTwpdma5GYSZb5bNygVCtSuWL6DKet1Nauf5J57v19mmfnq434YrkKYJqvYt2ydQBUT3P7Xgj5ZVDVAcc5k")));

    @Test
    public void bytesV1BytesTest() {
        assertEquals("eQ67RAW8YJXcFYmBzJCBRHAnf6UyhP1nFr9ipcLSqaeFAhQFh5MHdSYdEKPnWk436ocN1Zx9nNKUPJ6nixKW", Base58.encode(txV1.getBytes()));
    }

    @Test
    public void bytesV1IdTest() {
        assertEquals("7acjQQWJAharrgzb4Z6jo3eeAKAGPmLkHTPtvBTKaiug", txV1.getId().getBase58String());
    }

    @Test
    public void bytesV2BytesTest() {
        assertEquals("3dYnf1F6RzFvE1tWjMxjvesXRc9EV7KfXz8n666arFT2Ks1pFrWQo2AZ4DHMV6MVTJoNV1qQG5TdJQF8qzQfdW", Base58.encode(txV2.getBytes()));
    }

    @Test
    public void bytesV2IdTest() {
        assertEquals("7acjQQWJAharrgzb4Z6jo3eeAKAGPmLkHTPtvBTKaiug", txV2.getId().getBase58String());
    }

    @Test
    public void signatureV1Test() {
        assertTrue(txV1.verifySignature());
    }
}
