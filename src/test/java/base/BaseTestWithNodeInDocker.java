package base;

import com.wavesplatform.crypto.Crypto;
import com.wavesplatform.transactions.TransferTransaction;
import com.wavesplatform.transactions.account.PrivateKey;
import com.wavesplatform.transactions.common.Amount;
import com.wavesplatform.transactions.common.Id;
import com.wavesplatform.wavesj.Node;
import com.wavesplatform.wavesj.Profile;
import com.wavesplatform.wavesj.exceptions.NodeException;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

import static java.time.temporal.ChronoUnit.MINUTES;

public abstract class BaseTestWithNodeInDocker {
    private static final boolean DEBUG = false;
    private static final GenericContainer<?> NODE_CONTAINER;
    private static final String NODE_API_URL;

    static {
        if (DEBUG) {
            NODE_CONTAINER = null;
            NODE_API_URL = Profile.LOCAL.uri().toString();
        } else {
            NODE_CONTAINER = new GenericContainer<>(DockerImageName.parse("wavesplatform/waves-private-node:v1.5.11"))
                    .withExposedPorts(6869)
                    .withStartupTimeout(Duration.of(5, MINUTES));
            NODE_CONTAINER.start();
            NODE_API_URL = "http://" + NODE_CONTAINER.getHost() + ":" + NODE_CONTAINER.getFirstMappedPort();
        }
    }

    protected static final Node node;

    static {
        try {
            node = new Node(NODE_API_URL);
        } catch (URISyntaxException | NodeException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected static final PrivateKey faucet = PrivateKey.fromSeed("waves private node seed with waves tokens");

    protected static PrivateKey createAccountWithBalance() throws IOException, NodeException {
        return PrivateKey.fromSeed(Crypto.getRandomSeedBytes());
    }

    protected static PrivateKey createAccountWithBalance(long wavesAmount) throws IOException, NodeException {
        PrivateKey account = createAccountWithBalance();

        Id transferTx = node.broadcast(TransferTransaction
                .builder(account.address(), Amount.of(wavesAmount))
                .getSignedWith(faucet)).id();
        node.waitForTransaction(transferTx);

        return account;
    }

    protected static void printTxInfo(String description, Id txId) {
        System.out.println(description + ":\t" + NODE_API_URL + "/transactions/info/" + txId.toString());
    }
}
