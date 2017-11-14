package com.wavesplatform.core;

public class Alias implements AddressOrAlias {
    private final String alias;

    public Alias(String alias) {
        this.alias = alias;
    }

    @Override
    public int length() {
        return alias.length();
    }

    @Override
    public byte[] toBytes() {
        return alias.getBytes();
    }

    @Override
    public String repr() {
        return alias;
    }
}
