package com.wavesplatform.wavesj;

import java.util.HashMap;
import java.util.Map;

public class ObjectWithSignature<T extends Signable> extends ApiJson implements ProofedObject<T> {
    public static final byte V1 = 1;

    protected final String signature;
    protected final T object;

    public ObjectWithSignature(T object, String signature) {
        this.signature = signature;
        this.object = object;
    }

    public ObjectWithSignature(T object, PrivateKeyAccount account) {
        this.signature = account.sign(object.getBytes());
        this.object = object;
    }

    public String getSignature() {
        return signature;
    }

    public T getObject() {
        return object;
    }

    public byte getVersion() {
        return V1;
    }

    public Map<String, Object> getData() {
        Map<String, Object> base = new HashMap<String, Object>();
        HashMap<String, Object> toJson = new HashMap<String, Object>(base);
        toJson.put("signature", signature);
        toJson.put("version", V1);
        return toJson;
    }
}
