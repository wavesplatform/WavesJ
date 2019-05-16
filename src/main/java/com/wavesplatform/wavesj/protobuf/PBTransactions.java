package com.wavesplatform.wavesj.protobuf;

import com.google.protobuf.ByteString;
import com.wavesplatform.protobuf.transaction.RecipientOuterClass;
import com.wavesplatform.protobuf.transaction.TransactionOuterClass;
import com.wavesplatform.wavesj.*;
import com.wavesplatform.wavesj.matcher.Order;
import com.wavesplatform.wavesj.transactions.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("ALL")
public class PBTransactions {
    public static String toVanillaAssetId(final ByteString assetId) {
        if (assetId.isEmpty()) return Asset.WAVES;
        else return Asset.normalize(Base58.encode(assetId.toByteArray()));
    }

    public static com.wavesplatform.wavesj.ByteString toSignature(final List<ByteString> proofs) {
        if (proofs.isEmpty()) return com.wavesplatform.wavesj.ByteString.EMPTY;
        else return toVanillaByteString(proofs.get(0));
    }

    public static com.wavesplatform.wavesj.ByteString toVanillaByteString(final ByteString bs) {
        return new com.wavesplatform.wavesj.ByteString(bs.toByteArray());
    }

    public static List<com.wavesplatform.wavesj.ByteString> toVanillaProofs(final List<ByteString> proofs) {
        final List<com.wavesplatform.wavesj.ByteString> result = new ArrayList<com.wavesplatform.wavesj.ByteString>(proofs.size());
        for (ByteString proof : proofs) result.add(toVanillaByteString(proof));
        return Collections.unmodifiableList(result);
    }

    public static List<DataEntry<?>> toVanillaDataEntryList(final List<TransactionOuterClass.DataTransactionData.DataEntry> dataEntries) {
        final List<DataEntry<?>> result = new ArrayList<DataEntry<?>>(dataEntries.size());
        for (TransactionOuterClass.DataTransactionData.DataEntry dataEntry : dataEntries) result.add(toVanillaDataEntry(dataEntry));
        return Collections.unmodifiableList(result);
    }

    public static DataEntry<?> toVanillaDataEntry(final TransactionOuterClass.DataTransactionData.DataEntry dataEntry) {
        DataEntry<?> vd = null;
        switch (dataEntry.getValueCase()) {
            case STRING_VALUE:
                vd = new DataEntry.StringEntry(dataEntry.getKey(), dataEntry.getStringValue());
                break;
            case BOOL_VALUE:
                vd = new DataEntry.BooleanEntry(dataEntry.getKey(), dataEntry.getBoolValue());
                break;
            case INT_VALUE:
                vd = new DataEntry.LongEntry(dataEntry.getKey(), dataEntry.getIntValue());
                break;
            case BINARY_VALUE:
                vd = new DataEntry.BinaryEntry(dataEntry.getKey(), toVanillaByteString(dataEntry.getBinaryValue()));
                break;
            default:
                throw new IllegalArgumentException("Not supported: " + dataEntry);
        }
        return vd;
    }

    public static Order toVanillaOrder(final TransactionOuterClass.ExchangeTransactionData.Order order) {
        final Order.Type orderType = order.getOrderSide() == TransactionOuterClass.ExchangeTransactionData.Order.Side.BUY ? Order.Type.BUY : Order.Type.SELL;
        final AssetPair assetPair = new AssetPair(toVanillaAssetId(order.getAssetPair().getAmountAssetId()), toVanillaAssetId(order.getAssetPair().getPriceAssetId()));

        return new Order(orderType, assetPair, order.getAmount(), order.getPrice(), order.getTimestamp(), order.getExpiration(), order.getMatcherFee().getAmount(), new PublicKeyAccount(order.getSenderPublicKey().toByteArray(), (byte) order.getChainId()), new PublicKeyAccount(order.getMatcherPublicKey().toByteArray(), (byte)order.getChainId()), toSignature(order.getProofsList()));
    }

    public static String toRecipientString(final RecipientOuterClass.Recipient recipient) {
        switch (recipient.getRecipientCase()) {
            case ALIAS:
                return recipient.getAlias();
            case ADDRESS:
                return Base58.encode(recipient.getAddress().toByteArray());
            default:
                throw new IllegalArgumentException("Recipient not supported: " + recipient);
        }
    }

    public static Transaction toVanilla(final TransactionOuterClass.SignedTransaction signedTransaction) {
        final TransactionOuterClass.Transaction tx = signedTransaction.getTransaction();
        if (tx.getVersion() != 1 && tx.getVersion() != 2) throw new IllegalArgumentException("TX version not supported: " + tx);

        if (tx.hasBurn()) {
            final TransactionOuterClass.BurnTransactionData burn = tx.getBurn();
            switch (tx.getVersion()) {
                case Transaction.V1:
                    return new BurnTransactionV1(new PublicKeyAccount(tx.getSenderPublicKey().toByteArray(), (byte) tx.getChainId()), toVanillaAssetId(burn.getAssetAmount().getAssetId()), burn.getAssetAmount().getAmount(), tx.getFee().getAmount(), tx.getTimestamp(), toSignature(signedTransaction.getProofsList()));

                case Transaction.V2:
                    return new BurnTransactionV2(new PublicKeyAccount(tx.getSenderPublicKey().toByteArray(), (byte) tx.getChainId()), (byte) tx.getChainId(), toVanillaAssetId(burn.getAssetAmount().getAssetId()), burn.getAssetAmount().getAmount(), tx.getFee().getAmount(), tx.getTimestamp(), toVanillaProofs(signedTransaction.getProofsList()));
            }
        } else if (tx.hasCreateAlias()) {
            final TransactionOuterClass.CreateAliasTransactionData createAlias = tx.getCreateAlias();
            switch (tx.getVersion()) {
                case Transaction.V1:
                    return new AliasTransactionV1(new PublicKeyAccount(tx.getSenderPublicKey().toByteArray(), (byte) tx.getChainId()), new Alias(createAlias.getAlias(), (byte)tx.getChainId()), tx.getFee().getAmount(), tx.getTimestamp(), toSignature(signedTransaction.getProofsList()));

                case Transaction.V2:
                    return new AliasTransactionV2(new PublicKeyAccount(tx.getSenderPublicKey().toByteArray(), (byte) tx.getChainId()), new Alias(createAlias.getAlias(), (byte)tx.getChainId()), tx.getFee().getAmount(), tx.getTimestamp(), toVanillaProofs(signedTransaction.getProofsList()));
            }
        } else if (tx.hasDataTransaction()) {
            final TransactionOuterClass.DataTransactionData data = tx.getDataTransaction();
            return new DataTransaction(new PublicKeyAccount(tx.getSenderPublicKey().toByteArray(), (byte) tx.getChainId()), toVanillaDataEntryList(data.getDataList()), tx.getFee().getAmount(), tx.getTimestamp(), toVanillaProofs(signedTransaction.getProofsList()));
        } else if (tx.hasExchange()) {
            final TransactionOuterClass.ExchangeTransactionData exchange = tx.getExchange();
            return new ExchangeTransaction(new PublicKeyAccount(tx.getSenderPublicKey().toByteArray(), (byte)tx.getChainId()), exchange.getAmount(), exchange.getPrice(), toVanillaOrder(exchange.getOrders(0)), toVanillaOrder(exchange.getOrders(1)), exchange.getBuyMatcherFee(), exchange.getSellMatcherFee(), tx.getFee().getAmount(), tx.getTimestamp(), toSignature(signedTransaction.getProofsList()));
        } else if (tx.hasGenesis()) {
            // ???
        } else if (tx.hasInvokeScript()) {
            // ???
        } else if (tx.hasIssue()) {
            final TransactionOuterClass.IssueTransactionData issue = tx.getIssue();
            switch (tx.getVersion()) {
                case Transaction.V1:
                    return new IssueTransactionV1(new PublicKeyAccount(tx.getSenderPublicKey().toByteArray(), (byte) tx.getChainId()), new String(issue.getName().toByteArray()), new String(issue.getDescription().toByteArray()), issue.getAmount(), (byte)issue.getDecimals(), issue.getReissuable(), tx.getFee().getAmount(), tx.getTimestamp(), toSignature(signedTransaction.getProofsList()));

                case Transaction.V2:
                    return new IssueTransactionV2(new PublicKeyAccount(tx.getSenderPublicKey().toByteArray(), (byte) tx.getChainId()), (byte) tx.getChainId(), new String(issue.getName().toByteArray()), new String(issue.getDescription().toByteArray()), issue.getAmount(), (byte)issue.getDecimals(), issue.getReissuable(), new String(issue.getScript().getBytes().toByteArray()), tx.getFee().getAmount(), tx.getTimestamp(), toVanillaProofs(signedTransaction.getProofsList()));
            }
        } else if (tx.hasReissue()) {
            final TransactionOuterClass.ReissueTransactionData reissue = tx.getReissue();
            switch (tx.getVersion()) {
                case Transaction.V1:
                    return new ReissueTransactionV1(new PublicKeyAccount(tx.getSenderPublicKey().toByteArray(), (byte) tx.getChainId()), toVanillaAssetId(reissue.getAssetAmount().getAssetId()), reissue.getAssetAmount().getAmount(), reissue.getReissuable(), tx.getFee().getAmount(), tx.getTimestamp(), toSignature(signedTransaction.getProofsList()));

                case Transaction.V2:
                    return new ReissueTransactionV2(new PublicKeyAccount(tx.getSenderPublicKey().toByteArray(), (byte) tx.getChainId()), (byte)tx.getChainId(), toVanillaAssetId(reissue.getAssetAmount().getAssetId()), reissue.getAssetAmount().getAmount(), reissue.getReissuable(), tx.getFee().getAmount(), tx.getTimestamp(), toVanillaProofs(signedTransaction.getProofsList()));
            }
        } else if (tx.hasSetAssetScript()) {
            // ???
        } else if (tx.hasSetScript()) {
            final TransactionOuterClass.SetScriptTransactionData setScript = tx.getSetScript();
            return new SetScriptTransaction(new PublicKeyAccount(tx.getSenderPublicKey().toByteArray(), (byte)tx.getChainId()), new String(setScript.getScript().getBytes().toByteArray()), (byte)tx.getChainId(), tx.getFee().getAmount(), tx.getTimestamp(), toVanillaProofs(signedTransaction.getProofsList()));
        } else if (tx.hasTransfer()) {
            final TransactionOuterClass.TransferTransactionData transfer = tx.getTransfer();
            switch (tx.getVersion()) {
                case Transaction.V1:
                    return new TransferTransactionV1(new PublicKeyAccount(tx.getSenderPublicKey().toByteArray(), (byte)tx.getChainId()), toRecipientString(transfer.getRecipient()), transfer.getAmount().getAmount(), toVanillaAssetId(transfer.getAmount().getAssetId().getIssuedAsset()), tx.getFee().getAmount(), toVanillaAssetId(tx.getFee().getAssetId().getIssuedAsset()), toVanillaByteString(transfer.getAttachment()), tx.getTimestamp(), toSignature(signedTransaction.getProofsList()));

                case Transaction.V2:
                    return new TransferTransactionV2(new PublicKeyAccount(tx.getSenderPublicKey().toByteArray(), (byte)tx.getChainId()), toRecipientString(transfer.getRecipient()), transfer.getAmount().getAmount(), toVanillaAssetId(transfer.getAmount().getAssetId().getIssuedAsset()), tx.getFee().getAmount(), toVanillaAssetId(tx.getFee().getAssetId().getIssuedAsset()), toVanillaByteString(transfer.getAttachment()), tx.getTimestamp(), toVanillaProofs(signedTransaction.getProofsList()));
            }
        } else if (tx.hasPayment()) {
            // ???
        } else if (tx.hasLease()) {
            final TransactionOuterClass.LeaseTransactionData lease = tx.getLease();
            switch (tx.getVersion()) {
                case Transaction.V1:
                    return new LeaseTransactionV1(new PublicKeyAccount(tx.getSenderPublicKey().toByteArray(), (byte)tx.getChainId()), toRecipientString(lease.getRecipient()), lease.getAmount(), tx.getFee().getAmount(), tx.getTimestamp(), toSignature(signedTransaction.getProofsList()));

                case Transaction.V2:
                    return new LeaseTransactionV2(new PublicKeyAccount(tx.getSenderPublicKey().toByteArray(), (byte)tx.getChainId()), toRecipientString(lease.getRecipient()), lease.getAmount(), tx.getFee().getAmount(), tx.getTimestamp(), toVanillaProofs(signedTransaction.getProofsList()));
            }
        } else if (tx.hasLeaseCancel()) {
            final TransactionOuterClass.LeaseCancelTransactionData leaseCancel = tx.getLeaseCancel();
            switch (tx.getVersion()) {
                case Transaction.V1:
                    return new LeaseCancelTransactionV1(new PublicKeyAccount(tx.getSenderPublicKey().toByteArray(), (byte)tx.getChainId()), Base58.encode(leaseCancel.getLeaseId().toByteArray()), tx.getFee().getAmount(), tx.getTimestamp(), toSignature(signedTransaction.getProofsList()));

                case Transaction.V2:
                    return new LeaseCancelTransactionV2(new PublicKeyAccount(tx.getSenderPublicKey().toByteArray(), (byte)tx.getChainId()), (byte)tx.getChainId(), Base58.encode(leaseCancel.getLeaseId().toByteArray()), tx.getFee().getAmount(), tx.getTimestamp(), toVanillaProofs(signedTransaction.getProofsList()));
            }
        } else if (tx.hasMassTransfer()) {
            final TransactionOuterClass.MassTransferTransactionData massTransfer = tx.getMassTransfer();
            final List<Transfer> transfers = new ArrayList<Transfer>(massTransfer.getTransfersList().size());
            for (TransactionOuterClass.MassTransferTransactionData.Transfer transfer : massTransfer.getTransfersList()) {
                final Transfer transfer1 = new Transfer(toRecipientString(transfer.getAddress()), transfer.getAmount());
                transfers.add(transfer1);
            }
            return new MassTransferTransaction(new PublicKeyAccount(tx.getSenderPublicKey().toByteArray(), (byte)tx.getChainId()), toVanillaAssetId(massTransfer.getAssetId().getIssuedAsset()), Collections.unmodifiableList(transfers), tx.getFee().getAmount(), toVanillaByteString(massTransfer.getAttachment()), tx.getTimestamp(), toVanillaProofs(signedTransaction.getProofsList()));
        } else if (tx.hasSponsorFee()) {
            final TransactionOuterClass.SponsorFeeTransactionData sponsorFee = tx.getSponsorFee();
            return new SponsorTransaction(new PublicKeyAccount(tx.getSenderPublicKey().toByteArray(), (byte)tx.getChainId()), toVanillaAssetId(sponsorFee.getMinFee().getAssetId()), sponsorFee.getMinFee().getAmount(), tx.getFee().getAmount(), tx.getTimestamp(), toVanillaProofs(signedTransaction.getProofsList()));
        }

        throw new IllegalArgumentException("Invalid TX: " + tx);
    }
}
