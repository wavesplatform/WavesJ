package node;

import base.BaseTestWithNodeInDocker;
import com.wavesplatform.crypto.Crypto;
import com.wavesplatform.transactions.IssueTransaction;
import com.wavesplatform.transactions.account.Address;
import com.wavesplatform.transactions.account.PrivateKey;
import com.wavesplatform.transactions.common.AssetId;
import com.wavesplatform.wavesj.exceptions.NodeException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Arrays.asList;

public class BlockchainTest extends BaseTestWithNodeInDocker {

    @Test
    void rewards() throws IOException, NodeException {
        PrivateKey alice = createAccountWithBalance(10_00000000);

        int currentHeight = node.waitForHeight(3);
        node.getBlockchainRewards();
        node.getBlockchainRewards(currentHeight - 1);
    }

}
