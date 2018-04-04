package com.wavesplatform.wavesj;

public class Transfer {
    public final String recipient;
    public final long amount;

    public Transfer(String recipient, long amount) {
        this.recipient = recipient;
        this.amount = amount;
    }
}
