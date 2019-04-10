package com.wavesplatform.wavesj.json.deser;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.wavesplatform.wavesj.Transaction;
import com.wavesplatform.wavesj.json.WavesJsonMapper;
import com.wavesplatform.wavesj.transactions.*;

import java.io.IOException;

public class TransactionDeserializer extends StdDeserializer<Transaction> {

    private WavesJsonMapper objectMapper;

    public TransactionDeserializer(WavesJsonMapper objectMapper) {
        super(Transaction.class);
        this.objectMapper = objectMapper;
    }

    @Override
    public Transaction deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        TreeNode treeNode = jsonParser.getCodec().readTree(jsonParser);
        int type = objectMapper.treeToValue(treeNode.get("type"), Integer.class);
        int version = objectMapper.treeToValue(treeNode.get("version"), Integer.class);
        byte chainId = objectMapper.getChainId();
        if (treeNode.get("chainId") != null && !(treeNode.get("chainId") instanceof NullNode)) {
            Byte _chainId = objectMapper.treeToValue(treeNode.get("chainId"), Byte.class);
            if (_chainId != null) chainId = _chainId;
        }
        // todo omfg remove after 0.15.4 release
        ((ObjectNode)treeNode).put("chainId", chainId);

        Class t = null;
        switch (type) {
            case AliasTransaction.ALIAS:
                switch (version) {
                    case Transaction.V1:
                        t = AliasTransactionV1.class;
                        break;
                    case Transaction.V2:
                        t = AliasTransactionV2.class;
                        break;
                }
                break;
            case BurnTransaction.BURN:
                switch (version) {
                    case Transaction.V1:
                        t = BurnTransactionV1.class;
                        break;
                    case Transaction.V2:
                        t = BurnTransactionV2.class;
                        break;
                }
                break;
            case DataTransaction.DATA:
                t = DataTransaction.class;
                break;
            case IssueTransaction.ISSUE:
                switch (version) {
                    case Transaction.V1:
                        t = IssueTransactionV1.class;
                        break;
                    case Transaction.V2:
                        t = IssueTransactionV2.class;
                        break;
                }
                break;
            case LeaseCancelTransaction.LEASE_CANCEL:
                switch (version) {
                    case Transaction.V1:
                        t = LeaseCancelTransactionV1.class;
                        break;
                    case Transaction.V2:
                        t = LeaseCancelTransactionV2.class;
                        break;
                }
                break;
            case LeaseTransaction.LEASE:
                switch (version) {
                    case Transaction.V1:
                        t = LeaseTransactionV1.class;
                        break;
                    case Transaction.V2:
                        t = LeaseTransactionV2.class;
                        break;
                }
                break;
            case MassTransferTransaction.MASS_TRANSFER:
                t = MassTransferTransaction.class;
                break;
            case ReissueTransaction.REISSUE:
                switch (version) {
                    case Transaction.V1:
                        t = ReissueTransactionV1.class;
                        break;
                    case Transaction.V2:
                        t = ReissueTransactionV2.class;
                        break;
                }
                break;
            case SetScriptTransaction.SET_SCRIPT:
                t = SetScriptTransaction.class;
                break;
            case SponsorTransaction.SPONSOR:
                t = SponsorTransaction.class;
                break;
            case ExchangeTransaction.EXCHANGE:
                t = ExchangeTransaction.class;
                break;
            case TransferTransaction.TRANSFER:
                switch (version) {
                    case Transaction.V1:
                        t = TransferTransactionV1.class;
                        break;
                    case Transaction.V2:
                        t = TransferTransactionV2.class;
                        break;
                }
                break;
            case ContractInvocationTransaction.CONTRACT_INVOKE:
                t = ContractInvocationTransaction.class;
                break;
            default:
                t = UnknownTransaction.class;
        }

        return (Transaction) objectMapper.treeToValue(treeNode, t);
    }
}
