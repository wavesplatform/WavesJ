package com.wavesplatform.wavesj.matcher;

import com.wavesplatform.wavesj.*;

import java.util.HashMap;
import java.util.Map;

public class DeleteOrder extends ApiJson {
    private PublicKeyAccount account;
    private AssetPair assetPair;
    private String orderId;

    public DeleteOrder(PublicKeyAccount account, AssetPair assetPair, String orderId) {
        this.account = account;
        this.assetPair = assetPair;
        this.orderId = orderId;
    }

    public PublicKeyAccount getAccount() {
        return account;
    }

    public AssetPair getAssetPair() {
        return assetPair;
    }

    public String getOrderId() {
        return orderId;
    }

    @Override
    public Map<String, Object> getData() {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("sender", Base58.encode(account.getPublicKey()));
        data.put("orderId", orderId);
        return data;
    }
}
