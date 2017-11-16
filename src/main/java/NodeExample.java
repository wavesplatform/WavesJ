import com.wavesplatform.wavesj.*;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.bitcoinj.core.Base58;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class NodeExample {

    static Node node = new Node("https://testnode1.wavesnodes.com");

    static void xfer() throws IOException {
        PrivateKeyAccount acc = new PrivateKeyAccount("CMLwxbMZJMztyTJ6Zkos66cgU7DybfFJfyJtTVpme54t", 'T');
        Transaction tt = BlockchainTransaction.
                makeTransferTx(acc, new Address("3N9gDFq8tKFhBDBTQxR3zqvtpXjw5wW3syA"),
                        1_00000000, null, 100000, null, "");
        send(tt, "/assets/broadcast/transfer");
    }

    static void makeIssueTx() throws IOException {
        PrivateKeyAccount acc = new PrivateKeyAccount("CMLwxbMZJMztyTJ6Zkos66cgU7DybfFJfyJtTVpme54t", 'T');
        System.out.println(acc.getAddress());
        Transaction tt = BlockchainTransaction.
                makeIssueTx(acc, "Wmanbatan", "maa", 1000000000000L, 8, true, 100000000);
        send(tt, "/assets/broadcast/issue");
    }

    static void alias() throws IOException {
        PrivateKeyAccount acc = new PrivateKeyAccount("CMLwxbMZJMztyTJ6Zkos66cgU7DybfFJfyJtTVpme54t", 'T');
        Transaction tt = BlockchainTransaction.makeAliasTx(acc, new Alias("gobbie"), 100_000);
        send(tt, "/alias/broadcast/create");
    }

    static void makeReissueTx() throws IOException {
        PrivateKeyAccount acc = new PrivateKeyAccount("CMLwxbMZJMztyTJ6Zkos66cgU7DybfFJfyJtTVpme54t", 'T');
        Transaction tt = BlockchainTransaction.makeReissueTx(acc,
                Base58.decode("7GZKSQvn9tswZVNnZS8temu3ZNNoFKURMc9RQKYUKPLW"), 1000000000000L, false, 100000000);
        send(tt, "/assets/broadcast/reissue");
    }

    public static void main(String[] args) throws IOException {
        xfer();
    }

    static void send(Transaction tx, String path) throws IOException {
        try (CloseableHttpResponse response = node.send(tx, path)) {
            System.out.println(response.getStatusLine());
            BufferedReader rd = new BufferedReader
                    (new InputStreamReader(
                            response.getEntity().getContent()));
            String line = "";
            while ((line = rd.readLine()) != null) {
                System.out.println(line);
            }
        }
    }
}
