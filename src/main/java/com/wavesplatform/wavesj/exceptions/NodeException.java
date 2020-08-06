package com.wavesplatform.wavesj.exceptions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

@SuppressWarnings("unused")
public class NodeException extends Exception {

    private final int errorCode;

    @JsonCreator
    public NodeException(@JsonProperty("error") int errorCode,
                         @JsonProperty("message") String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NodeException that = (NodeException) o;
        return errorCode == that.errorCode;
    }

    @Override
    public int hashCode() {
        return Objects.hash(errorCode);
    }

    @Override
    public String toString() {
        return "NodeException{" +
                "code=" + errorCode +
                ", message=" + getMessage() +
                '}';
    }
}
