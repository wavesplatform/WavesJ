package com.wavesplatform.wavesj.json.deser;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.IntNode;
import com.wavesplatform.wavesj.Transaction;
import com.wavesplatform.wavesj.transactions.*;

import java.io.IOException;
import java.util.Map;

public class TransactionDeser extends JsonDeserializer<Transaction> {
    static final protected ObjectMapper mapper = new ObjectMapper();
    static final TypeReference<Map<String, Object>> TX_INFO = new TypeReference<Map<String, Object>>() {};

    @Override
    public Transaction deserialize(JsonParser p, DeserializationContext context) throws IOException {
        TreeNode n = p.getCodec().readTree(p);
        IntNode typeN = (IntNode) n.get("type");
        TypeReference<? extends Transaction> t = null;
//        switch (typeN.intValue()) {
//            case AliasTransaction.ALIAS: t = AliasTransaction.TRANSACTION_TYPE; break;
//            case BurnTransaction.BURN: t = BurnTransaction.TRANSACTION_TYPE; break;
//            case DataTransaction.DATA: t = DataTransaction.TRANSACTION_TYPE; break;
//            case IssueTransaction.ISSUE: t = IssueTransaction.TRANSACTION_TYPE; break;
//            case LeaseCancelTransaction.LEASE_CANCEL: t = LeaseCancelTransaction.TRANSACTION_TYPE; break;
//            case LeaseTransaction.LEASE: t = LeaseTransaction.TRANSACTION_TYPE; break;
//            case MassTransferTransaction.MASS_TRANSFER: t = MassTransferTransaction.TRANSACTION_TYPE; break;
//            case ReissueTransaction.REISSUE: t = ReissueTransaction.TRANSACTION_TYPE; break;
//            case SetScriptTransaction.SET_SCRIPT: t = SetScriptTransaction.TRANSACTION_TYPE; break;
//            case SponsorTransaction.SPONSOR: t = SponsorTransaction.TRANSACTION_TYPE; break;
//            case TransferTransaction.TRANSFER: t = TransferTransaction.TRANSACTION_TYPE; break;
//            default: throw new IllegalArgumentException();
//        }
        return mapper.reader(t).readValue(p);
    }
}
