package com.wavesplatform.wavesj;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.util.Map;

public class Node {
    private final String url;
    private final CloseableHttpClient client = HttpClients.createDefault();

    public Node(String url) {
        this.url = url;
    }

    public CloseableHttpResponse send(Transaction tx, String path) throws IOException {
        HttpPost request = new HttpPost(url + path);
        request.setHeader("Content-Type", "application/json");
        request.setHeader("Accept", "application/json");
        request.setEntity(new StringEntity(toJson(tx.getData())));
        return client.execute(request);
    }

    private static String toJson(Map<String, Object> data) {
        StringBuilder json = new StringBuilder("{\n");
        data.forEach((k,v) -> {
            boolean primitive = v instanceof Number;
            json.append('"').append(k).append("\": ");
            if (! primitive) json.append('"');
            json.append(v);
            if (! primitive) json.append('"');
            json.append(",\n");
        });
        json.deleteCharAt(json.length()-2).append("}");
        return json.toString();
    }
}
