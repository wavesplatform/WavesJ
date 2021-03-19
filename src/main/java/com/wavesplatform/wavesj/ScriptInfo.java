package com.wavesplatform.wavesj;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wavesplatform.transactions.common.Base64String;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class ScriptInfo {

    private final Base64String script;
    private final int complexity;
    private final int verifierComplexity;
    private final Map<String, Integer> callableComplexities;
    private final long extraFee;

    @JsonCreator
    public ScriptInfo(@JsonProperty("script") Base64String script,
                      @JsonProperty("complexity") int complexity,
                      @JsonProperty("verifierComplexity") int verifierComplexity,
                      @JsonProperty("callableComplexities") Map<String, Integer> callableComplexities,
                      @JsonProperty("extraFee") long extraFee) {
        this.script = script == null ? Base64String.empty() : script;
        this.complexity = complexity;
        this.verifierComplexity = verifierComplexity;
        this.callableComplexities = Common.notNull(callableComplexities, "CallableComplexities");
        this.extraFee = extraFee;
    }

    public Base64String script() {
        return script;
    }

    public int complexity() {
        return complexity;
    }

    public int verifierComplexity() {
        return verifierComplexity;
    }

    public Map<String, Integer> callableComplexities() {
        return callableComplexities;
    }

    public long extraFee() {
        return extraFee;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScriptInfo that = (ScriptInfo) o;
        return complexity == that.complexity &&
                verifierComplexity == that.verifierComplexity &&
                extraFee == that.extraFee &&
                script.equals(that.script) &&
                callableComplexities.equals(that.callableComplexities);
    }

    @Override
    public int hashCode() {
        return Objects.hash(script, complexity, verifierComplexity, callableComplexities, extraFee);
    }

    @Override
    public String toString() {
        String callables = callableComplexities.keySet().stream()
                .map(key -> key + "=" + callableComplexities.get(key))
                .collect(Collectors.joining(", ", "{", "}"));
        return "ScriptInfo{" +
                "script='" + script + '\'' +
                ", complexity=" + complexity +
                ", verifierComplexity=" + verifierComplexity +
                ", callableComplexities=" + callables +
                ", extraFee=" + extraFee +
                '}';
    }
}
