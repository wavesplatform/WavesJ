package node.transactions;

import com.wavesplatform.transactions.EthereumTransaction;
import com.wavesplatform.transactions.common.ChainId;
import com.wavesplatform.transactions.common.Id;
import com.wavesplatform.transactions.data.DataEntry;
import com.wavesplatform.transactions.data.StringEntry;
import com.wavesplatform.transactions.invocation.Arg;
import com.wavesplatform.transactions.invocation.ArgType;
import com.wavesplatform.transactions.invocation.StringArg;
import com.wavesplatform.wavesj.ApplicationStatus;
import com.wavesplatform.wavesj.Block;
import com.wavesplatform.wavesj.Node;
import com.wavesplatform.wavesj.Profile;
import com.wavesplatform.wavesj.exceptions.NodeException;
import com.wavesplatform.wavesj.info.EthereumTransactionInfo;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

//todo rewrite with private node (right now mock response from node)
//todo move bytes attribute to inner transaction
public class EthereumTransactionTest {

    private final Node node = new Node(Profile.STAGENET);

    public EthereumTransactionTest() throws NodeException, IOException {
    }

    @Test
    void readEthereumTransferTransactionByIdTest() throws NodeException, IOException {
        EthereumTransactionInfo ethTransferTxInfo =
                (EthereumTransactionInfo) node.getTransactionInfo(
                        new Id("Ba4pFx78Ueg3j6CZqjhuBdg5cjxTwTzJJSS6GPpH3Cn4")
                );

        EthereumTransaction ethTransferTx = ethTransferTxInfo.tx();
        EthereumTransaction.Transfer payload = (EthereumTransaction.Transfer) ethTransferTx.payload();

        assertEquals(18, ethTransferTx.type());
        assertEquals("Ba4pFx78Ueg3j6CZqjhuBdg5cjxTwTzJJSS6GPpH3Cn4", ethTransferTx.id().encoded());
        assertEquals("", ethTransferTx.fee().assetId().encoded());
        assertEquals(100000, ethTransferTx.fee().value());
        assertEquals(1634966428189L, ethTransferTx.timestamp());
        assertEquals(1, ethTransferTx.version());
        assertEquals(ChainId.STAGENET, ethTransferTx.chainId());
        assertEquals(transferBytes, ethTransferTxInfo.getBytes());
        assertEquals("3MXSTprW6rt1baSkKcqXSqqrXFG9Hod6Zg4", ethTransferTx.sender().address().encoded());
        assertEquals("4WcqqW7mkz7AaBgpWQXbewk3wPSHZGqGj38d8HQZe1Umud2HXFswbGhZyoHZWd3thLjz4KW22JM5SB4yyzGiWjNx",
                ethTransferTx.sender().encoded());
        assertEquals(1043438, ethTransferTxInfo.height());
        assertEquals(ApplicationStatus.SUCCEEDED, ethTransferTxInfo.applicationStatus());
        assertTrue(ethTransferTxInfo.isTransferTransaction());
        assertEquals("", payload.amount().assetId().encoded());
        assertEquals(100000000, payload.amount().value());
        assertEquals("3Mi63XiwniEj6mTC557pxdRDddtpj7fZMMw", payload.recipient().encoded());
    }

    @Test
    public void readEthereumTransferTransactionFromBlockTest() throws NodeException, IOException {
        Block block = node.getBlock(1043438);
        EthereumTransaction ethTransferTx = (EthereumTransaction) block.transactions().get(0).tx();
        assertEquals("Ba4pFx78Ueg3j6CZqjhuBdg5cjxTwTzJJSS6GPpH3Cn4", ethTransferTx.id().encoded());

        assertEquals(18, ethTransferTx.type());
        assertEquals("Ba4pFx78Ueg3j6CZqjhuBdg5cjxTwTzJJSS6GPpH3Cn4", ethTransferTx.id().encoded());
        assertEquals(100000, ethTransferTx.fee().value());
        assertEquals("", ethTransferTx.fee().assetId().encoded());
        assertEquals(1634966428189L, ethTransferTx.timestamp());
        assertEquals(1, ethTransferTx.version());
        assertEquals(ChainId.STAGENET, ethTransferTx.chainId());
        //bytes
        assertEquals("3MXSTprW6rt1baSkKcqXSqqrXFG9Hod6Zg4", ethTransferTx.sender().address().encoded());
        assertEquals("4WcqqW7mkz7AaBgpWQXbewk3wPSHZGqGj38d8HQZe1Umud2HXFswbGhZyoHZWd3thLjz4KW22JM5SB4yyzGiWjNx",
                ethTransferTx.sender().encoded());
        // application status
    }

    @Test
    void readEthereumInvokeTransactionByIdTest() throws NodeException, IOException {
        EthereumTransactionInfo ethInvokeTxInfo =
                (EthereumTransactionInfo) node.getTransactionInfo(
                        new Id("CWuFY42te67sLmc5gwt4NxwHmFjVfJdHkKuLyshTwEct")
                );

        EthereumTransaction ethInvokeTx = ethInvokeTxInfo.tx();
        EthereumTransaction.Invocation payload = (EthereumTransaction.Invocation) ethInvokeTx.payload();

        assertEquals(18, ethInvokeTx.type());
        assertEquals("CWuFY42te67sLmc5gwt4NxwHmFjVfJdHkKuLyshTwEct", ethInvokeTx.id().encoded());
        assertEquals("", ethInvokeTx.fee().assetId().encoded());
        assertEquals(500000, ethInvokeTx.fee().value());
        assertEquals(1634983329302L, ethInvokeTx.timestamp());
        assertEquals(1, ethInvokeTx.version());
        assertEquals(ChainId.STAGENET, ethInvokeTx.chainId());
        assertEquals(invokeBytes, ethInvokeTxInfo.getBytes());
        assertEquals("3MbhUcL94QzSwkgRztUrdh9E5kwRpRK7Tp6", ethInvokeTx.sender().address().encoded());
        assertEquals("2HAk5dPx7Jx7fwbehqA9JRM9de9E7ZXtxVA2u92vYAp9ttZQiVgChPwBdoJ7ck2wcXmgfGxiAK9a6PPmmtEZmhvd",
                ethInvokeTx.sender().encoded());
        assertEquals(1043725, ethInvokeTxInfo.height());
        assertEquals("succeeded", ethInvokeTxInfo.applicationStatus().toString().toLowerCase());
        assertTrue(ethInvokeTxInfo.isInvokeTransaction());
        assertEquals("3MRuzZVauiiX2DGwNyP8Tv7idDGUy1VG5bJ", payload.dApp().encoded());
        assertEquals("saveString", payload.function().name());
        List<Arg> args = payload.function().args();
        assertEquals(ArgType.STRING, args.get(0).type());
        assertEquals("test metamask2", ((StringArg) args.get(0)).value());

        DataEntry dataEntry = ethInvokeTxInfo.getStateChanges().data().get(0);
        assertEquals("str_1043725", dataEntry.key());
        assertEquals("string", dataEntry.type().name().toLowerCase());
        assertEquals("test metamask2", ((StringEntry) dataEntry).value());
    }

    @Test
    void readEthereumInvokeTransactionFromBlockTest() throws NodeException, IOException {

        Block block = node.getBlock(1043725);
        EthereumTransaction ethInvokeTx = (EthereumTransaction) block.transactions().get(0).tx();

        EthereumTransaction.Invocation payload = (EthereumTransaction.Invocation) ethInvokeTx.payload();

        assertEquals(18, ethInvokeTx.type());
        assertEquals("CWuFY42te67sLmc5gwt4NxwHmFjVfJdHkKuLyshTwEct", ethInvokeTx.id().encoded());
        assertEquals("", ethInvokeTx.fee().assetId().encoded());
        assertEquals(500000, ethInvokeTx.fee().value());
        assertEquals(1634983329302L, ethInvokeTx.timestamp());
        assertEquals(1, ethInvokeTx.version());
        assertEquals(ChainId.STAGENET, ethInvokeTx.chainId());
        //assertEquals(invokeBytes, ethInvokeTxInfo.getBytes());
        assertEquals("3MbhUcL94QzSwkgRztUrdh9E5kwRpRK7Tp6", ethInvokeTx.sender().address().encoded());
        assertEquals("2HAk5dPx7Jx7fwbehqA9JRM9de9E7ZXtxVA2u92vYAp9ttZQiVgChPwBdoJ7ck2wcXmgfGxiAK9a6PPmmtEZmhvd",
                ethInvokeTx.sender().encoded());
    }

    private static final String transferBytes = "0xf87486017cab97da1d8502540be400830186a094c01187f4ae820a0c956c48c06ee" +
            "73f85c373cc03880de0b6b3a76400008081c9a005860b476a8adbc65e1118a27f761059abaf2d097e9246cebec959c745375" +
            "07ba009f5ba7d8b87628e2a1c5d4e7f2a2a64f3815d88f1ea9cd0e396315a0b8f946b";

    private static final String invokeBytes = "0xf9011186017cac99be168502540be4008307a120940ea8e14f313237aac31995f9" +
            "c19a7e0f78c1cc2b80b8a409abf90e0000000000000000000000000000000000000000000000000000000000000040000000" +
            "0000000000000000000000000000000000000000000000000000000080000000000000000000000000000000000000000000" +
            "000000000000000000000e74657374206d6574616d61736b3200000000000000000000000000000000000000000000000000" +
            "0000000000000000000000000000000000000000000000000081c9a0dcc682194d46cd3a763b352ca77a4317e9d89f10e5213" +
            "379b55563cbc03619f3a02a2f26c580ab9f3d83db801bf7d556dd50d37cd69b19df8ee4a3488a6c5140c8";
}
