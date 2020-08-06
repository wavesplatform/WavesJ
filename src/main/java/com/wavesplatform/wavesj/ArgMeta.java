package com.wavesplatform.wavesj;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

@SuppressWarnings("unused")
public class ArgMeta {

    private final String name;
    private final String type;

    @JsonCreator
    public ArgMeta(@JsonProperty("name") String name,
                   @JsonProperty("type") String type) {
        this.name = Common.notNull(name, "Name");
        this.type = Common.notNull(type, "Type");
    }

    public String name() {
        return name;
    }

    public String type() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArgMeta argMeta = (ArgMeta) o;
        return Objects.equals(name, argMeta.name) &&
                Objects.equals(type, argMeta.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type);
    }

    @Override
    public String toString() {
        return "ArgMeta{" +
                "name='" + name + '\'' +
                ", type=" + type +
                '}';
    }

}
