package com.wavesplatform.wavesj.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wavesplatform.wavesj.Account;
import com.wavesplatform.wavesj.PrivateKeyAccount;
import com.wavesplatform.wavesj.Transaction;
import com.wavesplatform.wavesj.transactions.ObjectWithProofs;
import com.wavesplatform.wavesj.transactions.TransferTransaction;

public class Test {
    public static void main(String[] args) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new WavesModule((byte) 'T'));
        PrivateKeyAccount acc = PrivateKeyAccount.fromPrivateKey("CMLwxbMZJMztyTJ6Zkos66cgU7DybfFJfyJtTVpme54t", Account.TESTNET);
        String recipient = "3N9gDFq8tKFhBDBTQxR3zqvtpXjw5wW3syA";
        ObjectWithProofs<TransferTransaction> tx = Transaction.makeTransferTx(acc, recipient, 100, null, 100, null, "Shut up & take my money");
        System.out.println(mapper.writeValueAsString(tx.getObject()));
    }
}
