package node.transactions;

import com.wavesplatform.transactions.EthereumTransaction;
import com.wavesplatform.transactions.common.ChainId;
import com.wavesplatform.transactions.common.Id;
import com.wavesplatform.wavesj.Block;
import com.wavesplatform.wavesj.Node;
import com.wavesplatform.wavesj.Profile;
import com.wavesplatform.wavesj.exceptions.NodeException;
import com.wavesplatform.wavesj.info.EthereumTransactionInfo;
import com.wavesplatform.wavesj.info.TransactionInfo;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EthereumTransactionTest {

    @Test
    void readEthereumTransferTransactionTest() throws NodeException, IOException {
        Node node = new Node(Profile.STAGENET);
        Block block = node.getBlock(1043438);
        EthereumTransaction transactionWithStatus = (EthereumTransaction) block.transactions().get(0).tx();
        assertEquals("Ba4pFx78Ueg3j6CZqjhuBdg5cjxTwTzJJSS6GPpH3Cn4", transactionWithStatus.id().encoded());
        System.out.println(block);
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
        assertEquals("succeeded", ethTransferTxInfo.applicationStatus().toString().toLowerCase());
        assertTrue(ethTransferTxInfo.isTransferTransaction());
        assertEquals("", payload.amount().assetId().encoded());
        assertEquals(100000000, payload.amount().value());
        assertEquals("3Mi63XiwniEj6mTC557pxdRDddtpj7fZMMw", payload.recipient().encoded());
    }

    @Test
    @Disabled
    void readEthereumInvokeTransactionTest() throws NodeException, IOException {
        Node node = new Node(Profile.STAGENET);

        TransactionInfo ethInvokeTx = node.getTransactionInfo(new Id("CWuFY42te67sLmc5gwt4NxwHmFjVfJdHkKuLyshTwEct"));
        System.out.println(ethInvokeTx);
    }

    private static final String transferBytes = "0xf87486017cab97da1d8502540be400830186a094c01187f4ae820a0c956c48c06ee" +
            "73f85c373cc03880de0b6b3a76400008081c9a005860b476a8adbc65e1118a27f761059abaf2d097e9246cebec959c745375" +
            "07ba009f5ba7d8b87628e2a1c5d4e7f2a2a64f3815d88f1ea9cd0e396315a0b8f946b";
}
