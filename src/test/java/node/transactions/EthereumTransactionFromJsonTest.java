package node.transactions;

import com.wavesplatform.transactions.EthereumTransaction;
import com.wavesplatform.transactions.ExchangeTransaction;
import com.wavesplatform.transactions.common.Id;
import com.wavesplatform.transactions.data.DataEntry;
import com.wavesplatform.transactions.data.EntryType;
import com.wavesplatform.transactions.data.StringEntry;
import com.wavesplatform.transactions.exchange.Order;
import com.wavesplatform.transactions.invocation.Arg;
import com.wavesplatform.transactions.invocation.ArgType;
import com.wavesplatform.transactions.invocation.StringArg;
import com.wavesplatform.wavesj.Block;
import com.wavesplatform.wavesj.Node;
import com.wavesplatform.wavesj.Profile;
import com.wavesplatform.wavesj.exceptions.NodeException;
import com.wavesplatform.wavesj.info.EthereumTransactionInfo;
import com.wavesplatform.wavesj.info.ExchangeTransactionInfo;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static com.wavesplatform.transactions.common.ChainId.STAGENET;
import static com.wavesplatform.transactions.exchange.OrderType.BUY;
import static com.wavesplatform.transactions.exchange.OrderType.SELL;
import static com.wavesplatform.wavesj.ApplicationStatus.SUCCEEDED;
import static node.mock.util.MockHttpRsUtil.*;
import static org.bouncycastle.util.encoders.Hex.toHexString;
import static org.junit.jupiter.api.Assertions.*;

//todo move bytes attribute to inner transaction
public class EthereumTransactionFromJsonTest {

    private final Node node = new Node(Profile.LOCAL, mockHttpClient("src/test/resources/stub/addresses.json"));

    private static final byte PRIVATE_NODE_CHAIN_ID = 'R';

    public EthereumTransactionFromJsonTest() throws NodeException, IOException {
    }

    @Test
    void readEthereumTransferTransactionByIdTest() throws NodeException, IOException {
        mockTransactionInfoRs(node,
                "Ba4pFx78Ueg3j6CZqjhuBdg5cjxTwTzJJSS6GPpH3Cn4",
                "src/test/resources/stub/txs/eth/eth_transfer_tx_info.json"
        );

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
        assertEquals(PRIVATE_NODE_CHAIN_ID, ethTransferTx.chainId());
        assertEquals(transferBytes, ethTransferTxInfo.getBytes());
        assertEquals("3M86rqkCp9hYim1cEbRC8MiajcktM16ogVx", ethTransferTx.sender().address(PRIVATE_NODE_CHAIN_ID).encoded());
        assertEquals("4WcqqW7mkz7AaBgpWQXbewk3wPSHZGqGj38d8HQZe1Umud2HXFswbGhZyoHZWd3thLjz4KW22JM5SB4yyzGiWjNx",
                ethTransferTx.sender().encoded());
        assertEquals(1043438, ethTransferTxInfo.height());
        assertEquals(SUCCEEDED, ethTransferTxInfo.applicationStatus());
        assertTrue(ethTransferTxInfo.isTransferTransaction());
        assertEquals("", payload.amount().assetId().encoded());
        assertEquals(100000000, payload.amount().value());
        assertEquals("3Mi63XiwniEj6mTC557pxdRDddtpj7fZMMw", payload.recipient().encoded());
    }

    @Test
    public void readEthereumTransferTransactionFromBlockTest() throws NodeException, IOException {
        mockGetBlockRs(
                node,
                1043438,
                "src/test/resources/stub/txs/eth/eth_transfer_tx_from_block.json"
        );

        Block block = node.getBlock(1043438);
        EthereumTransaction ethTransferTx = (EthereumTransaction) block.transactions().get(0).tx();
        assertEquals("Ba4pFx78Ueg3j6CZqjhuBdg5cjxTwTzJJSS6GPpH3Cn4", ethTransferTx.id().encoded());

        assertEquals(18, ethTransferTx.type());
        assertEquals("Ba4pFx78Ueg3j6CZqjhuBdg5cjxTwTzJJSS6GPpH3Cn4", ethTransferTx.id().encoded());
        assertEquals(100000, ethTransferTx.fee().value());
        assertEquals("", ethTransferTx.fee().assetId().encoded());
        assertEquals(1634966428189L, ethTransferTx.timestamp());
        assertEquals(1, ethTransferTx.version());
        assertEquals(PRIVATE_NODE_CHAIN_ID, ethTransferTx.chainId());
        //bytes
        assertEquals("3M86rqkCp9hYim1cEbRC8MiajcktM16ogVx", ethTransferTx.sender().address(PRIVATE_NODE_CHAIN_ID).encoded());
        assertEquals("4WcqqW7mkz7AaBgpWQXbewk3wPSHZGqGj38d8HQZe1Umud2HXFswbGhZyoHZWd3thLjz4KW22JM5SB4yyzGiWjNx",
                ethTransferTx.sender().encoded());
        // application status
    }

    @Test
    void readEthereumInvokeTransactionByIdTest() throws NodeException, IOException {
        mockTransactionInfoRs(node,
                "CWuFY42te67sLmc5gwt4NxwHmFjVfJdHkKuLyshTwEct",
                "src/test/resources/stub/txs/eth/eth_invoke_tx_info.json"
        );

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
        assertEquals(PRIVATE_NODE_CHAIN_ID, ethInvokeTx.chainId());
        assertEquals(invokeBytes, ethInvokeTxInfo.getBytes());
        assertEquals("3MCMsdDqmhoz4wFHus4XKD1xJ8SAsimgBTW", ethInvokeTx.sender().address(PRIVATE_NODE_CHAIN_ID).encoded());
        assertEquals("2HAk5dPx7Jx7fwbehqA9JRM9de9E7ZXtxVA2u92vYAp9ttZQiVgChPwBdoJ7ck2wcXmgfGxiAK9a6PPmmtEZmhvd",
                ethInvokeTx.sender().encoded());
        assertEquals(1043725, ethInvokeTxInfo.height());
        assertEquals(SUCCEEDED, ethInvokeTxInfo.applicationStatus());
        assertTrue(ethInvokeTxInfo.isInvokeTransaction());
        assertEquals("3MRuzZVauiiX2DGwNyP8Tv7idDGUy1VG5bJ", payload.dApp().encoded());
        assertEquals("saveString", payload.function().name());
        List<Arg> args = payload.function().args();
        assertEquals(ArgType.STRING, args.get(0).type());
        assertEquals("test metamask2", ((StringArg) args.get(0)).value());

        DataEntry dataEntry = ethInvokeTxInfo.getStateChanges().data().get(0);
        assertEquals("str_1043725", dataEntry.key());
        assertEquals(EntryType.STRING, dataEntry.type());
        assertEquals("test metamask2", ((StringEntry) dataEntry).value());
    }

    @Test
    void readEthereumInvokeTransactionFromBlockTest() throws NodeException, IOException {
        mockGetBlockRs(
                node,
                1043725,
                "src/test/resources/stub/txs/eth/eth_invoke_tx_from_block.json"
        );

        Block block = node.getBlock(1043725);
        EthereumTransaction ethInvokeTx = (EthereumTransaction) block.transactions().get(0).tx();

        assertEquals(18, ethInvokeTx.type());
        assertEquals("CWuFY42te67sLmc5gwt4NxwHmFjVfJdHkKuLyshTwEct", ethInvokeTx.id().encoded());
        assertEquals("", ethInvokeTx.fee().assetId().encoded());
        assertEquals(500000, ethInvokeTx.fee().value());
        assertEquals(1634983329302L, ethInvokeTx.timestamp());
        assertEquals(1, ethInvokeTx.version());
        assertEquals(PRIVATE_NODE_CHAIN_ID, ethInvokeTx.chainId());
        //assertEquals(invokeBytes, ethInvokeTxInfo.getBytes());
        assertEquals("3MCMsdDqmhoz4wFHus4XKD1xJ8SAsimgBTW", ethInvokeTx.sender().address(PRIVATE_NODE_CHAIN_ID).encoded());
        assertEquals("2HAk5dPx7Jx7fwbehqA9JRM9de9E7ZXtxVA2u92vYAp9ttZQiVgChPwBdoJ7ck2wcXmgfGxiAK9a6PPmmtEZmhvd",
                ethInvokeTx.sender().encoded());
    }

    @Test
    void readExchangeTransactionInfoWithEthereumSignatureTest() throws IOException, NodeException {
        mockTransactionInfoRs(node,
                "3ZPyxs4p7abkj5cf43pUkaHaMPRuBaWC89RX93d2AB6R",
                "src/test/resources/stub/txs/eth/exchange_tx_info_with_eth_sign.json"
        );

        ExchangeTransactionInfo exchangeTxInfo =
                node.getTransactionInfo(
                        new Id("3ZPyxs4p7abkj5cf43pUkaHaMPRuBaWC89RX93d2AB6R"),
                        ExchangeTransactionInfo.class
                );

        ExchangeTransaction exchangeTx = exchangeTxInfo.tx();
        assertEquals(7, exchangeTx.type());
        assertEquals("3ZPyxs4p7abkj5cf43pUkaHaMPRuBaWC89RX93d2AB6R", exchangeTx.id().encoded());
        assertEquals(300000, exchangeTx.fee().value());
        assertEquals("", exchangeTx.fee().assetId().encoded());
        assertEquals(1652441936773L, exchangeTx.timestamp());
        assertEquals(3, exchangeTx.version());
        assertEquals(PRIVATE_NODE_CHAIN_ID, exchangeTx.chainId());
        assertEquals("3MNUQ7dgUpRVnY84PJHy33rMPD8v4zm3FDJ", exchangeTx.sender().address(PRIVATE_NODE_CHAIN_ID).encoded());
        assertEquals("CsdeJPgJebkW4tsvAaWtfzNvaFd7nWJUGiHrqrtEGN9p", exchangeTx.sender().encoded());
        assertEquals("3vbCSYNC6T3BrooeULFpHewphHKLV8p6cDz9NFYQEpkGzA8HCaL7EDTYRYEyNs26AiqS1gdHnxb4H6AYUqdmH8pd",
                exchangeTx.proofs().get(0).encoded());

        Order order1 = exchangeTx.buyOrder();

        assertEquals(4, order1.version());
        assertEquals("GozxekHa86f9TqMiibckWKJR8V2FJzYuksBV2ESWVyHF", order1.id().encoded());
        assertEquals("3M6zKVa3cshzkPRRobLaZjzkrT4fHtuSn3Z", order1.sender().address(PRIVATE_NODE_CHAIN_ID).encoded());
        assertEquals("4USoKrfmyQ2xFB8jSjpF95Ma2RFgNDkAbk3td5PhEjrk", order1.sender().encoded());
        assertEquals("CsdeJPgJebkW4tsvAaWtfzNvaFd7nWJUGiHrqrtEGN9p", order1.matcher().encoded());
        assertEquals("CDyHKz5S5dmnBceTxix1cGQpetXAwWVXKiez2TpvpVLw", order1.assetPair().left().encoded());
        assertEquals("", order1.assetPair().right().encoded());
        assertEquals(BUY, order1.type());
        assertEquals(10000, order1.amount().value());
        assertEquals(10000, order1.price().value());
        assertEquals(1652441936773L, order1.timestamp());
        assertEquals(1653881936773L, order1.expiration());
        assertEquals(300000, order1.fee().value());
        assertEquals("5qneeJ2va7M8uKhy93ycqWeWstNb4FDSSeV86ZHgFrykDpffcqdEQrq1Ux8XHP18a3eMwA6HHCzr19sdQDywfYNU",
                order1.proofs().get(0).encoded());
        assertEquals("", order1.fee().assetId().encoded());
        assertNull(order1.eip712Signature());
        //priceMode

        Order order2 = exchangeTx.sellOrder();
        assertEquals(4, order2.version());
        assertEquals("3qnirddnVRKeFR84RPdxDUeFmqcV3SGyQpwgkgEz6SYM", order2.id().encoded());
        assertEquals("3M2jsLHQgaewRmP6BChR2LEjpsef2VZSixf", order2.sender().address(PRIVATE_NODE_CHAIN_ID).encoded());
        assertEquals(
                "26Djzne1dkfLBMu6nkf4RS9TETmGhMRiaoao1nEkhj5NeqSmwkGXr9WP6zAU4h1rZFSTjgn3EkfTUonZutRNEBSm",
                order2.sender().encoded()
        );
        assertEquals("CsdeJPgJebkW4tsvAaWtfzNvaFd7nWJUGiHrqrtEGN9p", order2.matcher().encoded());
        assertEquals("CDyHKz5S5dmnBceTxix1cGQpetXAwWVXKiez2TpvpVLw", order2.assetPair().left().encoded());
        assertEquals("", order2.assetPair().right().encoded());
        assertEquals(SELL, order2.type());
        assertEquals(10000, order2.amount().value());
        assertEquals(10000, order2.price().value());
        assertEquals(1652441936773L, order2.timestamp());
        assertEquals(1653881936773L, order2.expiration());
        assertEquals(300000, order2.fee().value());
        assertTrue(order2.proofs().isEmpty());
        assertEquals("", order2.fee().assetId().encoded());
        assertEquals(
                "0xc44dea13cefb97c71298e73afb1ef8fa18e75b41264554d54c755c54d90df4fe6678fd267c1cb1063c" +
                        "a73a8ef44bc368d8131a9a936ca4a00f4b7f98fcd9f06e1c",
                "0x" + toHexString(order2.eip712Signature()));
        //priceMode
        assertEquals(10000, exchangeTx.amount());
        assertEquals(10000, exchangeTx.price());
        assertEquals(300000, exchangeTx.buyMatcherFee());
        assertEquals(300000, exchangeTx.sellMatcherFee());
        assertEquals(1099637, exchangeTxInfo.height());
        assertEquals(SUCCEEDED, exchangeTxInfo.applicationStatus());
    }

    @Test
    void readExchangeTransactionWithEthereumSignatureFormBlockTest() throws IOException, NodeException {
        mockGetBlockRs(
                node,
                1099637,
                "src/test/resources/stub/txs/eth/exchange_tx_with_eth_sign_from_block.json"
        );

        ExchangeTransaction exchangeTx =
                (ExchangeTransaction) node.getBlock(1099637).transactions().get(0).tx();

        assertEquals(7, exchangeTx.type());
        assertEquals("3ZPyxs4p7abkj5cf43pUkaHaMPRuBaWC89RX93d2AB6R", exchangeTx.id().encoded());
        assertEquals(300000, exchangeTx.fee().value());
        assertEquals("", exchangeTx.fee().assetId().encoded());
        assertEquals(1652441936773L, exchangeTx.timestamp());
        assertEquals(3, exchangeTx.version());
        assertEquals(PRIVATE_NODE_CHAIN_ID, exchangeTx.chainId());
        assertEquals("3MNUQ7dgUpRVnY84PJHy33rMPD8v4zm3FDJ", exchangeTx.sender().address(PRIVATE_NODE_CHAIN_ID).encoded());
        assertEquals("CsdeJPgJebkW4tsvAaWtfzNvaFd7nWJUGiHrqrtEGN9p", exchangeTx.sender().encoded());
        assertEquals("3vbCSYNC6T3BrooeULFpHewphHKLV8p6cDz9NFYQEpkGzA8HCaL7EDTYRYEyNs26AiqS1gdHnxb4H6AYUqdmH8pd",
                exchangeTx.proofs().get(0).encoded());

        Order order1 = exchangeTx.buyOrder();

        assertEquals(4, order1.version());
        assertEquals("GozxekHa86f9TqMiibckWKJR8V2FJzYuksBV2ESWVyHF", order1.id().encoded());
        assertEquals("3M6zKVa3cshzkPRRobLaZjzkrT4fHtuSn3Z", order1.sender().address(PRIVATE_NODE_CHAIN_ID).encoded());
        assertEquals("4USoKrfmyQ2xFB8jSjpF95Ma2RFgNDkAbk3td5PhEjrk", order1.sender().encoded());
        assertEquals("CsdeJPgJebkW4tsvAaWtfzNvaFd7nWJUGiHrqrtEGN9p", order1.matcher().encoded());
        assertEquals("CDyHKz5S5dmnBceTxix1cGQpetXAwWVXKiez2TpvpVLw", order1.assetPair().left().encoded());
        assertEquals("", order1.assetPair().right().encoded());
        assertEquals(BUY, order1.type());
        assertEquals(10000, order1.amount().value());
        assertEquals(10000, order1.price().value());
        assertEquals(1652441936773L, order1.timestamp());
        assertEquals(1653881936773L, order1.expiration());
        assertEquals(300000, order1.fee().value());
        assertEquals("5qneeJ2va7M8uKhy93ycqWeWstNb4FDSSeV86ZHgFrykDpffcqdEQrq1Ux8XHP18a3eMwA6HHCzr19sdQDywfYNU",
                order1.proofs().get(0).encoded());
        assertEquals("", order1.fee().assetId().encoded());
        assertNull(order1.eip712Signature());
        //priceMode

        Order order2 = exchangeTx.sellOrder();
        assertEquals(4, order2.version());
        assertEquals("3qnirddnVRKeFR84RPdxDUeFmqcV3SGyQpwgkgEz6SYM", order2.id().encoded());
        assertEquals("3M2jsLHQgaewRmP6BChR2LEjpsef2VZSixf", order2.sender().address(PRIVATE_NODE_CHAIN_ID).encoded());
        assertEquals(
                "26Djzne1dkfLBMu6nkf4RS9TETmGhMRiaoao1nEkhj5NeqSmwkGXr9WP6zAU4h1rZFSTjgn3EkfTUonZutRNEBSm",
                order2.sender().encoded()
        );
        assertEquals("CsdeJPgJebkW4tsvAaWtfzNvaFd7nWJUGiHrqrtEGN9p", order2.matcher().encoded());
        assertEquals("CDyHKz5S5dmnBceTxix1cGQpetXAwWVXKiez2TpvpVLw", order2.assetPair().left().encoded());
        assertEquals("", order2.assetPair().right().encoded());
        assertEquals(SELL, order2.type());
        assertEquals(10000, order2.amount().value());
        assertEquals(10000, order2.price().value());
        assertEquals(1652441936773L, order2.timestamp());
        assertEquals(1653881936773L, order2.expiration());
        assertEquals(300000, order2.fee().value());
        assertTrue(order2.proofs().isEmpty());
        assertEquals("", order2.fee().assetId().encoded());
        assertEquals(
                "0xc44dea13cefb97c71298e73afb1ef8fa18e75b41264554d54c755c54d90df4fe6678fd267c1cb1063c" +
                        "a73a8ef44bc368d8131a9a936ca4a00f4b7f98fcd9f06e1c",
                "0x" + toHexString(order2.eip712Signature()));
        //priceMode
        assertEquals(10000, exchangeTx.amount());
        assertEquals(10000, exchangeTx.price());
        assertEquals(300000, exchangeTx.buyMatcherFee());
        assertEquals(300000, exchangeTx.sellMatcherFee());
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
