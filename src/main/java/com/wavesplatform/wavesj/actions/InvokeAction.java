package com.wavesplatform.wavesj.actions;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wavesplatform.transactions.account.Address;
import com.wavesplatform.transactions.common.Amount;
import com.wavesplatform.transactions.invocation.Function;
import com.wavesplatform.wavesj.StateChanges;

import java.util.List;

public class InvokeAction {

    private final Address dApp;
    private final Function function;
    private final List<Amount> payments;
    private final StateChanges stateChanges;

    public InvokeAction(
            @JsonProperty("dApp") Address dApp,
            @JsonProperty("call") Function function,
            @JsonProperty("payments") List<Amount> payments,
            @JsonProperty("stateChanges") StateChanges stateChanges) {
        this.dApp = dApp;
        this.function = function;
        this.payments = payments;
        this.stateChanges = stateChanges;
    }

    public Address dApp() {
        return dApp;
    }

    public Function function() {
        return function;
    }

    public List<Amount> payments() {
        return payments;
    }

    public StateChanges stateChanges() {
        return stateChanges;
    }
}
