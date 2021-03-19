package com.wavesplatform.wavesj;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wavesplatform.transactions.common.Base64String;

import java.util.Objects;

@SuppressWarnings("unused")
public class ScriptDetails {

    private final Base64String script;
    private final int complexity;

    @JsonCreator
    public ScriptDetails(@JsonProperty("script") String script,
                         @JsonProperty("scriptComplexity") int complexity) {
        this.script = new Base64String(Common.notNull(script, "Script"));
        this.complexity = complexity;
    }

    public Base64String script() {
        return script;
    }

    public int complexity() {
        return complexity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScriptDetails that = (ScriptDetails) o;
        return complexity == that.complexity &&
                Objects.equals(script, that.script);
    }

    @Override
    public int hashCode() {
        return Objects.hash(script, complexity);
    }

    @Override
    public String toString() {
        return "ScriptDetails{" +
                "script='" + script + '\'' +
                ", complexity=" + complexity +
                '}';
    }

}
