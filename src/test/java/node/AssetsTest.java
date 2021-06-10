package node;

import base.BaseTestWithNodeInDocker;
import com.wavesplatform.crypto.Crypto;
import com.wavesplatform.transactions.IssueTransaction;
import com.wavesplatform.transactions.MassTransferTransaction;
import com.wavesplatform.transactions.account.Address;
import com.wavesplatform.transactions.account.PrivateKey;
import com.wavesplatform.transactions.common.AssetId;
import com.wavesplatform.transactions.common.Id;
import com.wavesplatform.transactions.mass.Transfer;
import com.wavesplatform.wavesj.AssetDistribution;
import com.wavesplatform.wavesj.exceptions.NodeException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.*;
import static org.assertj.core.api.Assertions.assertThat;

public class AssetsTest extends BaseTestWithNodeInDocker {

    //TODO issue assets with all versions
    //TODO issue from v4/v5 DApps + inner
    //TODO issue NFT

    //TODO all reissue versions
    //TODO reissue from v4/v5 DApps + inner

    //TODO all burn versions
    //TODO burn from v4/v5 DApps + inner

    //TODO update asset info

    //TODO sponsor fee all versions
    //TODO sponsor fee from v4/v5 DApps + inner

    //TODO Set Asset Script


    //TODO check everywhere: in blocks, assets, transactions

    @Test
    void distribution() throws IOException, NodeException {
        PrivateKey alice = createAccountWithBalance(10_00000000);

        int recipientsNumber = 1100;
        AssetId assetId = node.broadcast(IssueTransaction
                .builder("Asset", IntStream.range(1, recipientsNumber + 1).sum(), 2).getSignedWith(alice))
                .assetId();
        node.waitForTransaction(assetId);

        List<Transfer> transfersToDistribute = IntStream.range(1, recipientsNumber + 1)
                .mapToObj(amount ->
                        Transfer.to(PrivateKey.fromSeed(Crypto.getRandomSeedBytes()).address(), amount))
                .collect(toList());

        final AtomicInteger transfersCounter = new AtomicInteger();
        List<MassTransferTransaction> massTransferTxs = transfersToDistribute
                .stream()
                .collect(groupingBy(it ->
                        transfersCounter.getAndIncrement() / 100))
                .values()
                .stream()
                .map(transfers ->
                        MassTransferTransaction.builder(transfers).assetId(assetId).getSignedWith(alice))
                .collect(toList());

        List<Id> massTransferTxIds = massTransferTxs.stream().map(MassTransferTransaction::id).collect(toList());

        for (MassTransferTransaction massTransferTx : massTransferTxs)
            node.broadcast(massTransferTx);
        node.waitForTransactions(massTransferTxIds);

        node.waitBlocks(1);
        AssetDistribution distributionPage1 =
                node.getAssetDistribution(assetId, node.getHeight() - 1, 1000);
        AssetDistribution distributionPage2 =
                node.getAssetDistribution(assetId, node.getHeight() - 1, 1000, distributionPage1.lastItem());

        assertThat(distributionPage1.hasNext()).isTrue();
        assertThat(distributionPage2.hasNext()).isFalse();

        assertThat(distributionPage1.items()).containsKey(distributionPage1.lastItem());
        assertThat(distributionPage2.items()).containsKey(distributionPage2.lastItem());

        List<Transfer> items1 = new ArrayList<>();
        distributionPage1.items().forEach((k, v) -> items1.add(Transfer.to(k, v)));
        List<Transfer> items2 = new ArrayList<>();
        distributionPage2.items().forEach((k, v) -> items2.add(Transfer.to(k, v)));

        assertThat(transfersToDistribute).containsAll(items1);
        assertThat(transfersToDistribute).containsAll(items2);

        assertThat(distributionPage1.items())
                .doesNotContainKeys(distributionPage2.items().keySet().toArray(new Address[0]));

        assertThat(distributionPage1.items().size() + distributionPage2.items().size())
                .isEqualTo(transfersToDistribute.size());
    }

    @Test
    void balance() throws IOException, NodeException {
        PrivateKey alice = createAccountWithBalance(10_00000000);

        AssetId assetId1 = node.broadcast(IssueTransaction
                .builder("Asset 1", 10000000, 2)
                .getSignedWith(alice)
        ).assetId();
        node.waitForTransaction(assetId1);

        AssetId assetId2 = node.broadcast(IssueTransaction
                .builder("Asset 2", 10000000, 2)
                .getSignedWith(alice)
        ).assetId();
        node.waitForTransaction(assetId2);

        node.getAssetsBalance(alice.address());
        node.getAssetBalance(alice.address(), assetId1);
    }

    @Test
    void details() throws IOException, NodeException {
        PrivateKey alice = createAccountWithBalance(10_00000000);

        AssetId assetId1 = node.broadcast(IssueTransaction
                .builder("Asset 1", 10000000, 2)
                .getSignedWith(alice)
        ).assetId();
        node.waitForTransaction(assetId1);

        AssetId assetId2 = node.broadcast(IssueTransaction
                .builder("Asset 2", 10000000, 2)
                .getSignedWith(alice)
        ).assetId();
        node.waitForTransaction(assetId2);

        node.getAssetDetails(assetId1);
        node.getAssetsDetails(asList(assetId1, assetId2));
    }

    @Test
    void nft() throws IOException, NodeException {
        PrivateKey alice = createAccountWithBalance(10_00000000);
        //TODO issue many NFTs

        node.getNft(alice.address());
        node.getNft(alice.address(), 1);
        node.getNft(alice.address(), 1, AssetId.WAVES);
    }

}
