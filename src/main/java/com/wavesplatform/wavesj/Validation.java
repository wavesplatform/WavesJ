package com.wavesplatform.wavesj;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class Validation {

    private final boolean valid;
    private final long validationTime;
    private final String error;

    @JsonCreator
    public Validation(@JsonProperty("valid") boolean valid,
                      @JsonProperty("validationTime") long validationTime,
                      @JsonProperty("error") String error) {
        this.valid = valid;
        this.validationTime = validationTime;
        this.error = error;
    }

    public boolean isValid() {
        return valid;
    }

    public long validationTime() {
        return validationTime;
    }

    public String error() {
        return error;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Validation that = (Validation) o;
        return valid == that.valid &&
                validationTime == that.validationTime &&
                Objects.equals(error, that.error);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valid, validationTime, error);
    }

    @Override
    public String toString() {
        return "Validation{" +
                "valid=" + valid +
                ", validationTime=" + validationTime +
                ", error='" + error + '\'' +
                '}';
    }
}
