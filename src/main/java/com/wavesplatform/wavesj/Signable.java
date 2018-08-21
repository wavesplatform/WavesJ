package com.wavesplatform.wavesj;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface Signable {
    @JsonIgnore
    byte[] getBytes();

    @JsonIgnore
    PublicKeyAccount getSenderPublicKey();
}