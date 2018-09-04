package com.wavesplatform.wavesj.json.ser;

import com.wavesplatform.wavesj.Asset;
import com.wavesplatform.wavesj.ByteString;
import com.wavesplatform.wavesj.PublicKeyAccount;
import com.wavesplatform.wavesj.Transfer;
import com.wavesplatform.wavesj.transactions.MassTransferTransaction;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class MassTransferTransactionSerTest extends TransactionSerTest {
    List<Transfer> transfers = new LinkedList<Transfer>();

    {
        transfers.add(new Transfer("3N5GRqzDBhjVXnCn44baHcz2GoZy5qLxtTh", 100000000L));
        transfers.add(new Transfer("3N5GRqzDBhjVXnCn44baHcz2GoZy5qLxtTh", 200000000L));
    }

    MassTransferTransaction tx = new MassTransferTransaction(new PublicKeyAccount("FM5ojNqW7e9cZ9zhPYGkpSP1Pcd8Z3e3MNKYVS5pGJ8Z", (byte) 'T'), Asset.WAVES, transfers, 200000, new ByteString("59QuUcqP6p"), 1518091313964L, Collections.singletonList(new ByteString("FXMNu3ecy5zBjn9b69VtpuYRwxjCbxdkZ3xZpLzB8ZeFDvcgTkmEDrD29wtGYRPtyLS3LPYrL2d5UM6TpFBMUGQ")));

    @Test
    public void V1SerializeTest() throws IOException {
        serializationRoadtripTest(tx, MassTransferTransaction.class);
    }
}
