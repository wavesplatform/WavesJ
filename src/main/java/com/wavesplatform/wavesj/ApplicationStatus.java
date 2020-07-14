package com.wavesplatform.wavesj;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ApplicationStatus {

    @JsonProperty("succeeded")
    SUCCEEDED,
    @JsonProperty("script_execution_failed")
    SCRIPT_EXECUTION_FAILED,
    @JsonProperty("unknown")
    UNKNOWN

}
