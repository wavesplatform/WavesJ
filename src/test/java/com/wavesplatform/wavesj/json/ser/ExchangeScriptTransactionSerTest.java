package com.wavesplatform.wavesj.json.ser;

import com.wavesplatform.wavesj.Asset;
import com.wavesplatform.wavesj.AssetPair;
import com.wavesplatform.wavesj.ByteString;
import com.wavesplatform.wavesj.PublicKeyAccount;
import com.wavesplatform.wavesj.matcher.Order;
import com.wavesplatform.wavesj.transactions.ExchangeTransaction;
import com.wavesplatform.wavesj.transactions.SetScriptTransaction;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;

public class ExchangeScriptTransactionSerTest extends TransactionSerTest {
    Order sell = new Order(Order.Type.SELL, new AssetPair(Asset.WAVES, "9ZDWzK53XT5bixkmMwTJi2YzgxCqn5dUajXFcT2HcFDy"), 3, 5000000000L, 1526992336241L, 1529584336241L, 2, new PublicKeyAccount("7E9Za8v8aT6EyU1sX91CVK7tWUeAetnNYDxzKZsyjyKV", (byte)'T'), new PublicKeyAccount("Fvk5DXmfyWVZqQVBowUBMwYtRAHDtdyZNNeRrwSjt6KP", (byte)'T'), new ByteString("2R6JfmNjEnbXAA6nt8YuCzSf1effDS4Wkz8owpCD9BdCNn864SnambTuwgLRYzzeP5CAsKHEviYKAJ2157vdr5Zq"));
    Order buy = new Order(Order.Type.BUY, new AssetPair(Asset.WAVES, "9ZDWzK53XT5bixkmMwTJi2YzgxCqn5dUajXFcT2HcFDy"), 2, 6000000000L, 1526992336241L, 1529584336241L, 1, new PublicKeyAccount("BqeJY8CP3PeUDaByz57iRekVUGtLxoow4XxPvXfHynaZ", (byte)'T'), new PublicKeyAccount("Fvk5DXmfyWVZqQVBowUBMwYtRAHDtdyZNNeRrwSjt6KP", (byte)'T'), new ByteString("2bkuGwECMFGyFqgoHV4q7GRRWBqYmBFWpYRkzgYANR4nN2twgrNaouRiZBqiK2RJzuo9NooB9iRiuZ4hypBbUQs"));

    ExchangeTransaction tx = new ExchangeTransaction(
            2, 5000000000L, buy, sell, 1, 1, 1, 1526992336241L,
            new ByteString("5NxNhjMrrH5EWjSFnVnPbanpThic6fnNL48APVAkwq19y2FpQp4tNSqoAZgboC2ykUfqQs9suwBQj6wERmsWWNqa")
    );

    @Test
    public void V1SerializeTest() throws IOException {
        serializationRoadtripTest(tx, ExchangeTransaction.class);
    }
}
