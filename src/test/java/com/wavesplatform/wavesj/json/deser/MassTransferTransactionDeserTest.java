package com.wavesplatform.wavesj.json.deser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wavesplatform.wavesj.Asset;
import com.wavesplatform.wavesj.ByteString;
import com.wavesplatform.wavesj.PublicKeyAccount;
import com.wavesplatform.wavesj.Transfer;
import com.wavesplatform.wavesj.json.WavesJsonMapper;
import com.wavesplatform.wavesj.transactions.MassTransferTransaction;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class MassTransferTransactionDeserTest extends TransactionDeserTest {
    ObjectMapper mapper = new WavesJsonMapper((byte) 'T');

    List<Transfer> transfers = new LinkedList<Transfer>();

    {
        transfers.add(new Transfer("3N5GRqzDBhjVXnCn44baHcz2GoZy5qLxtTh", 100000000L));
        transfers.add(new Transfer("3N5GRqzDBhjVXnCn44baHcz2GoZy5qLxtTh", 200000000L));
    }

    MassTransferTransaction tx = new MassTransferTransaction(
            new PublicKeyAccount("FM5ojNqW7e9cZ9zhPYGkpSP1Pcd8Z3e3MNKYVS5pGJ8Z", (byte) 'T'),
            Asset.WAVES, transfers, 200000, new ByteString("59QuUcqP6p"),
            1518091313964L,
            Collections.singletonList(new ByteString("FXMNu3ecy5zBjn9b69VtpuYRwxjCbxdkZ3xZpLzB8ZeFDvcgTkmEDrD29wtGYRPtyLS3LPYrL2d5UM6TpFBMUGQ")));

    @Test
    public void V1DeserializeTest() throws IOException {
        deserializationTest("{\"height\":1234,\"type\":11,\"id\":\"H36CTJc7ztGRZPCrvpNYeagCN1HV1gXqUthsXKdBT3UD\",\"sender\":\"3N5GRqzDBhjVXnCn44baHcz2GoZy5qLxtTh\",\"senderPublicKey\":\"FM5ojNqW7e9cZ9zhPYGkpSP1Pcd8Z3e3MNKYVS5pGJ8Z\",\"fee\":200000,\"timestamp\":1518091313964,\"proofs\":[\"FXMNu3ecy5zBjn9b69VtpuYRwxjCbxdkZ3xZpLzB8ZeFDvcgTkmEDrD29wtGYRPtyLS3LPYrL2d5UM6TpFBMUGQ\"],\"version\":1,\"assetId\":null,\"attachment\":\"59QuUcqP6p\",\"transferCount\":2,\"totalAmount\":300000000,\"transfers\":[{\"recipient\":\"3N5GRqzDBhjVXnCn44baHcz2GoZy5qLxtTh\",\"amount\":100000000},{\"recipient\":\"3N5GRqzDBhjVXnCn44baHcz2GoZy5qLxtTh\",\"amount\":200000000}]}", tx, MassTransferTransaction.class);
    }
}
