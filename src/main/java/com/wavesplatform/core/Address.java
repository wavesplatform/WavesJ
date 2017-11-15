package com.wavesplatform.core;

import org.bitcoinj.core.Base58;

import java.util.Arrays;

public class Address implements AddressOrAlias {
    private final byte[] bytes;

    public Address(byte[] bytes) {
        this.bytes = bytes;
    }

    public Address(String addr) {
        this(Base58.decode(addr));
    }

    @Override
    public int length() {
        return bytes.length;
    }

    @Override
    public byte[] getBytes() {
        return Arrays.copyOf(bytes, bytes.length);
    }

    @Override
    public String toString() {
        return Base58.encode(bytes);
    }
}
