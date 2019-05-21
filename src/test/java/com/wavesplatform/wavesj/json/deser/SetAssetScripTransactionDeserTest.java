package com.wavesplatform.wavesj.json.deser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wavesplatform.wavesj.ByteString;
import com.wavesplatform.wavesj.PublicKeyAccount;
import com.wavesplatform.wavesj.json.WavesJsonMapper;
import com.wavesplatform.wavesj.transactions.ReissueTransactionV1;
import com.wavesplatform.wavesj.transactions.ReissueTransactionV2;
import com.wavesplatform.wavesj.transactions.SetAssetScriptTransaction;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;

public class SetAssetScripTransactionDeserTest extends TransactionDeserTest {
    ObjectMapper mapper = new WavesJsonMapper((byte) 'T');

    SetAssetScriptTransaction tx = new SetAssetScriptTransaction(
            new PublicKeyAccount("Athtgb7Zm9V6ExyAzAJM1mP57qNAW1A76TmzXdDZDjbt", (byte) 'T'),
            (byte) 'T',
            "CFTN1FiJnBBqCNKdu9NFmomBshCbbEks1t6iqkX41xjq",
            "base64:AgeJ1sz7",
            100400000,
            1558445313974L,
            Collections.singletonList(new ByteString("3XrCkbSHGE6PjKXaX7fgYmYcAsBQf2yzJRV39hqjwHdi7Shjj2V2MbXEodLcXrJD9LdWYMts6cxnNTv3pC2ArPnj")));


    @Test
    public void DeserializeTest() throws IOException {
        deserializationTest(" {\"senderPublicKey\":\"Athtgb7Zm9V6ExyAzAJM1mP57qNAW1A76TmzXdDZDjbt\",\"fee\":100400000,\"type\":15,\"version\":1,\"script\":\"base64:AgeJ1sz7\",\"sender\":\"3N3kDDPYNbb3vzZRAPkgiR1R7YnLVtSrsiZ\",\"feeAssetId\":null,\"chainId\":84,\"proofs\":[\"3XrCkbSHGE6PjKXaX7fgYmYcAsBQf2yzJRV39hqjwHdi7Shjj2V2MbXEodLcXrJD9LdWYMts6cxnNTv3pC2ArPnj\"],\"assetId\":\"CFTN1FiJnBBqCNKdu9NFmomBshCbbEks1t6iqkX41xjq\",\"id\":\"9VACRmun3M23ceaN6gq3ps9U1kH2ZtSZrotwFm15WWUY\",\"timestamp\":1558445313974,\"height\":571845}",
                tx, SetAssetScriptTransaction.class);
    }

}
