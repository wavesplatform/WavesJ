package node;

import base.BaseTestWithNodeInDocker;
import com.wavesplatform.transactions.LeaseTransaction;
import com.wavesplatform.transactions.account.PrivateKey;
import com.wavesplatform.transactions.common.Base58String;
import com.wavesplatform.wavesj.actions.LeaseInfo;
import com.wavesplatform.wavesj.exceptions.NodeException;
import com.wavesplatform.wavesj.info.TransactionInfo;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class BlocksTest extends BaseTestWithNodeInDocker {

    static final Base58String blockIdAtHeight2;

    static {
        try {
            blockIdAtHeight2 = node.getBlockHeaders(2).id();
        } catch (IOException | NodeException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void height() throws IOException, NodeException {
        node.getBlockHeight(System.currentTimeMillis());
        node.getBlockHeight(blockIdAtHeight2);
    }

    @Test
    void blocks() throws IOException, NodeException {
        PrivateKey alice = createAccountWithBalance(10_00000000);

        node.getBlock(2);
        node.getBlock(blockIdAtHeight2);
        node.getGenesisBlock();
        node.getLastBlock();
        node.getBlockHeaders(2);
        node.getBlockHeaders(blockIdAtHeight2);
        node.getBlocks(2, 3);
        node.getBlocksDelay(blockIdAtHeight2, node.getHeight() - 2);
        node.getBlocksGeneratedBy(faucet.address(), 2, 101);
        node.getBlocksHeaders(2, 101);
        node.getLastBlockHeaders();

        //leasing
        LeaseInfo leasing = null;
        LeaseTransaction leaseTx = null;
        TransactionInfo txInBlock = node.getBlock(leasing.height())
                .transactions().stream()
                .filter(t -> t.tx().id().equals(leaseTx.id()))
                .findFirst().orElseThrow(AssertionError::new);
        TransactionInfo txInBlocksSeq = node.getBlocks(leasing.height(), leasing.height())
                .get(0).transactions().stream()
                .filter(t -> t.tx().id().equals(leaseTx.id()))
                .findFirst().orElseThrow(AssertionError::new);
    }

}
