package com.wavesplatform.wavesj.actions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class Error {
    private final int code;
    private final String text;

    @JsonCreator
    public Error(@JsonProperty("code") int code,
                 @JsonProperty("text") String text) {
        this.code = code;
        this.text = text;
    }

    public int code() {
        return code;
    }

    public String text() {
        return text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Error error = (Error) o;
        return code == error.code &&
                Objects.equals(text, error.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, text);
    }

    @Override
    public String toString() {
        return "Error{" +
                "code=" + code +
                ", text='" + text + '\'' +
                '}';
    }
}
