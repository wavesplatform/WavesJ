package com.wavesplatform.core;

import org.bitcoinj.core.Base58;

import java.util.Arrays;

public class Address implements AddressOrAlias {
    private final byte[] bytes;

    public Address(String addr) {
        bytes = Base58.decode(addr);
    }

    @Override
    public int length() {
        return bytes.length;
    }

    @Override
    public byte[] toBytes() {
        return Arrays.copyOf(bytes, bytes.length);
    }

    @Override
    public String repr() {
        return Base58.encode(bytes);
    }
}
