package com.wavesplatform.wavesj.matcher;

import com.wavesplatform.wavesj.ApiJson;
import com.wavesplatform.wavesj.AssetPair;
import com.wavesplatform.wavesj.Base58;
import com.wavesplatform.wavesj.PublicKeyAccount;

import java.util.HashMap;
import java.util.Map;

public class CancelOrder extends ApiJson {
    private PublicKeyAccount account;
    private AssetPair assetPair;
    private String orderId;

    public CancelOrder(PublicKeyAccount account, AssetPair assetPair, String orderId) {
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
