package com.wavesplatform.wavesj.transactions;

import com.wavesplatform.wavesj.*;
import org.junit.Test;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class MassTransferTransactionTest {
    List<Transfer> transfers = new LinkedList<Transfer>();

    {
        transfers.add(new Transfer("3N5GRqzDBhjVXnCn44baHcz2GoZy5qLxtTh", 100000000L));
        transfers.add(new Transfer("3N5GRqzDBhjVXnCn44baHcz2GoZy5qLxtTh", 200000000L));
    }

    MassTransferTransaction tx = new MassTransferTransaction(new PublicKeyAccount("FM5ojNqW7e9cZ9zhPYGkpSP1Pcd8Z3e3MNKYVS5pGJ8Z", (byte) 'T'), Asset.WAVES, transfers, 200000, new ByteString("59QuUcqP6p"), 1518091313964L, Collections.singletonList(new ByteString("FXMNu3ecy5zBjn9b69VtpuYRwxjCbxdkZ3xZpLzB8ZeFDvcgTkmEDrD29wtGYRPtyLS3LPYrL2d5UM6TpFBMUGQ")));

    @Test
    public void bytesBytesTest() {
        assertEquals("NwibNKaAQ6aG8Uw5gkauUET1Du4GPVnU9ZAV8FxvvCQJu4n1cfJbEcuSgs9LM5v8Nw8koLc5Y8AGHtRJfekYXMw2RH81sE6C1i81pZRTj2PpuyjhWmdAbhbiePiFBQfeXGfXxy1GPmxsp7T9iDWtxJmRpiQ2UjD3QRyEuejtssBuhAgqv", Base58.encode(tx.getBodyBytes()));
    }

    @Test
    public void bytesIdTest() {
        assertEquals("H36CTJc7ztGRZPCrvpNYeagCN1HV1gXqUthsXKdBT3UD", tx.getId().getBase58String());
    }
}
