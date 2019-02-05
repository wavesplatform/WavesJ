package com.wavesplatform.wavesj;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface Signable {
    @JsonIgnore
    byte[] getBodyBytes();

    @JsonIgnore
    byte[] getBytes();

    PublicKeyAccount getSenderPublicKey();
}