package com.wavesplatform.wavesj.protobuf;

import com.google.protobuf.ByteString;
import com.wavesplatform.protobuf.AmountOuterClass;
import com.wavesplatform.protobuf.order.OrderOuterClass;
import com.wavesplatform.protobuf.transaction.RecipientOuterClass;
import com.wavesplatform.protobuf.transaction.TransactionOuterClass;
import com.wavesplatform.wavesj.*;
import com.wavesplatform.wavesj.matcher.Order;
import com.wavesplatform.wavesj.matcher.OrderV1;
import com.wavesplatform.wavesj.matcher.OrderV2;
import com.wavesplatform.wavesj.transactions.*;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@SuppressWarnings({"unused", "WeakerAccess"})
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
            switch (tx.getVersion()) {
                case Transaction.V1:
                    return new ExchangeTransactionV1(toVanillaOrder(exchange.getOrders(0)), toVanillaOrder(exchange.getOrders(1)), senderPublicKey, exchange.getAmount(), exchange.getPrice(), exchange.getBuyMatcherFee(), exchange.getSellMatcherFee(), feeAmount, timestamp, signature);

                case Transaction.V2:
                    return new ExchangeTransactionV2(senderPublicKey, toVanillaOrder(exchange.getOrders(0)), toVanillaOrder(exchange.getOrders(1)), exchange.getAmount(), exchange.getPrice(), exchange.getBuyMatcherFee(), exchange.getSellMatcherFee(), feeAmount, timestamp, proofs);
            }

        }  else if (tx.hasInvokeScript()) {
            final TransactionOuterClass.InvokeScriptTransactionData data = tx.getInvokeScript();
            final List<InvokeScriptTransaction.Payment> payments = new ArrayList<InvokeScriptTransaction.Payment>(data.getPaymentsCount());
            for (AmountOuterClass.Amount payment : data.getPaymentsList()) payments.add(new InvokeScriptTransaction.Payment(payment.getAmount(), toVanillaAssetId(payment.getAssetId())));
            return new InvokeScriptTransaction((byte) tx.getChainId(), senderPublicKey, Base58.encode(data.getDApp().toByteArray()), InvokeScriptTransaction.FunctionCall.fromBytes(data.getFunctionCall().asReadOnlyByteBuffer()), Collections.unmodifiableList(payments), feeAmount, toVanillaAssetId(tx.getFee().getAssetId()), timestamp, proofs);
        } else if (tx.hasIssue()) {
            final TransactionOuterClass.IssueTransactionData issue = tx.getIssue();
            switch (tx.getVersion()) {
                case Transaction.V1:
                    return new IssueTransactionV1(senderPublicKey, issue.getName(), issue.getDescription(), issue.getAmount(), (byte) issue.getDecimals(), issue.getReissuable(), feeAmount, timestamp, signature);

                case Transaction.V2:
                    return new IssueTransactionV2(senderPublicKey, (byte) tx.getChainId(), issue.getName(), issue.getDescription(), issue.getAmount(), (byte) issue.getDecimals(), issue.getReissuable(), Base64.encode(issue.getScript().toByteArray()), feeAmount, timestamp, proofs);
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
            final TransactionOuterClass.SetAssetScriptTransactionData sas = tx.getSetAssetScript();
            return new SetAssetScriptTransaction(senderPublicKey, (byte) tx.getChainId(), toVanillaAssetId(sas.getAssetId()), Base58.encode(sas.getScript().toByteArray()), feeAmount, timestamp, proofs);
        } else if (tx.hasSetScript()) {
            final TransactionOuterClass.SetScriptTransactionData setScript = tx.getSetScript();
            return new SetScriptTransaction(senderPublicKey, Base58.encode(setScript.getScript().toByteArray()), (byte) tx.getChainId(), feeAmount, timestamp, proofs);
        } else if (tx.hasTransfer()) {
            final TransactionOuterClass.TransferTransactionData transfer = tx.getTransfer();
            switch (tx.getVersion()) {
                case Transaction.V1:
                    return new TransferTransactionV1(senderPublicKey, toRecipientString(transfer.getRecipient(), (byte) tx.getChainId()), transfer.getAmount().getAmount(), toVanillaAssetId(transfer.getAmount().getAssetId()), feeAmount, toVanillaAssetId(tx.getFee().getAssetId()), toVanillaByteString(transfer.getAttachment()), timestamp, signature);

                case Transaction.V2:
                    return new TransferTransactionV2(senderPublicKey, toRecipientString(transfer.getRecipient(), (byte) tx.getChainId()), transfer.getAmount().getAmount(), toVanillaAssetId(transfer.getAmount().getAssetId()), feeAmount, toVanillaAssetId(tx.getFee().getAssetId()), toVanillaByteString(transfer.getAttachment()), timestamp, proofs);
            }
        } else if (tx.hasLease()) {
            final TransactionOuterClass.LeaseTransactionData lease = tx.getLease();
            switch (tx.getVersion()) {
                case Transaction.V1:
                    return new LeaseTransactionV1(senderPublicKey, toRecipientString(lease.getRecipient(), (byte) tx.getChainId()), lease.getAmount(), feeAmount, timestamp, signature);

                case Transaction.V2:
                    return new LeaseTransactionV2(senderPublicKey, toRecipientString(lease.getRecipient(), (byte) tx.getChainId()), lease.getAmount(), feeAmount, timestamp, proofs);
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
                final Transfer transfer1 = new Transfer(toRecipientString(transfer.getRecipient(), (byte) tx.getChainId()), transfer.getAmount());
                transfers.add(transfer1);
            }
            return new MassTransferTransaction(senderPublicKey, toVanillaAssetId(massTransfer.getAssetId()), Collections.unmodifiableList(transfers), feeAmount, toVanillaByteString(massTransfer.getAttachment()), timestamp, proofs);
        } else if (tx.hasSponsorFee()) {
            final TransactionOuterClass.SponsorFeeTransactionData sponsorFee = tx.getSponsorFee();
            return new SponsorTransaction(senderPublicKey, toVanillaAssetId(sponsorFee.getMinFee().getAssetId()), sponsorFee.getMinFee().getAmount(), feeAmount, timestamp, proofs);
        }

        throw new IllegalArgumentException("Invalid TX: " + tx);
    }

    public static TransactionOuterClass.SignedTransaction toPB(final Transaction tx) {
        TransactionOuterClass.Transaction.Builder base = TransactionOuterClass.Transaction.newBuilder()
                .setFee(toPBAmount(Asset.WAVES, tx.getFee()))
                .setTimestamp(tx.getTimestamp())
                .setVersion(tx.getVersion());

        if (tx instanceof IssueTransaction) {
            final IssueTransaction issue = (IssueTransaction) tx;

            ByteString script = ByteString.EMPTY;
            if (issue instanceof IssueTransactionV2)
                script = ByteString.copyFrom(Base64.decode(((IssueTransactionV2) issue).getScript()));

            final TransactionOuterClass.IssueTransactionData data = TransactionOuterClass.IssueTransactionData.newBuilder()
                    .setAmount(issue.getQuantity())
                    .setDecimals(issue.getDecimals())
                    .setName(issue.getName())
                    .setDescription(issue.getDescription())
                    .setReissuable(issue.isReissuable())
                    .setScript(script)
                    .build();
            base.setIssue(data);
        } else if (tx instanceof ReissueTransaction) {
            final ReissueTransaction reissue = (ReissueTransaction) tx;
            final TransactionOuterClass.ReissueTransactionData data = TransactionOuterClass.ReissueTransactionData.newBuilder()
                    .setAssetAmount(AmountOuterClass.Amount.newBuilder().setAssetId(assetIdToBytes(reissue.getAssetId())).setAmount(reissue.getQuantity()).build())
                    .setReissuable(reissue.isReissuable())
                    .build();
            base.setReissue(data);
        } else if (tx instanceof BurnTransaction) {
            final BurnTransaction burn = (BurnTransaction) tx;
            final TransactionOuterClass.BurnTransactionData data = TransactionOuterClass.BurnTransactionData.newBuilder()
                    .setAssetAmount(AmountOuterClass.Amount.newBuilder().setAssetId(assetIdToBytes(burn.getAssetId())).setAmount(burn.getAmount()).build())
                    .build();
            base.setBurn(data);
        } else if (tx instanceof SetScriptTransaction) {
            final SetScriptTransaction setScript = (SetScriptTransaction) tx;
            final TransactionOuterClass.SetScriptTransactionData data = TransactionOuterClass.SetScriptTransactionData.newBuilder()
                    .setScript(ByteString.copyFrom(Base64.decode(setScript.getScript())))
                    .build();
            base.setSetScript(data);
        } else if (tx instanceof SetAssetScriptTransaction) {
            final SetAssetScriptTransaction sas = (SetAssetScriptTransaction) tx;
            final TransactionOuterClass.SetAssetScriptTransactionData data = TransactionOuterClass.SetAssetScriptTransactionData
                    .newBuilder()
                    .setAssetId(assetIdToBytes(sas.getAssetId()))
                    .setScript(ByteString.copyFrom(Base64.decode(sas.getScript())))
                    .build();
            base.setSetAssetScript(data);
        } else if (tx instanceof DataTransaction) {
            final DataTransaction dataTransaction = (DataTransaction) tx;

            final List<TransactionOuterClass.DataTransactionData.DataEntry> dataEntries = new ArrayList<TransactionOuterClass.DataTransactionData.DataEntry>(dataTransaction.getData().size());
            for (DataEntry<?> dataEntry : dataTransaction.getData()) dataEntries.add(toPBDataEntry(dataEntry));

            final TransactionOuterClass.DataTransactionData data = TransactionOuterClass.DataTransactionData.newBuilder()
                    .addAllData(dataEntries)
                    .build();

            base.setDataTransaction(data);
        } else if (tx instanceof MassTransferTransaction) {
            final MassTransferTransaction mtt = (MassTransferTransaction) tx;
            final ByteString assetId = assetIdToBytes(mtt.getAssetId());
            final List<TransactionOuterClass.MassTransferTransactionData.Transfer> transfers = new ArrayList<TransactionOuterClass.MassTransferTransactionData.Transfer>(mtt.getTransfers().size());
            for (Transfer transfer : mtt.getTransfers()) {
                TransactionOuterClass.MassTransferTransactionData.Transfer transfer1 = TransactionOuterClass.MassTransferTransactionData.Transfer.newBuilder()
                        .setRecipient(toPBRecipient(transfer.getRecipient()))
                        .setAmount(transfer.getAmount()).build();
                transfers.add(transfer1);
            }

            final TransactionOuterClass.MassTransferTransactionData data = TransactionOuterClass.MassTransferTransactionData.newBuilder()
                    .setAssetId(assetId)
                    .setAttachment(toPBByteString(mtt.getAttachment()))
                    .addAllTransfers(transfers)
                    .build();
            base.setMassTransfer(data);
        } else if (tx instanceof TransferTransaction) {
            final TransferTransaction transfer = (TransferTransaction) tx;
            final TransactionOuterClass.TransferTransactionData data = TransactionOuterClass.TransferTransactionData.newBuilder()
                    .setRecipient(toPBRecipient(transfer.getRecipient()))
                    .setAmount(toPBAmount(transfer.getAssetId(), transfer.getAmount()))
                    .setAttachment(toPBByteString(transfer.getAttachment()))
                    .build();
            base.setTransfer(data);

            if (tx instanceof TransferTransactionV2)
                base.setFee(toPBAmount(((TransferTransactionV2) tx).getFeeAssetId(), tx.getFee()));
        } else if (tx instanceof SponsorTransaction) {
            final SponsorTransaction sponsor = (SponsorTransaction) tx;
            final TransactionOuterClass.SponsorFeeTransactionData data = TransactionOuterClass.SponsorFeeTransactionData.newBuilder()
                    .setMinFee(AmountOuterClass.Amount.newBuilder().setAssetId(assetIdToBytes(sponsor.getAssetId())).setAmount(sponsor.getMinSponsoredAssetFee()).build())
                    .build();
            base.setSponsorFee(data);
        } else if (tx instanceof ExchangeTransaction) {
            final ExchangeTransaction exchange = (ExchangeTransaction) tx;
            final TransactionOuterClass.ExchangeTransactionData data = TransactionOuterClass.ExchangeTransactionData.newBuilder()
                    .setPrice(exchange.getPrice())
                    .setAmount(exchange.getAmount())
                    .setBuyMatcherFee(exchange.getBuyMatcherFee())
                    .setSellMatcherFee(exchange.getSellMatcherFee())
                    .addOrders(toPBOrder(exchange.getOrder1()))
                    .addOrders(toPBOrder(exchange.getOrder2()))
                    .build();
            base.setExchange(data);
        } else if (tx instanceof InvokeScriptTransaction) {
            final InvokeScriptTransaction ist = (InvokeScriptTransaction) tx;
            final List<AmountOuterClass.Amount> payments = new ArrayList<AmountOuterClass.Amount>(ist.getPayments().size());
            for (InvokeScriptTransaction.Payment payment : ist.getPayments())
                payments.add(toPBAmount(payment.getAssetId(), payment.getAmount()));

            final TransactionOuterClass.InvokeScriptTransactionData data = TransactionOuterClass.InvokeScriptTransactionData.newBuilder()
                    .setDApp(toPBRecipient(ist.getdApp()))
                    .setFunctionCall(ByteString.copyFrom(ist.getCall().toBytes()))
                    .addAllPayments(payments)
                    .build();
            base.setInvokeScript(data);
        }

        List<ByteString> proofs = new ArrayList<ByteString>();
        if (tx instanceof TransactionWithProofs)
            //noinspection unchecked
            for (com.wavesplatform.wavesj.ByteString proof : ((List<com.wavesplatform.wavesj.ByteString>) (((TransactionWithProofs) tx).getProofs())))
                proofs.add(toPBByteString(proof));
        else if (tx instanceof TransactionWithSignature)
            proofs.add(toPBByteString(((TransactionWithSignature) tx).getSignature()));

        return TransactionOuterClass.SignedTransaction.newBuilder()
                .addAllProofs(proofs)
                .setTransaction(base)
                .build();
    }

    public static String toVanillaAssetId(final ByteString assetId) {
        if (assetId.isEmpty()) return Asset.WAVES;
        else return Asset.normalize(Base58.encode(assetId.toByteArray()));
    }


    public static ByteString assetIdToBytes(final String assetId) {
        if (Asset.isWaves(assetId)) return ByteString.EMPTY;
        else return ByteString.copyFrom(Base58.decode(assetId));
    }

    public static AmountOuterClass.Amount toPBAmount(final String assetId, final long amount) {
        return AmountOuterClass.Amount.newBuilder().setAssetId(assetIdToBytes(assetId)).build();
    }

    public static DataEntry<?> toVanillaDataEntry(final TransactionOuterClass.DataTransactionData.DataEntry dataEntry) {
        DataEntry<?> result;
        switch (dataEntry.getValueCase()) {
            case STRING_VALUE:
                result = new DataEntry.StringEntry(dataEntry.getKey(), dataEntry.getStringValue());
                break;
            case BOOL_VALUE:
                result = new DataEntry.BooleanEntry(dataEntry.getKey(), dataEntry.getBoolValue());
                break;
            case INT_VALUE:
                result = new DataEntry.LongEntry(dataEntry.getKey(), dataEntry.getIntValue());
                break;
            case BINARY_VALUE:
                result = new DataEntry.BinaryEntry(dataEntry.getKey(), toVanillaByteString(dataEntry.getBinaryValue()));
                break;
            default:
                throw new IllegalArgumentException("Not supported: " + dataEntry);
        }
        return result;
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

    public static Order toVanillaOrder(final OrderOuterClass.Order order) {
        final Order.Type orderType = order.getOrderSide() == OrderOuterClass.Order.Side.BUY ? Order.Type.BUY : Order.Type.SELL;
        final AssetPair assetPair = new AssetPair(toVanillaAssetId(order.getAssetPair().getAmountAssetId()), toVanillaAssetId(order.getAssetPair().getPriceAssetId()));
        final PublicKeyAccount senderPublicKey = new PublicKeyAccount(order.getSenderPublicKey().toByteArray(), (byte) order.getChainId());
        final PublicKeyAccount matcherPk = new PublicKeyAccount(order.getMatcherPublicKey().toByteArray(), (byte) order.getChainId());

        switch (order.getVersion()) {
            case Order.V1:
                return new OrderV1(senderPublicKey, matcherPk, orderType, assetPair, order.getAmount(), order.getPrice(), order.getTimestamp(), order.getExpiration(), order.getMatcherFee().getAmount(), toSignature(order.getProofsList()));
            case Order.V2:
                return new OrderV2(senderPublicKey, matcherPk, orderType, assetPair, order.getAmount(), order.getPrice(), order.getTimestamp(), order.getExpiration(), order.getMatcherFee().getAmount(), (byte) order.getVersion(), toVanillaProofs(order.getProofsList()));
            default:
                throw new IllegalArgumentException("Order not supported: " + order);
        }
    }

    public static OrderOuterClass.Order toPBOrder(final Order order) {
        final OrderOuterClass.Order.Side orderType = order.getOrderType() == Order.Type.BUY ? OrderOuterClass.Order.Side.BUY : OrderOuterClass.Order.Side.SELL;

        final List<ByteString> proofs = new ArrayList<ByteString>(order.getProofs().size());
        for (com.wavesplatform.wavesj.ByteString proof : order.getProofs()) proofs.add(toPBByteString(proof));

        return OrderOuterClass.Order.newBuilder()
                .setAmount(order.getAmount())
                .setPrice(order.getPrice())
                .setAssetPair(OrderOuterClass.AssetPair.newBuilder()
                        .setAmountAssetId(assetIdToBytes(order.getAssetPair().getAmountAsset()))
                        .setPriceAssetId(assetIdToBytes(order.getAssetPair().getPriceAsset())).build())
                .setExpiration(order.getExpiration())
                .setMatcherFee(AmountOuterClass.Amount.newBuilder().setAmount(order.getMatcherFee()).build())
                .setMatcherPublicKey(ByteString.copyFrom(order.getMatcherPublicKey().getPublicKey()))
                .setOrderSide(orderType)
                .setSenderPublicKey(ByteString.copyFrom(order.getSenderPublicKey().getPublicKey()))
                .setVersion(1)
                .addAllProofs(proofs)
                .build();
    }

    public static String toRecipientString(final RecipientOuterClass.Recipient recipient, final byte chainId) {
        switch (recipient.getRecipientCase()) {
            case ALIAS:
                return recipient.getAlias();
            case PUBLIC_KEY_HASH:
                final ByteBuffer withoutChecksum = ByteBuffer.allocate(2 + recipient.getPublicKeyHash().size())
                        .put((byte) 1)
                        .put(chainId)
                        .put(recipient.getPublicKeyHash().toByteArray());
                withoutChecksum.flip();
                final byte[] checksum = Hash.secureHash(withoutChecksum.array(), 0, withoutChecksum.capacity());

                final ByteBuffer addrBytes = ByteBuffer.allocate(withoutChecksum.capacity() + 4)
                        .put(withoutChecksum)
                        .put(checksum, 0, 4);
                addrBytes.flip();
                return Base58.encode(addrBytes.array());

            default:
                throw new IllegalArgumentException("Recipient not supported: " + recipient);
        }
    }

    public static RecipientOuterClass.Recipient toPBRecipient(final String recipient) {
        try {
            final byte[] sourceAddr = Base58.decode(recipient);
            assert sourceAddr.length == 20;

            final byte[] pubKeyHash = Arrays.copyOfRange(sourceAddr, 2, sourceAddr.length - 4);
            return RecipientOuterClass.Recipient.newBuilder().setPublicKeyHash(ByteString.copyFrom(pubKeyHash)).build();
        } catch (Throwable e) {
            return RecipientOuterClass.Recipient.newBuilder().setAlias(recipient).build();
        }
    }

    private static com.wavesplatform.wavesj.ByteString toSignature(final List<ByteString> proofs) {
        if (proofs.isEmpty()) return com.wavesplatform.wavesj.ByteString.EMPTY;
        else return toVanillaByteString(proofs.get(0));
    }

    public static com.wavesplatform.wavesj.ByteString toVanillaByteString(final ByteString bs) {
        return new com.wavesplatform.wavesj.ByteString(bs.toByteArray());
    }


    public static ByteString toPBByteString(final com.wavesplatform.wavesj.ByteString bs) {
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
