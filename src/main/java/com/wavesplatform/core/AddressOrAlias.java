package com.wavesplatform.core;

interface Sequence {
    int length();
}

public interface AddressOrAlias extends Sequence {
    byte[] getBytes();
}
