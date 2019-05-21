package com.wavesplatform.wavesj.transactions;

import com.wavesplatform.wavesj.Base58;
import com.wavesplatform.wavesj.ByteString;
import com.wavesplatform.wavesj.PublicKeyAccount;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SetAssetScriptTransactionTest {

    SetAssetScriptTransaction tx = new SetAssetScriptTransaction(
            new PublicKeyAccount("Athtgb7Zm9V6ExyAzAJM1mP57qNAW1A76TmzXdDZDjbt", (byte) 'T'),
            (byte) 'T',
            "CFTN1FiJnBBqCNKdu9NFmomBshCbbEks1t6iqkX41xjq",
            "base64:AgeJ1sz7",
            100400000,
            1558445313974L,
            Collections.singletonList(new ByteString("3XrCkbSHGE6PjKXaX7fgYmYcAsBQf2yzJRV39hqjwHdi7Shjj2V2MbXEodLcXrJD9LdWYMts6cxnNTv3pC2ArPnj")));

    @Test
    public void bytesBytesTest() {
        assertEquals("npGXyNbdQyJXT6EsZraPyn3VBqXwFq1unDSnEw8YGZMW9mAYD8C6BeYgEh7Rc996EDVMSXzvXMF9WAssgdpP68SnHwXVS7AgNS35P2aEfUer2keCVqHxxBZCUSVCN",
                Base58.encode(tx.getBodyBytes()));
    }

    @Test
    public void bytesIdTest() {
        assertEquals("9VACRmun3M23ceaN6gq3ps9U1kH2ZtSZrotwFm15WWUY", tx.getId().getBase58String());
    }
}
