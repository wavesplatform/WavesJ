package node.transactions;

import com.wavesplatform.transactions.CommitToGenerationTransaction;
import com.wavesplatform.transactions.EthereumTransaction;
import com.wavesplatform.transactions.account.PrivateKey;
import com.wavesplatform.transactions.common.Base64String;
import com.wavesplatform.transactions.common.Id;
import com.wavesplatform.wavesj.Node;
import com.wavesplatform.wavesj.Profile;
import com.wavesplatform.wavesj.exceptions.NodeException;
import com.wavesplatform.wavesj.info.CommitToGenerationTransactionInfo;
import com.wavesplatform.wavesj.info.EthereumTransactionInfo;
import org.bouncycastle.util.encoders.Base64;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static com.wavesplatform.wavesj.ApplicationStatus.SUCCEEDED;
import static node.mock.util.MockHttpRsUtil.mockHttpClient;
import static node.mock.util.MockHttpRsUtil.mockTransactionInfoRs;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CommitToGenerationTxTest {

    private final Node node = new Node(Profile.LOCAL, mockHttpClient("src/test/resources/stub/addressesD.json"));

    private static final byte CHAIN_ID = 'D';

    public CommitToGenerationTxTest() throws NodeException, IOException {
    }

    @Test
    void readCommitToGenerationTransactionByIdTest() throws NodeException, IOException {
        mockTransactionInfoRs(node,
                "EZk7kMzYMDipnRR8WY8ZF2JzLD1fDRMk73jXGWuyipBx",
                "src/test/resources/stub/txs/commit_to_generation_tx.json"
        );

        CommitToGenerationTransactionInfo ctgTxInfo =
                (CommitToGenerationTransactionInfo) node.getTransactionInfo(
                        new Id("EZk7kMzYMDipnRR8WY8ZF2JzLD1fDRMk73jXGWuyipBx")
                );

        CommitToGenerationTransaction ctgTx = ctgTxInfo.tx();

//        assertEquals(19, ctgTx.type());
//        assertEquals("EZk7kMzYMDipnRR8WY8ZF2JzLD1fDRMk73jXGWuyipBx", ctgTx.id().encoded());
//        assertEquals(100_00000, ctgTx.fee().value());
//        assertEquals(1760440134127L, ctgTx.timestamp());
//        assertEquals(1, ctgTx.version());
//        assertEquals(CHAIN_ID, ctgTx.chainId());
//        assertEquals(8370, ctgTx.generationPeriodStart());
//        assertEquals("3FZs7eqZSmG56AqfTdWj2wJ1NWTrUZNgS6Q", ctgTx.sender().address(CHAIN_ID).encoded());
//        //assertEquals("2JYMTjUK7tC8NQi6TD6oWgy41YbrnXuoLzZydrFKTKt6", ctgTx.sender().encoded());
//        assertEquals("6TGkDGP9my3dLeU1LphyCsYDcxeqfiz6xbFdNPG38PdQwEuUZeBTDr8QTTiR3XvMKR",
//                ctgTx.endorserPublicKey().encoded());
//        assertEquals("26DBpJeCPNrvD1q6mhNDcaHHbXg86tcbzfqpswbydjDcPNGSHVmPZozfcPh4z7NpBsz5pvHd9g4p2YqyYFXKpUTZAAHh1cDZbbB25HAE7a57NymCQewnTTQEEC2SCEY4U6qX",
//                ctgTx.commitmentSignature().encoded());
//        assertEquals(73, ctgTxInfo.height());
//        assertEquals(SUCCEEDED, ctgTxInfo.applicationStatus());
//        System.out.println(new Base64String(ctgTx.bodyBytes()));
//        System.out.println(new Base64String(ctgTx.toBytes()));
    }
}
