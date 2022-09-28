package node.transactions;

import com.google.protobuf.ByteString;
import com.wavesplatform.crypto.base.Base58;
import com.wavesplatform.crypto.base.Base64;
import com.wavesplatform.events.protobuf.Events.TransactionMetadata;
import com.wavesplatform.events.protobuf.Events.TransactionMetadata.EthereumMetadata;
import com.wavesplatform.protobuf.AmountOuterClass.Amount;
import com.wavesplatform.protobuf.transaction.InvokeScriptResultOuterClass.InvokeScriptResult.Call.Argument;
import com.wavesplatform.protobuf.transaction.TransactionOuterClass.SignedTransaction;
import com.wavesplatform.transactions.EthereumTransaction;
import com.wavesplatform.transactions.EthereumTransaction.Invocation;
import com.wavesplatform.transactions.common.ChainId;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EthereumTransactionFromProtobufTest {

    @Test
    public void readEthereumTransferTransactionFromPb() {
        byte[] decode = Base58.decode("2mXfCNgXmQhpaR14kr5Vg1s7BBDPCx9jYHLAK18narLsRYJEUypk" +
                "hYKR7oJR8xne4cBP13NLZvAdWKsvmMFvagrQR9yUdP8PVZjDDmyLEwDBJDdKjWNz1f" +
                "zzK3nao3BWFubTEk6q21zUYJw9FVse6A5Qm14QLmREga");
        SignedTransaction signedTransaction = SignedTransaction
                .newBuilder()
                .setEthereumTransaction(
                        ByteString.copyFrom(decode)
                )
                .build();

        EthereumTransaction ethTransferTransaction = EthereumTransaction.transferTxfromProtobuf(signedTransaction);

        assertEquals("Ba4pFx78Ueg3j6CZqjhuBdg5cjxTwTzJJSS6GPpH3Cn4", ethTransferTransaction.id().encoded());
        assertEquals(18, ethTransferTransaction.type());
        assertEquals(1, ethTransferTransaction.version());
        assertEquals(83, ethTransferTransaction.chainId());
        assertEquals(
                "4WcqqW7mkz7AaBgpWQXbewk3wPSHZGqGj38d8HQZe1Umud2HXFswbGhZyoHZWd3thLjz4KW22JM5SB4yyzGiWjNx",
                ethTransferTransaction.sender().encoded()
        );
        assertEquals(
                "3MXSTprW6rt1baSkKcqXSqqrXFG9Hod6Zg4",
                ethTransferTransaction.sender().address(ChainId.STAGENET).encoded()
        );

        EthereumTransaction.Transfer payload = (EthereumTransaction.Transfer) ethTransferTransaction.payload();

        assertEquals("3Mi63XiwniEj6mTC557pxdRDddtpj7fZMMw", payload.recipient().encoded());
        assertEquals(100000000L, payload.amount().value());
        assertEquals("", payload.amount().assetId().encoded());
        assertEquals(100000, ethTransferTransaction.fee().value());
        assertEquals("", ethTransferTransaction.fee().assetId().encoded());
        assertEquals(1634966428189L, ethTransferTransaction.timestamp());
    }

    @Test
    public void readEthereumInvokeScriptTransactionWithPaymentFromPb() throws IOException {
        SignedTransaction signedTransaction = SignedTransaction
                .newBuilder()
                .setEthereumTransaction(
                        ByteString.copyFrom(Base58.decode(signedInvokeTxWithPayments))
                )
                .build();


        EthereumTransaction ethInvokeScriptTransaction = EthereumTransaction.invokeScriptTxfromProtobuf(
                signedTransaction,
                buildInvokeScriptWithPaymentsMetadata()
        );

        assertEquals("HDuM4GXQrBpHeCnk4WDMkEkktwEi4caniMA61gcGsEUh", ethInvokeScriptTransaction.id().encoded());
        assertEquals(18, ethInvokeScriptTransaction.type());
        assertEquals(1, ethInvokeScriptTransaction.version());
        assertEquals(83, ethInvokeScriptTransaction.chainId());
        assertEquals(
                "5HxN2EUK3WQrfYBdWqVByPwVe7qYtEcrQhQGYqqqZZiTRSapUWkvGAqVmqXJ2TvFgW2gAYk7d1bNz9bHjAytdHV9",
                ethInvokeScriptTransaction.sender().encoded()
        );
        assertEquals(
                "3MUCDguXmP55K7ZE1B7xagYbEh9CNpzBRTJ",
                ethInvokeScriptTransaction.sender().address(ChainId.STAGENET).encoded()
        );

        Invocation payload = (Invocation) ethInvokeScriptTransaction.payload();

        assertEquals("", payload.payments().get(0).assetId().encoded());
        assertEquals(300000000, payload.payments().get(0).value());
        assertEquals("call", payload.function().name());
        assertEquals("3MinbjWtnaj293jv5zZEt2ZkgVj66EsJhRP", payload.dApp().encoded());
        assertEquals(14, payload.function().args().size());
    }

    @Test
    public void readEthereumInvokeScriptTransactionWithoutPaymentFromPb() throws IOException {
        SignedTransaction signedTransaction = SignedTransaction
                .newBuilder()
                .setEthereumTransaction(
                        ByteString.copyFrom(Base58.decode(signedInvokeTxWithoutPayments))
                )
                .build();


        EthereumTransaction ethInvokeScriptTransaction = EthereumTransaction.invokeScriptTxfromProtobuf(
                signedTransaction,
                buildInvokeScriptWithoutPaymentsMetadata()
        );

        assertEquals("HmwNwr7189HdwRHCYYcEq8mmNQjjCooqnjuSbCWccHfg", ethInvokeScriptTransaction.id().encoded());
        assertEquals(18, ethInvokeScriptTransaction.type());
        assertEquals(1, ethInvokeScriptTransaction.version());
        assertEquals(83, ethInvokeScriptTransaction.chainId());
        assertEquals(
                "5BcWEhVZFuvMhs8DRF1C8GLSbXvobkfaaRvANa86MboLSQLxC5989Zgo3Djp8WwWKo3JshYP39NnBAJRqTCXZ5Qd",
                ethInvokeScriptTransaction.sender().encoded()
        );
        assertEquals(
                "3MiKAyPxv5ccsFToCQiazxvBn4SMxECaFkU",
                ethInvokeScriptTransaction.sender().address(ChainId.STAGENET).encoded()
        );

        Invocation payload = (Invocation) ethInvokeScriptTransaction.payload();

        assertEquals("saveString", payload.function().name());
        assertEquals("3MRuzZVauiiX2DGwNyP8Tv7idDGUy1VG5bJ", payload.dApp().encoded());
        assertEquals(1, payload.function().args().size());
    }

    private TransactionMetadata buildInvokeScriptWithoutPaymentsMetadata() {
        return TransactionMetadata
                .newBuilder()
                .setEthereum(
                        EthereumMetadata
                                .newBuilder()
                                .setSenderPublicKey(
                                        ByteString.copyFrom(
                                                Base58.decode("5BcWEhVZFuvMhs8DRF1C8GLSbXvobkfaaRvANa86MboLSQLxC5989Zgo3Djp8WwWKo3JshYP39NnBAJRqTCXZ5Qd")
                                        )
                                )
                                .setInvoke(
                                        TransactionMetadata.InvokeScriptMetadata
                                                .newBuilder()
                                                .setDAppAddress(ByteString.copyFrom(
                                                        Base58.decode("3MRuzZVauiiX2DGwNyP8Tv7idDGUy1VG5bJ"))
                                                )
                                                .setFunctionName("saveString")
                                                .addArguments(0, Argument.newBuilder().setStringValue("vf").build())
                                                .build()
                                )
                                .build()
                ).build();
    }

    private TransactionMetadata buildInvokeScriptWithPaymentsMetadata() {
        return TransactionMetadata
                .newBuilder()
                .setEthereum(
                        EthereumMetadata
                                .newBuilder()

                                .setSenderPublicKey(
                                        ByteString.copyFrom(
                                                Base58.decode("5HxN2EUK3WQrfYBdWqVByPwVe7qYtEcrQhQGYqqqZZiTRSapUWkvGAqVmqXJ2TvFgW2gAYk7d1bNz9bHjAytdHV9")
                                        )
                                )
                                .setInvoke(
                                        TransactionMetadata.InvokeScriptMetadata
                                                .newBuilder()
                                                .setDAppAddress(ByteString.copyFrom(
                                                        Base58.decode("3MinbjWtnaj293jv5zZEt2ZkgVj66EsJhRP"))
                                                )
                                                .setFunctionName("call")
                                                .addArguments(0, Argument.newBuilder().setStringValue("Первая из строк").build())
                                                .addArguments(1, Argument.newBuilder().setIntegerValue(1).build())
                                                .addArguments(2, Argument.newBuilder().setBooleanValue(true).build())
                                                .addArguments(3, Argument.newBuilder()
                                                        .setBinaryValue(
                                                                ByteString.copyFrom(Base64.decode("4BD+OLA="))).build()
                                                )
                                                .addArguments(4, Argument.newBuilder().setStringValue("Вторая из строк").build())
                                                .addArguments(5, Argument.newBuilder().setIntegerValue(3).build())
                                                .addArguments(6, Argument.newBuilder().setBooleanValue(false).build())
                                                .addArguments(7, Argument.newBuilder().setBinaryValue(
                                                        ByteString.copyFrom(Base64.decode("AUWrPlBPQlWXsn4="))).build())
                                                .addArguments(8, Argument.newBuilder().setStringValue("Третья из строк").build())
                                                .addArguments(9, Argument.newBuilder().setIntegerValue(3).build())
                                                .addArguments(10, Argument.newBuilder().setBooleanValue(true).build())
                                                .addArguments(11, Argument.newBuilder().setBinaryValue(
                                                        ByteString.copyFrom(Base64.decode("pimpshwamp21312390AA"))).build())
                                                .addArguments(12, Argument.newBuilder().setIntegerValue(1).build())
                                                .addArguments(13, Argument.newBuilder().setIntegerValue(12354634543L).build())
                                                .addPayments(0,
                                                        Amount
                                                                .newBuilder()
                                                                .setAmount(300000000L)
                                                                .setAssetId(ByteString.EMPTY)
                                                                .build()
                                                )
                                                .build()
                                )
                                .build()
                ).build();
    }

    private final String signedInvokeTxWithoutPayments = "i3fxSybQfxBBG4xLgJpiWw9zGKHxDbaLwSWnS6Fd1A6dH3wRFRZ2w" +
            "9pujBNe34c7XSty5WeFGk5MAHVUmmS1Rbq3nfgWaLhX4VjrSmkjod8iZaatHfu3nhPTxMRcgbLtgEaLUC9S8nGcwWGf2dLTra2" +
            "VKWpRx5kafLVQK2cKRjokH7JVe7fQ4jBzdZEW9PjaWrjyUrLDWYP69nkqRC8iZEb3SxEPgJsdb1YikhqpBs7gUijVdgUtSBym8" +
            "c7n96qPhn7yzP5JQX2y1aRfDyVRm9jJXuXHbVJ1Kx8YJ7mu7QS6jG8DUG2a9oAWZwKrpihjDnWVYRxYFatmL7ZBaVLDNrwyqbg" +
            "zJzpn6R8Yn2JJ14eAHYKNCzg5RTDQB";

    private final String signedInvokeTxWithPayments = "TW82GbQC6tHsjHt9Y8hJNgHh57QDw3y44ggy2M2gKeqCZj2UjK1teNWcMJbLoETMP39" +
            "LPAcL5EHfne8kSwYp1LZhQj1sQUZta5xz1TUYjjKm3R6iZTZTQ6SWBCPnEQopTN713fSG8jPPRQBX2me9A92jtNgsKbRoAL" +
            "8KCv1SD5pig5xURrN1ZxUNx7n5sooXRPu8NMxcwZ1c6THDBcSGHx1cpNvqaCyqdXjUCcHmYUFP8WMCJtkDz6ZKd3b946UmX" +
            "zJGjHTMwtUwZB1tpDXZpMP7UZ8r5XRGN1tmbJpoHkh7oEkC2XNoYtZPdNzbuSXU7FdnfamFGfeqrLhEdmKx3iUaoeyWPvu" +
            "mEwVYkGWbtY9x26GDvjpdnmoLtmMeBHNARo58zMibbEwSsHFRvWJCQ6LF5d6GbThkZGZmgjSjpKSCBU5nE2RjznT4pztS" +
            "Xj67xoNzS4eTqdZC1Xpt3tr7XgZ5pExx9WPxhVA3rjdyENscr9CCuTvU647KRQtaVhXNzYBkhxdXdGrmVRupU2QUq9J" +
            "ivvHwCjJp2yJsPQ7JZm8dDZFKNjtVC1G1qx7qAZ1cZv9W9EGEmJbvNvvtSekxwMYxvgT2YYCG1njPkzhkvq68nyFEdV" +
            "RNhYwZhTaeHWRmkyZ4b6cwgHDJxGuu5oFu8zHuRrwzLinvL6RMmkqcnv8FAmjubYsa9vBwz7Gsii6gtEZ3oYMzUNe" +
            "fQ738zUcT8VpmPCFTbGQ2ewLeyspASexWbCqMgZCgCYurdidveMTPokw7nRUY6m3FnvKMiN7RkjSoPy4JwuQ72My" +
            "8KgVYQPWRpgqP7S542TU6VM4tepxTGcoeZVK9uQKeYABK6F5y8MQf8xa7ni2ZKMmbRmcsCb8aqMAxHzjvchTCri" +
            "fF9seP5bcvenyXFYpa5nzHrGRM2mNdpH3KCVcBqbjVHuigVDdCqXhR4iQELBVXZZYqvJFMq2FiLRL7AXnTZJXs" +
            "J4gXiNqXg9jsNY5oiESHFKib9LKGM1oaLmu4L32x392ohfwA6oMrGzevwHfBK9kLAPunqp2YeHpRMoo6g9Ko" +
            "ehmtPasT8rRN56155aCCSY2fBjrPcouNTpuqrU4YVpoAjKTRXiixHh73v8tSxY89ShSBDR2v3EtcJzqKueC" +
            "FpEscaHSEM16kwVVyrLQnKuE7Zhfdecijhtyka6Q49HXJJFVDsBkp5mpReHDLxhUcdy7gHTruKxqzMq2Su" +
            "nzoUcc4jM3B4rkkmjuVsHRkU6LBxwKuw5nh8QcfHrfVapYzZTXUrCJ1XoQH4UPg6rXED7LLjiDb1qnhLz" +
            "2e4RDsGUYwiqavhxuh9NF58D2Tp6QBAxnZQsnPqjZYMkFW2znPs8UjbQTkbGSQQbU4yZyf5yvRUesc7c" +
            "bjbXQ1bpJnjGayx3W69YBP9JrF7LBsDwwCW955kr3d2YxUaGZRNX8SQbroTBATBJ1hv8NP1RMw2vHZfG5eCeJ";

}
