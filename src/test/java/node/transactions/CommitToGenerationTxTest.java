package node.transactions;

import com.wavesplatform.transactions.CommitToGenerationTransaction;
import com.wavesplatform.transactions.common.Id;
import com.wavesplatform.wavesj.Node;
import com.wavesplatform.wavesj.Profile;
import com.wavesplatform.wavesj.exceptions.NodeException;
import com.wavesplatform.wavesj.info.CommitToGenerationTransactionInfo;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static com.wavesplatform.wavesj.ApplicationStatus.SUCCEEDED;
import static node.mock.util.MockHttpRsUtil.mockHttpClient;
import static node.mock.util.MockHttpRsUtil.mockTransactionInfoRs;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CommitToGenerationTxTest {

    private final Node node = new Node(Profile.LOCAL, mockHttpClient("src/test/resources/stub/addressesD.json"));

    private static final byte CHAIN_ID = 'D';

    public CommitToGenerationTxTest() throws NodeException, IOException {
    }

    @Test
    void readCommitToGenerationTransactionByIdTest() throws NodeException, IOException {
        mockTransactionInfoRs(node,
                "2r6kpnJbGqNTmLxi9cPuavgxqumGufnisnN8vhuSCpaX",
                "src/test/resources/stub/txs/commit_to_generation_tx.json"
        );

        CommitToGenerationTransactionInfo ctgTxInfo =
                (CommitToGenerationTransactionInfo) node.getTransactionInfo(
                        new Id("2r6kpnJbGqNTmLxi9cPuavgxqumGufnisnN8vhuSCpaX")
                );

        CommitToGenerationTransaction ctgTx = ctgTxInfo.tx();

        assertEquals(19, ctgTx.type());
        assertEquals("2r6kpnJbGqNTmLxi9cPuavgxqumGufnisnN8vhuSCpaX", ctgTx.id().encoded());
        assertEquals(100_00000, ctgTx.fee().value());
        assertEquals(1766488084192L, ctgTx.timestamp());
        assertEquals(1, ctgTx.version());
        assertEquals(CHAIN_ID, ctgTx.chainId());
        assertEquals(2941, ctgTx.generationPeriodStart());
        assertEquals("3FmjX4FAeDXE4ZdDj2JKxzE4QtbxaioXzxM", ctgTx.sender().address(CHAIN_ID).encoded());
        assertEquals("Bn21Eg8HbwZWZQMHXnTFnb64MhVjgH2HygDekQDbjjMq", ctgTx.sender().encoded());
        assertEquals("5t9zL1oqXW6kL3YUAuF8r4rKUaPwohPVrpMWR8Y1bAJtSMipP3TQJYZvpBFB7GWZwo",
                ctgTx.endorserPublicKey().encoded());
        assertEquals("u9CTxLENQWyd5egnHrnbnnDKy7mvtrWJfR1AuCL4e4e7uuRTobzxVy1SQBz7ayoY5mRfiTG3PR8niJPT3fdVfsD97EkagBha8ehrLAzdutWSLbiQF2VDwPfi7uvTcp5csMC",
                ctgTx.commitmentSignature().encoded());
        assertEquals(73, ctgTxInfo.height());
        assertEquals(SUCCEEDED, ctgTxInfo.applicationStatus());
    }
}
