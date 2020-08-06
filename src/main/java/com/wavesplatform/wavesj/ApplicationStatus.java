package com.wavesplatform.wavesj;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonProperty;

@SuppressWarnings("unused")
public enum ApplicationStatus {

    @JsonProperty("succeeded")
    SUCCEEDED,

    @JsonProperty("script_execution_failed")
    SCRIPT_EXECUTION_FAILED,

    @JsonEnumDefaultValue
    @JsonProperty("unknown")
    UNKNOWN

}
