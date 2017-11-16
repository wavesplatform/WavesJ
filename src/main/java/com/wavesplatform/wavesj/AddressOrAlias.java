package com.wavesplatform.wavesj;

interface Sequence {
    int length();
}

public interface AddressOrAlias extends Sequence {
    byte[] getBytes();
}
