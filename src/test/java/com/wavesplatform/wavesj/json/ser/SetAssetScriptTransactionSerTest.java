package com.wavesplatform.wavesj.json.ser;

import com.wavesplatform.wavesj.ByteString;
import com.wavesplatform.wavesj.PublicKeyAccount;
import com.wavesplatform.wavesj.transactions.SetAssetScriptTransaction;
import com.wavesplatform.wavesj.transactions.SetScriptTransaction;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;

public class SetAssetScriptTransactionSerTest extends TransactionSerTest {

    SetAssetScriptTransaction tx = new SetAssetScriptTransaction(
            new PublicKeyAccount("Athtgb7Zm9V6ExyAzAJM1mP57qNAW1A76TmzXdDZDjbt", (byte) 'T'),
            (byte) 'T',
            "CFTN1FiJnBBqCNKdu9NFmomBshCbbEks1t6iqkX41xjq",
            "base64:AgeJ1sz7",
            100400000,
            1558445313974L,
            Collections.singletonList(new ByteString("3XrCkbSHGE6PjKXaX7fgYmYcAsBQf2yzJRV39hqjwHdi7Shjj2V2MbXEodLcXrJD9LdWYMts6cxnNTv3pC2ArPnj")));



    @Test
    public void SerializeTest() throws IOException {
        serializationRoadtripTest(tx, SetAssetScriptTransaction.class);
    }
}
