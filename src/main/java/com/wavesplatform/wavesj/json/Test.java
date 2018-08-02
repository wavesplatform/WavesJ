package com.wavesplatform.wavesj.json;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wavesplatform.wavesj.*;
import com.wavesplatform.wavesj.transactions.TransferTransaction;

import java.io.IOException;

public class Test {
    public static void main(String[] args) throws IOException {
        ObjectMapper mapper = new WavesJsonMapper((byte) 'T');
        PrivateKeyAccount acc = PrivateKeyAccount.fromPrivateKey("CMLwxbMZJMztyTJ6Zkos66cgU7DybfFJfyJtTVpme54t", Account.TESTNET);
        String recipient = "3N9gDFq8tKFhBDBTQxR3zqvtpXjw5wW3syA";
        ObjectWithProofs<TransferTransaction> tx = Transaction.makeTransferTx(acc, recipient, 100, null, 100, null, "Shut up & take my money");
        String jsString = mapper.writeValueAsString(tx);
        System.out.println(jsString);
        ObjectWithProofs<TransferTransaction> result = mapper.readValue(jsString, new TypeReference<ProofedObject<Transaction>>() {});
        System.out.println(result);
    }
}
