package com.wavesplatform.wavesj.json.deser;

import com.wavesplatform.wavesj.*;
import com.wavesplatform.wavesj.matcher.Order;
import com.wavesplatform.wavesj.matcher.OrderV1;
import com.wavesplatform.wavesj.matcher.OrderV2;
import com.wavesplatform.wavesj.matcher.OrderV3;
import com.wavesplatform.wavesj.transactions.ExchangeTransactionV1;
import com.wavesplatform.wavesj.transactions.ExchangeTransactionV2;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

public class ExchangeTransactionDeserTest extends TransactionDeserTest {
    private OrderV1 sell = new OrderV1(
            new PublicKeyAccount("7E9Za8v8aT6EyU1sX91CVK7tWUeAetnNYDxzKZsyjyKV", (byte) 'T'),
            new PublicKeyAccount("Fvk5DXmfyWVZqQVBowUBMwYtRAHDtdyZNNeRrwSjt6KP", (byte) 'T'),
            Order.Type.SELL, new AssetPair(Asset.WAVES, "9ZDWzK53XT5bixkmMwTJi2YzgxCqn5dUajXFcT2HcFDy"),
            3, 5000000000L, 1526992336241L, 1529584336241L, 2,
            new ByteString("2R6JfmNjEnbXAA6nt8YuCzSf1effDS4Wkz8owpCD9BdCNn864SnambTuwgLRYzzeP5CAsKHEviYKAJ2157vdr5Zq"));
    private OrderV1 buy = new OrderV1(
            new PublicKeyAccount("BqeJY8CP3PeUDaByz57iRekVUGtLxoow4XxPvXfHynaZ", (byte) 'T'),
            new PublicKeyAccount("Fvk5DXmfyWVZqQVBowUBMwYtRAHDtdyZNNeRrwSjt6KP", (byte) 'T'),
            Order.Type.BUY,
            new AssetPair(Asset.WAVES, "9ZDWzK53XT5bixkmMwTJi2YzgxCqn5dUajXFcT2HcFDy"),
            2, 6000000000L, 1526992336241L, 1529584336241L, 1,
            new ByteString("2bkuGwECMFGyFqgoHV4q7GRRWBqYmBFWpYRkzgYANR4nN2twgrNaouRiZBqiK2RJzuo9NooB9iRiuZ4hypBbUQs"));

    private OrderV2 buyV2 = new OrderV2(
            new PublicKeyAccount("BqeJY8CP3PeUDaByz57iRekVUGtLxoow4XxPvXfHynaZ", (byte) 'T'),
            new PublicKeyAccount("Fvk5DXmfyWVZqQVBowUBMwYtRAHDtdyZNNeRrwSjt6KP", (byte) 'T'),
            Order.Type.BUY,
            new AssetPair(Asset.WAVES, "9ZDWzK53XT5bixkmMwTJi2YzgxCqn5dUajXFcT2HcFDy"),
            2, 6000000000L, 1526992336241L, 1529584336241L, 1, (byte) 2,
            Collections.singletonList(new ByteString("2bkuGwECMFGyFqgoHV4q7GRRWBqYmBFWpYRkzgYANR4nN2twgrNaouRiZBqiK2RJzuo9NooB9iRiuZ4hypBbUQs")));

    private OrderV3 buyV3 = new OrderV3(
            new PublicKeyAccount("BqeJY8CP3PeUDaByz57iRekVUGtLxoow4XxPvXfHynaZ", (byte) 'T'),
            new PublicKeyAccount("Fvk5DXmfyWVZqQVBowUBMwYtRAHDtdyZNNeRrwSjt6KP", (byte) 'T'),
            Order.Type.BUY,
            new AssetPair(Asset.WAVES, "9ZDWzK53XT5bixkmMwTJi2YzgxCqn5dUajXFcT2HcFDy"),
            2, 6000000000L, 1526992336241L, 1529584336241L, 1, "9ZDWzK53XT5bixkmMwTJi2YzgxCqn5dUajXFcT2HcFDy", (byte) 3,
            Collections.singletonList(new ByteString("2bkuGwECMFGyFqgoHV4q7GRRWBqYmBFWpYRkzgYANR4nN2twgrNaouRiZBqiK2RJzuo9NooB9iRiuZ4hypBbUQs")));

    private ExchangeTransactionV1 txV1 = new ExchangeTransactionV1(
            buy, sell, 2, 5000000000L, 1, 1, 1, 1526992336241L,
            new ByteString("5NxNhjMrrH5EWjSFnVnPbanpThic6fnNL48APVAkwq19y2FpQp4tNSqoAZgboC2ykUfqQs9suwBQj6wERmsWWNqa")
    );

    private ExchangeTransactionV2 txV2 = new ExchangeTransactionV2(
            buyV2, sell, 2, 5000000000L, 1, 1, 1, 1526992336241L,
            Collections.singletonList(new ByteString("5NxNhjMrrH5EWjSFnVnPbanpThic6fnNL48APVAkwq19y2FpQp4tNSqoAZgboC2ykUfqQs9suwBQj6wERmsWWNqa"))
    );

    private ExchangeTransactionV2 txV2withOV3 = new ExchangeTransactionV2(
            buyV3, sell, 2, 5000000000L, 1, 1, 1, 1526992336241L,
            Collections.singletonList(new ByteString("5NxNhjMrrH5EWjSFnVnPbanpThic6fnNL48APVAkwq19y2FpQp4tNSqoAZgboC2ykUfqQs9suwBQj6wERmsWWNqa"))
    );

    @Test
    public void V1DeserializeTest() throws IOException {
        deserializationTest("{\"height\":1234," +
                        "\"type\":7,\"id\":\"FaDrdKax2KBZY6Mh7K3tWmanEdzZx6MhYUmpjV3LBJRp\"," +
                        "\"sender\":\"3N22UCTvst8N1i1XDvGHzyqdgmZgwDKbp44\"," +
                        "\"senderPublicKey\":\"Fvk5DXmfyWVZqQVBowUBMwYtRAHDtdyZNNeRrwSjt6KP\"," +
                        "\"fee\":1," +
                        "\"timestamp\":1526992336241," +
                        "\"signature\":" +
                        "\"5NxNhjMrrH5EWjSFnVnPbanpThic6fnNL48APVAkwq19y2FpQp4tNSqoAZgboC2ykUfqQs9suwBQj6wERmsWWNqa\"," +
                        "\"order1\":{\"id\":\"EdUTcUZNK3NYKuPrsPCkZGzVUwpjx6qVjd4TgBwna7po\"," +
                        "\"version\":1," +
                        "\"sender\":\"3MthkhReCHXeaPZcWXcT3fa6ey1XWptLtwj\"," +
                        "\"senderPublicKey\":\"BqeJY8CP3PeUDaByz57iRekVUGtLxoow4XxPvXfHynaZ\"," +
                        "\"matcherPublicKey\":\"Fvk5DXmfyWVZqQVBowUBMwYtRAHDtdyZNNeRrwSjt6KP\"," +
                        "\"assetPair\":{\"amountAsset\":null," +
                        "\"priceAsset\":\"9ZDWzK53XT5bixkmMwTJi2YzgxCqn5dUajXFcT2HcFDy\"}," +
                        "\"orderType\":\"buy\",\"price\":6000000000,\"amount\":2," +
                        "\"timestamp\":1526992336241,\"expiration\":1529584336241," +
                        "\"matcherFee\":1," +
                        "\"signature\":\"2bkuGwECMFGyFqgoHV4q7GRRWBqYmBFWpYRkzgYANR4nN2twgrNaouRiZBqiK2RJzuo9NooB9iRiuZ4hypBbUQs\"}," +
                        "\"order2\":{\"id\":\"DS9HPBGRMJcquTb3sAGAJzi73jjMnFFSWWHfzzKK32Q7\"," +
                        "\"version\":1," +
                        "\"sender\":\"3MswjKzUBKCD6i1w4vCosQSbC8XzzdBx1mG\"," +
                        "\"senderPublicKey\":\"7E9Za8v8aT6EyU1sX91CVK7tWUeAetnNYDxzKZsyjyKV\"," +
                        "\"matcherPublicKey\":\"Fvk5DXmfyWVZqQVBowUBMwYtRAHDtdyZNNeRrwSjt6KP\", " +
                        "\"assetPair\":{\"amountAsset\":null,\"priceAsset\":\"9ZDWzK53XT5bixkmMwTJi2YzgxCqn5dUajXFcT2HcFDy\"},\"orderType\":\"sell\"," +
                        "\"price\":5000000000,\"amount\":3,\"timestamp\":1526992336241,\"expiration\":1529584336241,\"matcherFee\":2," +
                        "\"signature\":\"2R6JfmNjEnbXAA6nt8YuCzSf1effDS4Wkz8owpCD9BdCNn864SnambTuwgLRYzzeP5CAsKHEviYKAJ2157vdr5Zq\"}," +
                        "\"price\":5000000000,\"amount\":2,\"buyMatcherFee\":1,\"sellMatcherFee\":1}",
                txV1, ExchangeTransactionV1.class);
    }

    @Test
    public void V2DeserializeTest() throws IOException {
        deserializationTest("{\"height\":1234," +
                        "  \"version\": 2," +
                        "  \"type\": 7," +
                        "  \"id\": \"5KUDbPKjAoNHTMyae9zJZpFjYFAbeSQMQ9rzgkDEEUx6\"," +
                        "  \"sender\": \"3N22UCTvst8N1i1XDvGHzyqdgmZgwDKbp44\"," +
                        "  \"senderPublicKey\": \"Fvk5DXmfyWVZqQVBowUBMwYtRAHDtdyZNNeRrwSjt6KP\"," +
                        "  \"fee\": 1," +
                        "  \"timestamp\": 1526992336241," +
                        "  \"proofs\": [\"5NxNhjMrrH5EWjSFnVnPbanpThic6fnNL48APVAkwq19y2FpQp4tNSqoAZgboC2ykUfqQs9suwBQj6wERmsWWNqa\"]," +
                        "  \"order1\": {" +
                        "    \"version\": 2," +
                        "    \"id\": \"EcndU4vU3SJ58KZAXJPKACvMhijTzgRjLTsuWxSWaQUK\"," +
                        "    \"sender\": \"3MthkhReCHXeaPZcWXcT3fa6ey1XWptLtwj\"," +
                        "    \"senderPublicKey\": \"BqeJY8CP3PeUDaByz57iRekVUGtLxoow4XxPvXfHynaZ\"," +
                        "    \"matcherPublicKey\": \"Fvk5DXmfyWVZqQVBowUBMwYtRAHDtdyZNNeRrwSjt6KP\"," +
                        "    \"assetPair\": {\"amountAsset\": null,\"priceAsset\": \"9ZDWzK53XT5bixkmMwTJi2YzgxCqn5dUajXFcT2HcFDy\"" +
                        "    }," +
                        "    \"orderType\": \"buy\"," +
                        "    \"price\": 6000000000," +
                        "    \"amount\": 2," +
                        "    \"timestamp\": 1526992336241," +
                        "    \"expiration\": 1529584336241," +
                        "    \"matcherFee\": 1," +
                        "    \"signature\": \"2bkuGwECMFGyFqgoHV4q7GRRWBqYmBFWpYRkzgYANR4nN2twgrNaouRiZBqiK2RJzuo9NooB9iRiuZ4hypBbUQs\"," +
                        "    \"proofs\": [" +
                        "      \"2bkuGwECMFGyFqgoHV4q7GRRWBqYmBFWpYRkzgYANR4nN2twgrNaouRiZBqiK2RJzuo9NooB9iRiuZ4hypBbUQs\"" +
                        "    ]" +
                        "  }," +
                        "  \"order2\": {" +
                        "    \"version\": 1," +
                        "    \"id\": \"DS9HPBGRMJcquTb3sAGAJzi73jjMnFFSWWHfzzKK32Q7\"," +
                        "    \"sender\": \"3MswjKzUBKCD6i1w4vCosQSbC8XzzdBx1mG\"," +
                        "    \"senderPublicKey\": \"7E9Za8v8aT6EyU1sX91CVK7tWUeAetnNYDxzKZsyjyKV\"," +
                        "    \"matcherPublicKey\": \"Fvk5DXmfyWVZqQVBowUBMwYtRAHDtdyZNNeRrwSjt6KP\"," +
                        "    \"assetPair\": {" +
                        "      \"amountAsset\": null," +
                        "      \"priceAsset\": \"9ZDWzK53XT5bixkmMwTJi2YzgxCqn5dUajXFcT2HcFDy\"" +
                        "    }," +
                        "    \"orderType\": \"sell\"," +
                        "    \"price\": 5000000000," +
                        "    \"amount\": 3," +
                        "    \"timestamp\": 1526992336241," +
                        "    \"expiration\": 1529584336241," +
                        "    \"matcherFee\": 2," +
                        "    \"signature\": \"2R6JfmNjEnbXAA6nt8YuCzSf1effDS4Wkz8owpCD9BdCNn864SnambTuwgLRYzzeP5CAsKHEviYKAJ2157vdr5Zq\"," +
                        "    \"proofs\": [\"2R6JfmNjEnbXAA6nt8YuCzSf1effDS4Wkz8owpCD9BdCNn864SnambTuwgLRYzzeP5CAsKHEviYKAJ2157vdr5Zq\"]" +
                        "  }," +
                        "  \"price\": 5000000000," +
                        "  \"amount\": 2," +
                        "  \"buyMatcherFee\": 1," +
                        "  \"sellMatcherFee\": 1" +
                        "}",
                txV2, ExchangeTransactionV2.class);
    }

    @Test
    public void V3DeserializeTest() throws IOException {
        deserializationTest("{\"height\":1234," +
                        "  \"version\": 2," +
                        "  \"type\": 7," +
                        "  \"id\": \"5KUDbPKjAoNHTMyae9zJZpFjYFAbeSQMQ9rzgkDEEUx6\"," +
                        "  \"sender\": \"3N22UCTvst8N1i1XDvGHzyqdgmZgwDKbp44\"," +
                        "  \"senderPublicKey\": \"Fvk5DXmfyWVZqQVBowUBMwYtRAHDtdyZNNeRrwSjt6KP\"," +
                        "  \"fee\": 1," +
                        "  \"timestamp\": 1526992336241," +
                        "  \"proofs\": [\"5NxNhjMrrH5EWjSFnVnPbanpThic6fnNL48APVAkwq19y2FpQp4tNSqoAZgboC2ykUfqQs9suwBQj6wERmsWWNqa\"]," +
                        "  \"order1\": {" +
                        "    \"version\": 3," +
                        "    \"id\": \"EcndU4vU3SJ58KZAXJPKACvMhijTzgRjLTsuWxSWaQUK\"," +
                        "    \"sender\": \"3MthkhReCHXeaPZcWXcT3fa6ey1XWptLtwj\"," +
                        "    \"senderPublicKey\": \"BqeJY8CP3PeUDaByz57iRekVUGtLxoow4XxPvXfHynaZ\"," +
                        "    \"matcherPublicKey\": \"Fvk5DXmfyWVZqQVBowUBMwYtRAHDtdyZNNeRrwSjt6KP\"," +
                        "    \"assetPair\": {\"amountAsset\": null,\"priceAsset\": \"9ZDWzK53XT5bixkmMwTJi2YzgxCqn5dUajXFcT2HcFDy\"" +
                        "    }," +
                        "    \"orderType\": \"buy\"," +
                        "    \"price\": 6000000000," +
                        "    \"amount\": 2," +
                        "    \"timestamp\": 1526992336241," +
                        "    \"expiration\": 1529584336241," +
                        "    \"matcherFee\": 1," +
                        "    \"matcherFeeAssetId\": \"9ZDWzK53XT5bixkmMwTJi2YzgxCqn5dUajXFcT2HcFDy\"," +
                        "    \"signature\": \"2bkuGwECMFGyFqgoHV4q7GRRWBqYmBFWpYRkzgYANR4nN2twgrNaouRiZBqiK2RJzuo9NooB9iRiuZ4hypBbUQs\"," +
                        "    \"proofs\": [" +
                        "      \"2bkuGwECMFGyFqgoHV4q7GRRWBqYmBFWpYRkzgYANR4nN2twgrNaouRiZBqiK2RJzuo9NooB9iRiuZ4hypBbUQs\"" +
                        "    ]" +
                        "  }," +
                        "  \"order2\": {" +
                        "    \"version\": 1," +
                        "    \"id\": \"DS9HPBGRMJcquTb3sAGAJzi73jjMnFFSWWHfzzKK32Q7\"," +
                        "    \"sender\": \"3MswjKzUBKCD6i1w4vCosQSbC8XzzdBx1mG\"," +
                        "    \"senderPublicKey\": \"7E9Za8v8aT6EyU1sX91CVK7tWUeAetnNYDxzKZsyjyKV\"," +
                        "    \"matcherPublicKey\": \"Fvk5DXmfyWVZqQVBowUBMwYtRAHDtdyZNNeRrwSjt6KP\"," +
                        "    \"assetPair\": {" +
                        "      \"amountAsset\": null," +
                        "      \"priceAsset\": \"9ZDWzK53XT5bixkmMwTJi2YzgxCqn5dUajXFcT2HcFDy\"" +
                        "    }," +
                        "    \"orderType\": \"sell\"," +
                        "    \"price\": 5000000000," +
                        "    \"amount\": 3," +
                        "    \"timestamp\": 1526992336241," +
                        "    \"expiration\": 1529584336241," +
                        "    \"matcherFee\": 2," +
                        "    \"signature\": \"2R6JfmNjEnbXAA6nt8YuCzSf1effDS4Wkz8owpCD9BdCNn864SnambTuwgLRYzzeP5CAsKHEviYKAJ2157vdr5Zq\"," +
                        "    \"proofs\": [\"2R6JfmNjEnbXAA6nt8YuCzSf1effDS4Wkz8owpCD9BdCNn864SnambTuwgLRYzzeP5CAsKHEviYKAJ2157vdr5Zq\"]" +
                        "  }," +
                        "  \"price\": 5000000000," +
                        "  \"amount\": 2," +
                        "  \"buyMatcherFee\": 1," +
                        "  \"sellMatcherFee\": 1" +
                        "}",
                txV2withOV3, ExchangeTransactionV2.class);
    }
}
