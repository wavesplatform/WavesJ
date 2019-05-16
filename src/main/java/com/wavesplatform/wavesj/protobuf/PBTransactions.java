package com.wavesplatform.wavesj.protobuf;

import com.google.protobuf.ByteString;
import com.wavesplatform.protobuf.transaction.TransactionOuterClass;
import com.wavesplatform.wavesj.*;
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
        }

        throw new IllegalArgumentException("Invalid TX: " + tx);
    }
}
