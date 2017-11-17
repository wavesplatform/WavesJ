import com.wavesplatform.wavesj.Node;
import com.wavesplatform.wavesj.PrivateKeyAccount;

import java.io.IOException;

public class NodeExample {

    static Node node = new Node("https://testnode1.wavesnodes.com");

    public static void main(String[] args) throws IOException {
        System.out.println(node.getHeight());

        String address = "3N9gDFq8tKFhBDBTQxR3zqvtpXjw5wW3syA";
        PrivateKeyAccount acc = new PrivateKeyAccount("CMLwxbMZJMztyTJ6Zkos66cgU7DybfFJfyJtTVpme54t", 'T');
        String txid = node.transfer(acc, address, 1_00000000, 100_000, "hi there");
        System.out.println(txid);

        System.out.println(node.getBalance(address) / 100000000);
        System.out.println(node.getBalance(address, 100) / 100000000);
        System.out.println(node.getBalance(address, "5uiw55uiw55uiw55uiw55uiw55uiw5NM"));
    }
}
