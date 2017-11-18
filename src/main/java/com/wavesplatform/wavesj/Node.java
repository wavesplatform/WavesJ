package com.wavesplatform.wavesj;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.util.Map;

public class Node {
    public static final String DEFAULT_NODE = "https://testnode1.wavesnodes.com";

    private final String url;
    private final CloseableHttpClient client = HttpClients.createDefault();

    public Node() {
        this(DEFAULT_NODE);
    }

    public Node(String url) {
        this.url = url;
    }

    public int getHeight() throws IOException {
        return send("/blocks/height", "height", Integer.class);
    }

    public long getBalance(String address) throws IOException {
        return send("/addresses/balance/" + address, "balance", Long.class);
    }

    public long getBalance(String address, int confirmations) throws IOException {
        return send("/addresses/balance/" + address + "/" + confirmations, "balance", Long.class);
    }

    public long getBalance(String address, String assetId) throws IOException {
        return send("/assets/balance/" + address + "/" + assetId, "balance", Long.class);
    }

    public String transfer(PrivateKeyAccount from, String toAddress, long amount, long fee, String message) throws IOException {
        Transaction tx = BlockchainTransaction.makeTransferTx(from, toAddress, amount, null, fee, null, message);
        return send(tx, "/assets/broadcast/transfer");
    }

    public String transferAsset(PrivateKeyAccount from, String toAddress,
            long amount, String assetId, long fee, String feeAssetId, String message) throws IOException
    {
        Transaction tx = BlockchainTransaction.makeTransferTx(from, toAddress, amount, assetId, fee, feeAssetId, message);
        return send(tx, "/assets/broadcast/transfer");
    }

    public String lease(PrivateKeyAccount from, String toAddress, long amount, long fee) throws IOException {
        Transaction tx = BlockchainTransaction.makeLeaseTx(from, toAddress, amount, fee);
        return send(tx, "/leasing/lease");
    }

    public String cancelLease(PrivateKeyAccount account, String assetId, long fee) throws IOException {
        Transaction tx = BlockchainTransaction.makeLeaseCancelTx(account, assetId, fee);
        return send(tx, "/leasing/cancel");
    }

    public String issueAsset(PrivateKeyAccount account,
            String name, String description, long quantity, int decimals, boolean reissuable, long fee) throws IOException
    {
        Transaction tx = BlockchainTransaction.makeIssueTx(account, name, description, quantity, decimals, reissuable, fee);
        return send(tx, "/assets/broadcast/issue");
    }

    public String reissueAsset(PrivateKeyAccount account, String assetId, long quantity, boolean reissuable, long fee) throws IOException {
        Transaction tx = BlockchainTransaction.makeReissueTx(account, assetId, quantity, reissuable, fee);
        return send(tx, "/assets/broadcast/reissue");
    }

    public String burnAsset(PrivateKeyAccount account, String assetId, long amount, long fee) throws IOException {
        Transaction tx = BlockchainTransaction.makeBurnTx(account, assetId, amount, fee);
        return send(tx, "/assets/broadcast/burn");
    }

    public String alias(PrivateKeyAccount account, String alias, long fee) throws IOException {
        Transaction tx = BlockchainTransaction.makeAliasTx(account, alias, fee);
        return send(tx, "/alias/broadcast/create");
    }

    private <T> T send(String path, String key, Class<T> type) throws IOException {
        HttpGet request = new HttpGet(url + path);
        return exec(request, key, type);
    }

    private String send(Transaction tx, String path) throws IOException {
        String json = new ObjectMapper().writeValueAsString(tx.getData());
        return send(json, path);
    }

    private String send(String txAsJson, String path) throws IOException {
        HttpPost request = new HttpPost(url + path);
        request.setEntity(new StringEntity(txAsJson));
        request.setHeader("Content-Type", "application/json");
        request.setHeader("Accept", "application/json");
        return exec(request, "id", String.class);
    }

    private <T> T exec(HttpUriRequest request, String key, Class<T> type) throws IOException {
        HttpResponse r = client.execute(request);
        if (r.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
            String error = parseResponse(r, "message", String.class);
            throw new IOException(error);
        }
        return parseResponse(r, key, type);
    }

    private static <T> T parseResponse(HttpResponse r, String key, Class<T> type) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        if (type == Long.class) {
            mapper.enable(DeserializationFeature.USE_LONG_FOR_INTS);
        }
        Map data = mapper.readValue(r.getEntity().getContent(), Map.class);
        return type.cast(data.get(key));
    }
}
