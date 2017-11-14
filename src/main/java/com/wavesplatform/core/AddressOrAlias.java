package com.wavesplatform.core;

public interface AddressOrAlias {
    int length();///pkg-privatize
    byte[] toBytes();
    String repr();
}
