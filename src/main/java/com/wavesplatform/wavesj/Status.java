package com.wavesplatform.wavesj;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonProperty;

@SuppressWarnings("unused")
public enum Status {

    @JsonEnumDefaultValue
    @JsonProperty("not_found")
    NOT_FOUND,

    @JsonProperty("unconfirmed")
    UNCONFIRMED,

    @JsonProperty("confirmed")
    CONFIRMED

}
