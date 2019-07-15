package com.wavesplatform.wavesj.grpc;

import com.wavesplatform.wavesj.grpc.entities.Block;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

class RESTTestInterfaceImpl {

    static String getGeneratorByPublKey(String publKey) throws IOException {
        String url = "http://localhost:6869/addresses/publicKey/" + publKey;
        return doRequest(url);
    }

    static String getBlockByHeight(int height) throws IOException {
        String url = "http://localhost:6869/blocks/at/" + height;
        return doRequest(url);
    }

    static String getBlockById(String id) throws IOException {
        String url = "http://localhost:6869/blocks/signature/" + id;
        return doRequest(url);
    }

    static String getBlockByRef(String reference) throws IOException {
        String url = "http://localhost:6869/signature/" + reference;
        Block block = Utils.getBlockFromJson(doRequest(url));
        return getBlockByHeight(block.getHeight() + 1);
    }

    static String getBlocksByRange(int from, int to) throws IOException {
        String url = "http://localhost:6869/blocks/seq/" + from + "/" + to;
        return doRequest(url);
    }

    private static String doRequest(String url) throws IOException {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();
    }
}
