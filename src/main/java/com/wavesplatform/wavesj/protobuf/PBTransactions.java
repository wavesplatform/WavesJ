package com.wavesplatform.wavesj.protobuf;

import com.google.protobuf.ByteString;
import com.google.protobuf.Empty;
import com.wavesplatform.protobuf.transaction.RecipientOuterClass;
import com.wavesplatform.protobuf.transaction.ScriptOuterClass;
import com.wavesplatform.protobuf.transaction.TransactionOuterClass;
import com.wavesplatform.wavesj.*;
import com.wavesplatform.wavesj.matcher.Order;
import com.wavesplatform.wavesj.transactions.*;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("unused")
public class PBTransactions {
    public static Transaction toVanilla(final TransactionOuterClass.SignedTransaction signedTransaction) {
        final TransactionOuterClass.Transaction tx = signedTransaction.getTransaction();
        if (tx.getVersion() != 1 && tx.getVersion() != 2)
            throw new IllegalArgumentException("TX version not supported: " + tx);

        final PublicKeyAccount senderPublicKey = new PublicKeyAccount(tx.getSenderPublicKey().toByteArray(), (byte) tx.getChainId());
        final long feeAmount = tx.getFee().getAmount();
        final long timestamp = tx.getTimestamp();
        final com.wavesplatform.wavesj.ByteString signature = toSignature(signedTransaction.getProofsList());
        final List<com.wavesplatform.wavesj.ByteString> proofs = toVanillaProofs(signedTransaction.getProofsList());

        if (tx.hasBurn()) {
            final TransactionOuterClass.BurnTransactionData burn = tx.getBurn();
            switch (tx.getVersion()) {
                case Transaction.V1:
                    return new BurnTransactionV1(senderPublicKey, toVanillaAssetId(burn.getAssetAmount().getAssetId()), burn.getAssetAmount().getAmount(), feeAmount, timestamp, signature);

                case Transaction.V2:
                    return new BurnTransactionV2(senderPublicKey, (byte) tx.getChainId(), toVanillaAssetId(burn.getAssetAmount().getAssetId()), burn.getAssetAmount().getAmount(), feeAmount, timestamp, proofs);
            }
        } else if (tx.hasCreateAlias()) {
            final TransactionOuterClass.CreateAliasTransactionData createAlias = tx.getCreateAlias();
            switch (tx.getVersion()) {
                case Transaction.V1:
                    return new AliasTransactionV1(senderPublicKey, new Alias(createAlias.getAlias(), (byte) tx.getChainId()), feeAmount, timestamp, signature);

                case Transaction.V2:
                    return new AliasTransactionV2(senderPublicKey, new Alias(createAlias.getAlias(), (byte) tx.getChainId()), feeAmount, timestamp, proofs);
            }
        } else if (tx.hasDataTransaction()) {
            final TransactionOuterClass.DataTransactionData data = tx.getDataTransaction();
            return new DataTransaction(senderPublicKey, toVanillaDataEntryList(data.getDataList()), feeAmount, timestamp, proofs);
        } else if (tx.hasExchange()) {
            final TransactionOuterClass.ExchangeTransactionData exchange = tx.getExchange();
            return new ExchangeTransaction(senderPublicKey, exchange.getAmount(), exchange.getPrice(), toVanillaOrder(exchange.getOrders(0)), toVanillaOrder(exchange.getOrders(1)), exchange.getBuyMatcherFee(), exchange.getSellMatcherFee(), feeAmount, timestamp, signature);
        } else if (tx.hasGenesis()) {
            // ???
        } else if (tx.hasInvokeScript()) {
            // ???
        } else if (tx.hasIssue()) {
            final TransactionOuterClass.IssueTransactionData issue = tx.getIssue();
            switch (tx.getVersion()) {
                case Transaction.V1:
                    return new IssueTransactionV1(senderPublicKey, new String(issue.getName().toByteArray()), new String(issue.getDescription().toByteArray()), issue.getAmount(), (byte) issue.getDecimals(), issue.getReissuable(), feeAmount, timestamp, signature);

                case Transaction.V2:
                    return new IssueTransactionV2(senderPublicKey, (byte) tx.getChainId(), new String(issue.getName().toByteArray()), new String(issue.getDescription().toByteArray()), issue.getAmount(), (byte) issue.getDecimals(), issue.getReissuable(), new String(issue.getScript().getBytes().toByteArray()), feeAmount, timestamp, proofs);
            }
        } else if (tx.hasReissue()) {
            final TransactionOuterClass.ReissueTransactionData reissue = tx.getReissue();
            switch (tx.getVersion()) {
                case Transaction.V1:
                    return new ReissueTransactionV1(senderPublicKey, toVanillaAssetId(reissue.getAssetAmount().getAssetId()), reissue.getAssetAmount().getAmount(), reissue.getReissuable(), feeAmount, timestamp, signature);

                case Transaction.V2:
                    return new ReissueTransactionV2(senderPublicKey, (byte) tx.getChainId(), toVanillaAssetId(reissue.getAssetAmount().getAssetId()), reissue.getAssetAmount().getAmount(), reissue.getReissuable(), feeAmount, timestamp, proofs);
            }
        } else if (tx.hasSetAssetScript()) {
            // ???
        } else if (tx.hasSetScript()) {
            final TransactionOuterClass.SetScriptTransactionData setScript = tx.getSetScript();
            return new SetScriptTransaction(senderPublicKey, new String(setScript.getScript().getBytes().toByteArray()), (byte) tx.getChainId(), feeAmount, timestamp, proofs);
        } else if (tx.hasTransfer()) {
            final TransactionOuterClass.TransferTransactionData transfer = tx.getTransfer();
            switch (tx.getVersion()) {
                case Transaction.V1:
                    return new TransferTransactionV1(senderPublicKey, toRecipientString(transfer.getRecipient()), transfer.getAmount().getAmount(), toVanillaAssetId(transfer.getAmount().getAssetId().getIssuedAsset()), feeAmount, toVanillaAssetId(tx.getFee().getAssetId().getIssuedAsset()), toVanillaByteString(transfer.getAttachment()), timestamp, signature);

                case Transaction.V2:
                    return new TransferTransactionV2(senderPublicKey, toRecipientString(transfer.getRecipient()), transfer.getAmount().getAmount(), toVanillaAssetId(transfer.getAmount().getAssetId().getIssuedAsset()), feeAmount, toVanillaAssetId(tx.getFee().getAssetId().getIssuedAsset()), toVanillaByteString(transfer.getAttachment()), timestamp, proofs);
            }
        } else if (tx.hasPayment()) {
            // ???
        } else if (tx.hasLease()) {
            final TransactionOuterClass.LeaseTransactionData lease = tx.getLease();
            switch (tx.getVersion()) {
                case Transaction.V1:
                    return new LeaseTransactionV1(senderPublicKey, toRecipientString(lease.getRecipient()), lease.getAmount(), feeAmount, timestamp, signature);

                case Transaction.V2:
                    return new LeaseTransactionV2(senderPublicKey, toRecipientString(lease.getRecipient()), lease.getAmount(), feeAmount, timestamp, proofs);
            }
        } else if (tx.hasLeaseCancel()) {
            final TransactionOuterClass.LeaseCancelTransactionData leaseCancel = tx.getLeaseCancel();
            switch (tx.getVersion()) {
                case Transaction.V1:
                    return new LeaseCancelTransactionV1(senderPublicKey, Base58.encode(leaseCancel.getLeaseId().toByteArray()), feeAmount, timestamp, signature);

                case Transaction.V2:
                    return new LeaseCancelTransactionV2(senderPublicKey, (byte) tx.getChainId(), Base58.encode(leaseCancel.getLeaseId().toByteArray()), feeAmount, timestamp, proofs);
            }
        } else if (tx.hasMassTransfer()) {
            final TransactionOuterClass.MassTransferTransactionData massTransfer = tx.getMassTransfer();
            final List<Transfer> transfers = new ArrayList<Transfer>(massTransfer.getTransfersList().size());
            for (TransactionOuterClass.MassTransferTransactionData.Transfer transfer : massTransfer.getTransfersList()) {
                final Transfer transfer1 = new Transfer(toRecipientString(transfer.getAddress()), transfer.getAmount());
                transfers.add(transfer1);
            }
            return new MassTransferTransaction(senderPublicKey, toVanillaAssetId(massTransfer.getAssetId().getIssuedAsset()), Collections.unmodifiableList(transfers), feeAmount, toVanillaByteString(massTransfer.getAttachment()), timestamp, proofs);
        } else if (tx.hasSponsorFee()) {
            final TransactionOuterClass.SponsorFeeTransactionData sponsorFee = tx.getSponsorFee();
            return new SponsorTransaction(senderPublicKey, toVanillaAssetId(sponsorFee.getMinFee().getAssetId()), sponsorFee.getMinFee().getAmount(), feeAmount, timestamp, proofs);
        }

        throw new IllegalArgumentException("Invalid TX: " + tx);
    }

    public static TransactionOuterClass.SignedTransaction toPB(final Transaction tx) {
        TransactionOuterClass.Transaction.Builder base = TransactionOuterClass.Transaction.newBuilder();

        if (tx instanceof IssueTransaction) {
            final IssueTransaction issue = (IssueTransaction) tx;

            ByteString script = ByteString.EMPTY;
            if (issue instanceof IssueTransactionV2)
                script = ByteString.copyFrom(((IssueTransactionV2) issue).getScript().getBytes());

            final TransactionOuterClass.IssueTransactionData data = TransactionOuterClass.IssueTransactionData.newBuilder()
                    .setAmount(issue.getQuantity())
                    .setDecimals(issue.getDecimals())
                    .setName(ByteString.copyFrom(issue.getName().getBytes()))
                    .setDescription(ByteString.copyFrom(issue.getDescription().getBytes()))
                    .setReissuable(issue.isReissuable())
                    .setScript(ScriptOuterClass.Script.newBuilder().setBytes(script))
                    .build();
            base.setIssue(data);
        } else if (tx instanceof ReissueTransaction) {
            final ReissueTransaction reissue = (ReissueTransaction) tx;
            final TransactionOuterClass.ReissueTransactionData data = TransactionOuterClass.ReissueTransactionData.newBuilder()
                    .setAssetAmount(TransactionOuterClass.AssetAmount.newBuilder().setAssetId(toPBAssetId(reissue.getAssetId())).setAmount(reissue.getQuantity()).build())
                    .setReissuable(reissue.isReissuable())
                    .build();
            base.setReissue(data);
        } else if (tx instanceof BurnTransaction) {
            final BurnTransaction burn = (BurnTransaction) tx;
            final TransactionOuterClass.BurnTransactionData data = TransactionOuterClass.BurnTransactionData.newBuilder()
                    .setAssetAmount(TransactionOuterClass.AssetAmount.newBuilder().setAssetId(toPBAssetId(burn.getAssetId())).setAmount(burn.getAmount()).build())
                    .build();
            base.setBurn(data);
        } else if (tx instanceof SetScriptTransaction) {
            final SetScriptTransaction setScript = (SetScriptTransaction) tx;
            final TransactionOuterClass.SetScriptTransactionData data = TransactionOuterClass.SetScriptTransactionData.newBuilder()
                    .setScript(ScriptOuterClass.Script.newBuilder().setBytes(ByteString.copyFrom(setScript.getScript().getBytes())).build())
                    .build();
            base.setSetScript(data);
        } else if (tx instanceof DataTransaction) {
            final DataTransaction dataTransaction = (DataTransaction) tx;

            final List<TransactionOuterClass.DataTransactionData.DataEntry> dataEntries = new ArrayList<TransactionOuterClass.DataTransactionData.DataEntry>(dataTransaction.getData().size());
            for (DataEntry<?> dataEntry : dataTransaction.getData()) dataEntries.add(toPBDataEntry(dataEntry));

            final TransactionOuterClass.DataTransactionData data = TransactionOuterClass.DataTransactionData.newBuilder()
                    .addAllData(dataEntries)
                    .build();

            base.setDataTransaction(data);
        } else if (tx instanceof MassTransferTransaction) {
            final MassTransferTransaction massTransferTransaction = (MassTransferTransaction) tx;
            final ByteString assetId = toPBAssetId(massTransferTransaction.getAssetId());
            final List<TransactionOuterClass.MassTransferTransactionData.Transfer> transfers = new ArrayList<TransactionOuterClass.MassTransferTransactionData.Transfer>(massTransferTransaction.getTransfers().size());
            for (Transfer transfer : massTransferTransaction.getTransfers()) {
                transfers.add(TransactionOuterClass.MassTransferTransactionData.Transfer.newBuilder().setAddress(toPBRecipient(transfer.getRecipient())).setAmount(transfer.getAmount()).build());
            }

            final TransactionOuterClass.MassTransferTransactionData data = TransactionOuterClass.MassTransferTransactionData.newBuilder()
                    .setAssetId(assetId.isEmpty() ? TransactionOuterClass.AssetId.newBuilder().setWaves(Empty.newBuilder().build()).build() : TransactionOuterClass.AssetId.newBuilder().setIssuedAsset(assetId).build())
                    .setAttachment(toPBByteString(massTransferTransaction.getAttachment()))
                    .addAllTransfers(transfers)
                    .build();
        }
    }

    public static String toVanillaAssetId(final ByteString assetId) {
        if (assetId.isEmpty()) return Asset.WAVES;
        else return Asset.normalize(Base58.encode(assetId.toByteArray()));
    }

    public static ByteString toPBAssetId(final String assetId) {
        if (Asset.isWaves(assetId)) return ByteString.EMPTY;
        else return ByteString.copyFrom(Base58.decode(assetId));
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

    public static TransactionOuterClass.DataTransactionData.DataEntry toPBDataEntry(final DataEntry<?> dataEntry) {
        TransactionOuterClass.DataTransactionData.DataEntry.Builder builder = TransactionOuterClass.DataTransactionData.DataEntry.newBuilder();

        if (dataEntry.getType().equals("integer")) builder.setIntValue((Long) dataEntry.getValue());
        else if (dataEntry.getType().equals("string")) builder.setStringValue((String) dataEntry.getValue());
        else if (dataEntry.getType().equals("boolean")) builder.setBoolValue((Boolean) dataEntry.getValue());
        else if (dataEntry.getType().equals("binary"))
            builder.setBinaryValue(toPBByteString((com.wavesplatform.wavesj.ByteString) dataEntry.getValue()));

        return builder.setKey(dataEntry.getKey()).build();
    }

    public static Order toVanillaOrder(final TransactionOuterClass.ExchangeTransactionData.Order order) {
        final Order.Type orderType = order.getOrderSide() == TransactionOuterClass.ExchangeTransactionData.Order.Side.BUY ? Order.Type.BUY : Order.Type.SELL;
        final AssetPair assetPair = new AssetPair(toVanillaAssetId(order.getAssetPair().getAmountAssetId()), toVanillaAssetId(order.getAssetPair().getPriceAssetId()));

        return new Order(orderType, assetPair, order.getAmount(), order.getPrice(), order.getTimestamp(), order.getExpiration(), order.getMatcherFee().getAmount(), new PublicKeyAccount(order.getSenderPublicKey().toByteArray(), (byte) order.getChainId()), new PublicKeyAccount(order.getMatcherPublicKey().toByteArray(), (byte) order.getChainId()), toSignature(order.getProofsList()));
    }

    public static String toRecipientString(final RecipientOuterClass.Recipient recipient, byte chainId) {
        switch (recipient.getRecipientCase()) {
            case ALIAS:
                return recipient.getAlias();
            case ADDRESS:
                final ByteBuffer withoutChecksum = ByteBuffer.allocate(2 + recipient.getAddress().size())
                    .put((byte) 1)
                    .put(recipient.getAddress().toByteArray());
                withoutChecksum.flip();
                final byte[] checksum = Hash.fastHash(withoutChecksum.array(), 0, withoutChecksum.capacity());

                final ByteBuffer addrBytes = ByteBuffer.allocate(withoutChecksum.capacity() + 4)
                        .put(withoutChecksum)
                        .put(checksum, 0, 4);
                addrBytes.flip();
                return Base58.encode(addrBytes.array());

            default:
                throw new IllegalArgumentException("Recipient not supported: " + recipient);
        }
    }

    public RecipientOuterClass.Recipient toPBRecipient(final String recipient) {
        try {
            final byte[] addr = Arrays.copyOfRange(Base58.decode(recipient), 2, addr.length - 4);
            assert addr.length == 20;
            return RecipientOuterClass.Recipient.newBuilder().setAddress().build()
        } catch (Throwable e) {

        }
    }

    private static com.wavesplatform.wavesj.ByteString toSignature(final List<ByteString> proofs) {
        if (proofs.isEmpty()) return com.wavesplatform.wavesj.ByteString.EMPTY;
        else return toVanillaByteString(proofs.get(0));
    }

    private static com.wavesplatform.wavesj.ByteString toVanillaByteString(final ByteString bs) {
        return new com.wavesplatform.wavesj.ByteString(bs.toByteArray());
    }


    private static ByteString toPBByteString(final com.wavesplatform.wavesj.ByteString bs) {
        return ByteString.copyFrom(bs.getBytes());
    }

    private static List<com.wavesplatform.wavesj.ByteString> toVanillaProofs(final List<ByteString> proofs) {
        final List<com.wavesplatform.wavesj.ByteString> result = new ArrayList<com.wavesplatform.wavesj.ByteString>(proofs.size());
        for (ByteString proof : proofs) result.add(toVanillaByteString(proof));
        return Collections.unmodifiableList(result);
    }

    private static List<DataEntry<?>> toVanillaDataEntryList(final List<TransactionOuterClass.DataTransactionData.DataEntry> dataEntries) {
        final List<DataEntry<?>> result = new ArrayList<DataEntry<?>>(dataEntries.size());
        for (TransactionOuterClass.DataTransactionData.DataEntry dataEntry : dataEntries)
            result.add(toVanillaDataEntry(dataEntry));
        return Collections.unmodifiableList(result);
    }
}
