package com.wavesplatform.wavesj;

import java.net.URI;
import java.net.URISyntaxException;

public enum Profile {
    MAINNET("https://nodes.wavesnodes.com"),
    TESTNET("https://nodes-testnet.wavesnodes.com"),
    STAGENET("https://nodes-stagenet.wavesnodes.com"),
    LOCAL("http://127.0.0.1:6869");

    private final URI uri;

    Profile(String url) {
        try {
            this.uri = new URI(url);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public URI uri() {
        return uri;
    }
}
