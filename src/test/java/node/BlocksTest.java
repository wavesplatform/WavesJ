package node;

import base.BaseTestWithNodeInDocker;
import com.wavesplatform.transactions.DataTransaction;
import com.wavesplatform.transactions.LeaseCancelTransaction;
import com.wavesplatform.transactions.LeaseTransaction;
import com.wavesplatform.transactions.TransferTransaction;
import com.wavesplatform.transactions.account.Address;
import com.wavesplatform.transactions.account.PrivateKey;
import com.wavesplatform.transactions.common.Amount;
import com.wavesplatform.transactions.common.Base58String;
import com.wavesplatform.transactions.data.StringEntry;
import com.wavesplatform.wavesj.ApplicationStatus;
import com.wavesplatform.wavesj.Block;
import com.wavesplatform.wavesj.BlockHeaders;
import com.wavesplatform.wavesj.exceptions.NodeException;
import com.wavesplatform.wavesj.info.TransactionInfo;
import com.wavesplatform.wavesj.info.TransactionWithStatus;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

public class BlocksTest extends BaseTestWithNodeInDocker {

    @Test
    void height() throws IOException, NodeException {
        node.waitForHeight(4);

        long blockTimestamp = node.getBlockHeaders(3).timestamp();

        assertThat(node.getBlockHeight(blockTimestamp)).isEqualTo(3);
        assertThat(node.getBlockHeight(blockTimestamp - 1)).isEqualTo(2);
    }

    @Test
    void blocks() throws IOException, NodeException {
        Base58String blockIdAtHeight2 = node.getBlockHeaders(2).id();

        BlockHeaders headers = node.getBlockHeaders(2);
        Block block = node.getBlock(2);

        assertAll("All headers are equal",
                () -> assertThat(headers.id()).isEqualTo(block.id()),
                () -> assertThat(headers.height()).isEqualTo(block.height()),
                () -> assertThat(headers.baseTarget()).isEqualTo(block.baseTarget()),
                () -> assertThat(headers.desiredReward()).isEqualTo(block.desiredReward()),
                () -> assertThat(headers.generationSignature()).isEqualTo(block.generationSignature()),
                () -> assertThat(headers.features()).isEqualTo(block.features()),
                () -> assertThat(headers.generator()).isEqualTo(block.generator()),
                () -> assertThat(headers.reference()).isEqualTo(block.reference()),
                () -> assertThat(headers.reward()).isEqualTo(block.reward()),
                () -> assertThat(headers.signature()).isEqualTo(block.signature()),
                () -> assertThat(headers.size()).isEqualTo(block.size()),
                () -> assertThat(headers.timestamp()).isEqualTo(block.timestamp()),
                () -> assertThat(headers.totalFee()).isEqualTo(block.totalFee()),
                () -> assertThat(headers.transactionsCount()).isEqualTo(block.transactionsCount()),
                () -> assertThat(headers.transactionsRoot()).isEqualTo(block.transactionsRoot()),
                () -> assertThat(headers.version()).isEqualTo(block.version()),
                () -> assertThat(headers.vrf()).isEqualTo(block.vrf()));

        assertThat(node.getBlock(blockIdAtHeight2)).isEqualTo(block);
        assertThat(node.getBlocks(2, 5)).contains(block);

        assertThat(node.getBlockHeaders(blockIdAtHeight2)).isEqualTo(headers);
        assertThat(node.getBlocksHeaders(2, 5)).contains(headers);
    }

    @Test
    void lastBlock() throws IOException, NodeException {
        PrivateKey alice = createAccountWithBalance(DataTransaction.MIN_FEE);

        int txHeight = node.waitForTransaction(node.broadcast(
                DataTransaction.builder(StringEntry.as("foo", "bar")).getSignedWith(alice)).id())
                .height();

        Block block = node.getLastBlock();
        BlockHeaders headers = node.getLastBlockHeaders();

        assumeTrue(block.id().equals(headers.id()), "Failed to request the same block with two requests");
        assumeTrue(txHeight == block.height(), "Failed to catch the sent tx on current height");

        assertAll("All headers are equal",
                () -> assertThat(headers.id()).isEqualTo(block.id()),
                () -> assertThat(headers.height()).isEqualTo(block.height()),
                () -> assertThat(headers.baseTarget()).isEqualTo(block.baseTarget()),
                () -> assertThat(headers.desiredReward()).isEqualTo(block.desiredReward()),
                () -> assertThat(headers.generationSignature()).isEqualTo(block.generationSignature()),
                () -> assertThat(headers.features()).isEqualTo(block.features()),
                () -> assertThat(headers.generator()).isEqualTo(block.generator()),
                () -> assertThat(headers.reference()).isEqualTo(block.reference()),
                () -> assertThat(headers.reward()).isEqualTo(block.reward()),
                () -> assertThat(headers.signature()).isEqualTo(block.signature()),
                () -> assertThat(headers.size()).isEqualTo(block.size()),
                () -> assertThat(headers.timestamp()).isEqualTo(block.timestamp()),
                () -> assertThat(headers.totalFee()).isEqualTo(block.totalFee()),
                () -> assertThat(headers.transactionsCount()).isEqualTo(block.transactionsCount()),
                () -> assertThat(headers.transactionsRoot()).isEqualTo(block.transactionsRoot()),
                () -> assertThat(headers.version()).isEqualTo(block.version()),
                () -> assertThat(headers.vrf()).isEqualTo(block.vrf()));

        assertThat(block.fee()).isPositive();
        assertThat(block.transactions()).isNotEmpty();
    }

    @Test
    void genesisBlock() throws IOException, NodeException {
        Block genesis = node.getGenesisBlock();

        assertThat(genesis).isEqualTo(node.getBlock(1));
    }

    @Test
    void blocksGeneratedByAddress() throws IOException, NodeException {
        List<Block> blocks = node.getBlocksGeneratedBy(faucet.address(), 2, 3);

        assertThat(blocks).containsExactlyInAnyOrder(node.getBlock(2), node.getBlock(3));
    }

    @Test
    void blocksDelay() throws IOException, NodeException {
        Base58String blockIdAtStart = node.getBlockHeaders(node.getHeight() - 1).id();
        node.waitBlocks(2);

        assertThat(node.getBlocksDelay(blockIdAtStart, 3)).isGreaterThan(1000);
    }

    @Test
    void transactions() throws IOException, NodeException {
        PrivateKey alice = createAccountWithBalance(10_00000000);
        Address bob = createAccountWithBalance(10_00000000).address();

        TransactionInfo transfer = node.waitForTransaction(node.broadcast(
                TransferTransaction.builder(bob, Amount.of(10)).getSignedWith(alice)).id());
        TransactionInfo leasing = node.waitForTransaction(node.broadcast(
                LeaseTransaction.builder(bob, 20).getSignedWith(alice)).id());

        TransactionWithStatus transferInBlock = node.getBlock(transfer.height())
                .transactions()
                .stream()
                .filter(i -> transfer.tx().id().equals(i.tx().id()))
                .findFirst()
                .orElseThrow(() -> new IOException("Can't find transfer tx at height " + transfer.height()));
        TransactionWithStatus leasingInBlock = node.getBlocks(leasing.height(), leasing.height() + 1)
                .get(0)
                .transactions()
                .stream()
                .filter(i -> leasing.tx().id().equals(i.tx().id()))
                .findFirst()
                .orElseThrow(() -> new IOException("Can't find leasing tx at height " + leasing.height()));

        assertThat(transferInBlock.tx()).isInstanceOf(TransferTransaction.class);
        assertThat(leasingInBlock.tx()).isInstanceOf(LeaseTransaction.class);
        assertThat(transferInBlock).isEqualTo(new TransactionWithStatus(transfer.tx(), ApplicationStatus.SUCCEEDED));
        assertThat(leasingInBlock).isEqualTo(new TransactionWithStatus(leasing.tx(), ApplicationStatus.SUCCEEDED));
        assertThat(transferInBlock.tx(TransferTransaction.class).amount())
                .isEqualTo(transfer.tx(TransferTransaction.class).amount());
        assertThat(leasingInBlock.tx(LeaseTransaction.class).amount())
                .isEqualTo(leasing.tx(LeaseTransaction.class).amount());
    }

    @Test
    void leasingInBlock() throws IOException, NodeException {
        PrivateKey alice = createAccountWithBalance(1000000);
        PrivateKey bob = createAccountWithBalance(1000000);

        // 1. lease

        LeaseTransaction leaseTx = LeaseTransaction.builder(bob.address(), 10000).getSignedWith(alice);
        TransactionInfo leaseTxInfo = node.waitForTransaction(node.broadcast(leaseTx).id());

        TransactionWithStatus leaseTxInBlock = node.getBlock(leaseTxInfo.height())
                .transactions().stream()
                .filter(t -> t.tx().id().equals(leaseTx.id()))
                .findFirst().orElseThrow(AssertionError::new);
        TransactionWithStatus leaseTxInBlocksSeq = node.getBlocks(leaseTxInfo.height(), leaseTxInfo.height())
                .get(0).transactions().stream()
                .filter(t -> t.tx().id().equals(leaseTx.id()))
                .findFirst().orElseThrow(AssertionError::new);

        assertThat(leaseTxInBlock).isEqualTo(leaseTxInBlocksSeq);
        assertThat(leaseTxInBlock).isInstanceOf(TransactionWithStatus.class);
        assertThat(leaseTxInBlock).isEqualTo(new TransactionWithStatus(leaseTx, ApplicationStatus.SUCCEEDED));

        // 2. cancel

        LeaseCancelTransaction cancelTx = LeaseCancelTransaction.builder(leaseTx.id()).getSignedWith(alice);
        TransactionInfo cancelTxInfo = node.waitForTransaction(node.broadcast(cancelTx).id());

        TransactionWithStatus cancelTxInBlock = node.getBlock(cancelTxInfo.height())
                .transactions().stream()
                .filter(t -> t.tx().id().equals(cancelTx.id()))
                .findFirst().orElseThrow(AssertionError::new);
        TransactionWithStatus cancelTxInBlocksSeq = node.getBlocks(cancelTxInfo.height(), cancelTxInfo.height())
                .get(0).transactions().stream()
                .filter(t -> t.tx().id().equals(cancelTx.id()))
                .findFirst().orElseThrow(AssertionError::new);

        assertThat(cancelTxInBlock).isEqualTo(cancelTxInBlocksSeq);
        assertThat(cancelTxInfo).isInstanceOf(TransactionWithStatus.class);
        assertThat(cancelTxInBlock).isEqualTo(new TransactionWithStatus(cancelTx, ApplicationStatus.SUCCEEDED));
    }

}
