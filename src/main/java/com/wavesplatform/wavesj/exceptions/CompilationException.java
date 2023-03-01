package com.wavesplatform.wavesj.exceptions;

import java.util.Objects;

public class CompilationException extends Exception {
    private final String message;

    public CompilationException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompilationException that = (CompilationException) o;
        return Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message);
    }

    @Override
    public String toString() {
        return "CompilationException{" +
                "message='" + message + '\'' +
                '}';
    }
}
