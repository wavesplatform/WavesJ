package com.wavesplatform.wavesj.json;

import com.wavesplatform.wavesj.PublicKeyAccount;

import java.io.IOException;

public class Test {
    public static void main(String[] args) throws IOException {
//        ObjectMapper mapper = new WavesJsonMapper((byte) 'T');
//        PrivateKeyAccount acc = PrivateKeyAccount.fromPrivateKey("CMLwxbMZJMztyTJ6Zkos66cgU7DybfFJfyJtTVpme54t", Account.TESTNET);
//        String recipient = "3N9gDFq8tKFhBDBTQxR3zqvtpXjw5wW3syA";
//        TransferTransactionV1 tx = Transactions.makeTransferTx(acc, recipient, 100, null, 100, null, "Shut up & take my money");
//        String jsString = mapper.writeValueAsString(tx);
//        System.out.println(jsString);
//        TransferTransactionV1 result = mapper.readValue(jsString, new TypeReference<ProofedObject<Transaction>>() {});
//        System.out.println(result);
        PublicKeyAccount a = new PublicKeyAccount("7zV8VPvP2Pz119hj2RcRm6HDz25hkrokYpw6CjRenMYt", (byte) 'T');
        System.out.println(a.getAddress());
        System.out.println(Integer.valueOf('W'));
    }
}
