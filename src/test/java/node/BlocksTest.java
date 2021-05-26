package node;

import base.BaseTestWithNodeInDocker;
import com.wavesplatform.transactions.LeaseCancelTransaction;
import com.wavesplatform.transactions.LeaseTransaction;
import com.wavesplatform.transactions.account.PrivateKey;
import com.wavesplatform.transactions.common.Base58String;
import com.wavesplatform.wavesj.ApplicationStatus;
import com.wavesplatform.wavesj.exceptions.NodeException;
import com.wavesplatform.wavesj.info.TransactionInfo;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

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
    }

    @Test
    void leasingInBlock() throws IOException, NodeException {
        PrivateKey alice = createAccountWithBalance(1000000);
        PrivateKey bob = createAccountWithBalance(1000000);

        // 1. lease

        LeaseTransaction leaseTx = LeaseTransaction.builder(bob.address(), 10000).getSignedWith(alice);
        TransactionInfo leaseTxInfo = node.waitForTransaction(node.broadcast(leaseTx).id());

        TransactionInfo leaseTxInBlock = node.getBlock(leaseTxInfo.height())
                .transactions().stream()
                .filter(t -> t.tx().id().equals(leaseTx.id()))
                .findFirst().orElseThrow(AssertionError::new);
        TransactionInfo leaseTxInBlocksSeq = node.getBlocks(leaseTxInfo.height(), leaseTxInfo.height())
                .get(0).transactions().stream()
                .filter(t -> t.tx().id().equals(leaseTx.id()))
                .findFirst().orElseThrow(AssertionError::new);

        assertThat(leaseTxInBlock).isEqualTo(leaseTxInBlocksSeq);
        assertThat(leaseTxInBlock).isInstanceOf(TransactionInfo.class);
        assertThat(leaseTxInBlock).isEqualTo(
                new TransactionInfo(leaseTx, ApplicationStatus.SUCCEEDED, leaseTxInfo.height()));

        // 2. cancel

        LeaseCancelTransaction cancelTx = LeaseCancelTransaction.builder(leaseTx.id()).getSignedWith(alice);
        TransactionInfo cancelTxInfo = node.waitForTransaction(node.broadcast(cancelTx).id());

        TransactionInfo cancelTxInBlock = node.getBlock(cancelTxInfo.height())
                .transactions().stream()
                .filter(t -> t.tx().id().equals(cancelTx.id()))
                .findFirst().orElseThrow(AssertionError::new);
        TransactionInfo cancelTxInBlocksSeq = node.getBlocks(cancelTxInfo.height(), cancelTxInfo.height())
                .get(0).transactions().stream()
                .filter(t -> t.tx().id().equals(cancelTx.id()))
                .findFirst().orElseThrow(AssertionError::new);

        assertThat(cancelTxInBlock).isEqualTo(cancelTxInBlocksSeq);
        assertThat(cancelTxInfo).isInstanceOf(TransactionInfo.class);
        assertThat(cancelTxInBlock).isEqualTo(
                new TransactionInfo(cancelTx, ApplicationStatus.SUCCEEDED, cancelTxInfo.height()));
    }

}
