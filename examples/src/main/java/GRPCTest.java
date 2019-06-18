import com.wavesplatform.api.grpc.TransactionsApiOuterClass.*;
import com.wavesplatform.api.grpc.TransactionsApiGrpc;
import com.wavesplatform.protobuf.transaction.RecipientOuterClass;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.Iterator;

public class GRPCTest {
    public static void main(String[] args) {
        final ManagedChannel channel = ManagedChannelBuilder.forAddress("mainnet-aws-fr-3.wavesnodes.com", 6870)
                .usePlaintext()
                .build();

        final BlocksApiGrpc.BlocksApiBlockingStub blocksApiBlockingStub = BlocksApiGrpc.newBlockingStub(channel);
        final BlocksApiOuterClass.BlockWithHeight block = blocksApiBlockingStub.getBlock(BlocksApiOuterClass.BlockRequest.newBuilder().setHeight(1000).build());

        System.out.println(block);

        final TransactionsApiGrpc.TransactionsApiBlockingStub blockingStub = TransactionsApiGrpc.newBlockingStub(channel);
        final TransactionsRequest transactionsRequest = TransactionsRequest.newBuilder()
                .setRecipient(RecipientOuterClass.Recipient.newBuilder().setAlias("test_recipient").build())
                .build();

        final Iterator<TransactionResponse> transactions = blockingStub.getTransactions(transactionsRequest);
        for (int i = 0; i < 1000 && transactions.hasNext(); i++) {
            final TransactionResponse txResponse = transactions.next();
            System.out.printf("Transaction = %s, height = %d\n", txResponse.getTransaction(), txResponse.getHeight());
        }
    }
}
