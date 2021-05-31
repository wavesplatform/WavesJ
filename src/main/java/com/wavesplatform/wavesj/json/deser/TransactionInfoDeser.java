package com.wavesplatform.wavesj.json.deser;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.wavesplatform.transactions.*;
import com.wavesplatform.wavesj.ApplicationStatus;
import com.wavesplatform.wavesj.LeaseInfo;
import com.wavesplatform.wavesj.LeaseStatus;
import com.wavesplatform.wavesj.StateChanges;
import com.wavesplatform.wavesj.info.*;

import java.io.IOException;

public class TransactionInfoDeser extends JsonDeserializer<TransactionInfo> {

    @Override
    public TransactionInfo deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        ObjectCodec codec = p.getCodec();
        JsonNode json = codec.readTree(p);

        //transaction fields and info fields are on the same level
        Transaction tx = Transaction.fromJson(json.toString());
        ApplicationStatus status = codec.treeToValue(json.get("applicationStatus"), ApplicationStatus.class);
        int height = json.get("height").asInt();

        if (tx instanceof GenesisTransaction)
            return new GenesisTransactionInfo((GenesisTransaction) tx, status, height);
        else if (tx instanceof PaymentTransaction)
            return new PaymentTransactionInfo((PaymentTransaction) tx, status, height);
        else if (tx instanceof IssueTransaction)
            return new IssueTransactionInfo((IssueTransaction) tx, status, height);
        else if (tx instanceof TransferTransaction)
            return new TransferTransactionInfo((TransferTransaction) tx, status, height);
        else if (tx instanceof ReissueTransaction)
            return new ReissueTransactionInfo((ReissueTransaction) tx, status, height);
        else if (tx instanceof BurnTransaction)
            return new BurnTransactionInfo((BurnTransaction) tx, status, height);
        else if (tx instanceof ExchangeTransaction)
            return new ExchangeTransactionInfo((ExchangeTransaction) tx, status, height);
        else if (tx instanceof LeaseTransaction)
            return new LeaseTransactionInfo((LeaseTransaction) tx, status, height,
                    codec.treeToValue(json.get("status"), LeaseStatus.class));
        else if (tx instanceof LeaseCancelTransaction)
            return new LeaseCancelTransactionInfo((LeaseCancelTransaction) tx, status, height,
                    codec.treeToValue(json.get("lease"), LeaseInfo.class));
        else if (tx instanceof CreateAliasTransaction)
            return new CreateAliasTransactionInfo((CreateAliasTransaction) tx, status, height);
        else if (tx instanceof DataTransaction)
            return new DataTransactionInfo((DataTransaction) tx, status, height);
        else if (tx instanceof MassTransferTransaction)
            return new MassTransferTransactionInfo((MassTransferTransaction) tx, status, height);
        else if (tx instanceof SetScriptTransaction)
            return new SetScriptTransactionInfo((SetScriptTransaction) tx, status, height);
        else if (tx instanceof SponsorFeeTransaction)
            return new SponsorFeeTransactionInfo((SponsorFeeTransaction) tx, status, height);
        else if (tx instanceof SetAssetScriptTransaction)
            return new SetAssetScriptTransactionInfo((SetAssetScriptTransaction) tx, status, height);
        else if (tx instanceof InvokeScriptTransaction)
            return new InvokeScriptTransactionInfo((InvokeScriptTransaction) tx, status, height,
                    codec.treeToValue(json.get("stateChanges"), StateChanges.class));
        else if (tx instanceof UpdateAssetInfoTransaction)
            return new UpdateAssetInfoTransactionInfo((UpdateAssetInfoTransaction) tx, status, height);
        else
            throw new IOException("Can't parse transaction info: " + json.toString());
    }
}
