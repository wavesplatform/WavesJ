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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Arrays.asList;

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
        List<Address> addresses = IntStream.range(0, 1100)
                .mapToObj(i -> PrivateKey.fromSeed(Crypto.getRandomSeedBytes()).address())
                .collect(Collectors.toList());

        AssetId assetId = node.broadcast(IssueTransaction
                .builder("Asset", 10000000, 2)
                .getSignedWith(alice)
        ).assetId();
        node.waitForTransaction(assetId);

        node.waitBlocks(1);
        node.getAssetDistribution(assetId, node.getHeight() - 1);
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
