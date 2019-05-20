package com.wavesplatform.wavesj;

import com.wavesplatform.wavesj.matcher.*;
import com.wavesplatform.wavesj.transactions.*;

import java.util.Collection;
import java.util.List;

import static com.wavesplatform.wavesj.Asset.isWaves;
import static com.wavesplatform.wavesj.transactions.InvokeScriptTransaction.*;

public class Transactions {

    public static IssueTransactionV2 makeIssueTx(PrivateKeyAccount sender, byte chainId, String name, String description,
                                                 long quantity, byte decimals, boolean reissuable, String script, long fee, long timestamp) {
        return new IssueTransactionV2(sender, chainId, name, description, quantity, decimals, reissuable, script, fee, timestamp);
    }

    public static IssueTransactionV2 makeIssueTx(PrivateKeyAccount sender, byte chainId, String name, String description, long quantity,
                                                 byte decimals, boolean reissuable, String script, long fee) {
        return makeIssueTx(sender, chainId, name, description, quantity, decimals, reissuable, script, fee, System.currentTimeMillis());
    }

    public static ReissueTransactionV2 makeReissueTx(PrivateKeyAccount sender, byte chainId, String assetId, long quantity,
                                                     boolean reissuable, long fee, long timestamp) {
        if (isWaves(assetId)) {
            throw new IllegalArgumentException("Cannot reissue WAVES");
        }
        return new ReissueTransactionV2(sender, chainId, assetId, quantity, reissuable, fee, timestamp);
    }

    public static ReissueTransactionV2 makeReissueTx(PrivateKeyAccount sender, byte chainId, String assetId, long quantity, boolean reissuable, long fee) {
        return makeReissueTx(sender, chainId, assetId, quantity, reissuable, fee, System.currentTimeMillis());
    }

    public static TransferTransactionV2 makeTransferTx(PrivateKeyAccount sender, String recipient, long amount, String assetId,
                                                       long fee, String feeAssetId, String attachment, long timestamp) {
        return new TransferTransactionV2(sender, recipient, amount, assetId, fee, feeAssetId, attachment == null ? ByteString.EMPTY : new ByteString(attachment.getBytes()), timestamp);
    }

    public static TransferTransactionV2 makeTransferTx(PrivateKeyAccount sender, String recipient, long amount, String assetId,
                                                       long fee, String feeAssetId, String attachment) {
        return makeTransferTx(sender, recipient, amount, assetId, fee, feeAssetId, attachment, System.currentTimeMillis());
    }

    public static TransferTransactionV2 makeTransferTx(PrivateKeyAccount sender, String recipient, long amount, String assetId,
                                                       long fee, String feeAssetId, ByteString attachment) {
        return makeTransferTx(sender, recipient, amount, assetId, fee, feeAssetId, attachment, System.currentTimeMillis());
    }

    public static TransferTransactionV2 makeTransferTx(PrivateKeyAccount sender, String recipient, long amount, String assetId,
                                                       long fee, String feeAssetId, ByteString attachment, long timestamp) {
        return new TransferTransactionV2(sender, recipient, amount, assetId, fee, feeAssetId, attachment == null ? ByteString.EMPTY : attachment, timestamp);
    }

    public static BurnTransactionV2 makeBurnTx(PrivateKeyAccount sender, byte chainId, String assetId, long amount, long fee, long timestamp) {
        if (isWaves(assetId)) {
            throw new IllegalArgumentException("Cannot burn WAVES");
        }

        return new BurnTransactionV2(sender, chainId, assetId, amount, fee, timestamp);
    }

    public static BurnTransactionV2 makeBurnTx(PrivateKeyAccount sender, byte chainId, String assetId, long amount, long fee) {
        return makeBurnTx(sender, chainId, assetId, amount, fee, System.currentTimeMillis());
    }

    public static SponsorTransaction makeSponsorTx(PrivateKeyAccount sender, String assetId, long minAssetFee, long fee, long timestamp) {
        if (isWaves(assetId)) {
            throw new IllegalArgumentException("Cannot sponsor WAVES");
        }
        if (minAssetFee < 0) {
            throw new IllegalArgumentException("minAssetFee must be positive or zero");
        }

        return new SponsorTransaction(sender, assetId, minAssetFee, fee, timestamp);
    }

    public static SponsorTransaction makeSponsorTx(PrivateKeyAccount sender, String assetId, long minAssetFee, long fee) {
        return makeSponsorTx(sender, assetId, minAssetFee, fee, System.currentTimeMillis());
    }

    public static LeaseTransactionV2 makeLeaseTx(PrivateKeyAccount sender, String recipient, long amount, long fee, long timestamp) {
        return new LeaseTransactionV2(sender, recipient, amount, fee, timestamp);
    }

    public static LeaseTransaction makeLeaseTx(PrivateKeyAccount sender, String recipient, long amount, long fee) {
        return makeLeaseTx(sender, recipient, amount, fee, System.currentTimeMillis());
    }

    public static LeaseCancelTransaction makeLeaseCancelTx(PrivateKeyAccount sender, byte chainId, String leaseId, long fee, long timestamp) {
        return new LeaseCancelTransactionV2(sender, chainId, leaseId, fee, timestamp);
    }

    public static LeaseCancelTransaction makeLeaseCancelTx(PrivateKeyAccount sender, byte chainId, String leaseId, long fee) {
        return makeLeaseCancelTx(sender, chainId, leaseId, fee, System.currentTimeMillis());
    }

    public static AliasTransaction makeAliasTx(PrivateKeyAccount sender, String alias, byte chainId, long fee, long timestamp) {
        return new AliasTransactionV2(sender, new Alias(alias, chainId), fee, timestamp);
    }

    public static AliasTransaction makeAliasTx(PrivateKeyAccount sender, String alias, byte chainId, long fee) {
        return makeAliasTx(sender, alias, chainId, fee, System.currentTimeMillis());
    }

    public static MassTransferTransaction makeMassTransferTx(PrivateKeyAccount sender, String assetId, Collection<Transfer> transfers,
                                                             long fee, String attachment, long timestamp) {
        return new MassTransferTransaction(sender, assetId, transfers, fee, attachment == null ? ByteString.EMPTY : new ByteString(attachment.getBytes()), timestamp);
    }

    public static MassTransferTransaction makeMassTransferTx(PrivateKeyAccount sender, String assetId, Collection<Transfer> transfers,
                                                             long fee, String attachment) {
        return makeMassTransferTx(sender, assetId, transfers, fee, attachment, System.currentTimeMillis());
    }

    public static DataTransaction makeDataTx(PrivateKeyAccount sender, Collection<DataEntry<?>> data, long fee, long timestamp) {
        return new DataTransaction(sender, data, fee, timestamp);
    }

    public static DataTransaction makeDataTx(PrivateKeyAccount sender, Collection<DataEntry<?>> data, long fee) {
        return makeDataTx(sender, data, fee, System.currentTimeMillis());
    }

    /**
     * Creates a signed SetScript object.
     *
     * @param sender    the account to set the script for
     * @param script    compiled script, base64 encoded
     * @param chainId   chain ID
     * @param fee       object fee
     * @param timestamp operation timestamp
     * @return object object
     * @see Account#MAINNET
     * @see Account#TESTNET
     */
    public static SetScriptTransaction makeScriptTx(PrivateKeyAccount sender, String script, byte chainId, long fee, long timestamp) {
        return new SetScriptTransaction(sender, script, chainId, fee, timestamp);
    }


    public static SetScriptTransaction makeScriptTx(PrivateKeyAccount sender, String script, byte chainId, long fee) {
        return makeScriptTx(sender, script, chainId, fee, System.currentTimeMillis());
    }

    public static Order makeOrder(PrivateKeyAccount account, String matcherKey, Order.Type orderType,
                                  AssetPair assetPair, long price, long amount, long expiration, long matcherFee, long timestamp) {
        if (assetPair.getAmountAsset().equals(assetPair.getPriceAsset())) {
            throw new IllegalArgumentException("spendAsset and receiveAsset should not be equal");
        }
        return new OrderV2(account, new PublicKeyAccount(matcherKey, account.getChainId()), orderType, assetPair, amount, price, timestamp,
                expiration, matcherFee, Order.V2);
    }

    public static Order makeOrder(PrivateKeyAccount account, String matcherKey, Order.Type orderType,
                                  AssetPair assetPair, long price, long amount, long expiration, long matcherFee) {
        return makeOrder(account, matcherKey, orderType, assetPair, price, amount, expiration, matcherFee, System.currentTimeMillis());
    }



    public static ExchangeTransactionV2 makeExchangeTx(PrivateKeyAccount account, Order buyOrder, Order sellOrder, long amount,
                                                       long price, long buyMatcherFee, long sellMatcherFee, long fee) {
        return makeExchangeTx(account, buyOrder, sellOrder, amount, price, buyMatcherFee, sellMatcherFee, fee, System.currentTimeMillis());
    }

    public static ExchangeTransactionV2 makeExchangeTx(PrivateKeyAccount account, Order buyOrder, Order sellOrder, long amount,
                                                       long price, long buyMatcherFee, long sellMatcherFee, long fee, long timestamp) {
        return new ExchangeTransactionV2(account, buyOrder, sellOrder, amount, price, buyMatcherFee, sellMatcherFee, fee, timestamp);
    }

    public static CancelOrder makeOrderCancel(PrivateKeyAccount account) {
        return makeOrderCancel(account, System.currentTimeMillis());
    }

    public static CancelOrder makeOrderCancel(PrivateKeyAccount account, long timestamp) {
        return new CancelOrder(account, timestamp);
    }

    public static CancelOrder makeOrderCancel(PrivateKeyAccount account, AssetPair assetPair) {
        return makeOrderCancel(account, assetPair, System.currentTimeMillis());
    }

    public static CancelOrder makeOrderCancel(PrivateKeyAccount account, AssetPair assetPair, long timestamp) {
        return new CancelOrder(account, assetPair, timestamp);
    }

    public static CancelOrder makeOrderCancel(PrivateKeyAccount account, AssetPair assetPair, String orderId) {
        return new CancelOrder(account, assetPair, orderId);
    }

    public static DeleteOrder makeDeleteOrder(PrivateKeyAccount account, AssetPair assetPair, String orderId) {
        return new DeleteOrder(account, assetPair, orderId);
    }

    public static InvokeScriptTransaction makeInvokeScriptTx(PrivateKeyAccount account, byte chainId, String dApp, FunctionCall call,
                                                             List<Payment> payments, long fee, String feeAssetId, long timestamp) {
        return new InvokeScriptTransaction(chainId, account, dApp, call, payments, fee, feeAssetId, timestamp);
    }


    public static InvokeScriptTransaction makeInvokeScriptTx(PrivateKeyAccount account, byte chainId, String dApp, FunctionCall call,
                                                             List<Payment> payments, long fee, String feeAssetId) {
        return makeInvokeScriptTx(account, chainId, dApp, call, payments, fee, feeAssetId, System.currentTimeMillis());
    }

    public static InvokeScriptTransaction makeInvokeScriptTx(PrivateKeyAccount account, byte chainId, String dApp, FunctionCall call,
                                                             long fee, String feeAssetId, long timestamp) {
        return new InvokeScriptTransaction(chainId, account, dApp, call, fee, feeAssetId, timestamp);
    }


    public static InvokeScriptTransaction makeInvokeScriptTx(PrivateKeyAccount account, byte chainId, String dApp, FunctionCall call,
                                                            long fee, String feeAssetId) {
        return makeInvokeScriptTx(account, chainId, dApp, call, fee, feeAssetId, System.currentTimeMillis());
    }

    public static InvokeScriptTransaction makeInvokeScriptTx(PrivateKeyAccount account, byte chainId, String dApp, String functionName, long fee, String feeAssetId, long timestamp) {
        return new InvokeScriptTransaction(chainId, account, dApp, functionName, fee, feeAssetId, timestamp);
    }

    public static InvokeScriptTransaction makeInvokeScriptTx(PrivateKeyAccount account, byte chainId, String dApp, String functionName, long fee, String feeAssetId) {
        return makeInvokeScriptTx(account, chainId, dApp, functionName, fee, feeAssetId, System.currentTimeMillis());
    }
}
