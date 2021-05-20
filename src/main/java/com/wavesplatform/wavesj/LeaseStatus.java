package com.wavesplatform.wavesj;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonProperty;

public enum LeaseStatus {

    @JsonEnumDefaultValue
    @JsonProperty("active")
    ACTIVE,

    @JsonProperty("canceled")
    CANCELED

}
