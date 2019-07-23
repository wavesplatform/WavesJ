package com.wavesplatform.wavesj.json;

public class ExchangeTransactionTestData {

    public static final String NODE_FORMAT = "{\n" +
            "  \"senderPublicKey\": \"E3UwaHCQCySghK3zwNB8EDHoc3b8uhzGPFz3gHmWon4W\",\n" +
            "  \"id\": \"EHiaiQ2ky1agH3aBGzjrrMtPSXBxVdHxYuS5xrfseMjV\",\n" +
            "  \"timestamp\": 1563620267142,\n" +
            "  \"amount\": 489,\n" +
            "  \"fee\": 2200000,\n" +
            "  \"type\": 7,\n" +
            "  \"version\": 2,\n" +
            "  \"height\": 1623918,\n" +
            "  \"sellMatcherFee\": 1100000,\n" +
            "  \"buyMatcherFee\": 1100000,\n" +
            "  \"sender\": \"3PJjwFREg8F9V6Cp9fnUuEwRts6HQQa5nfP\",\n" +
            "  \"price\": 1059987479202,\n" +
            "  \"feeAssetId\": null,\n" +   // todo "feeAssetId" field is absent in Data Services
            "  \"proofs\": [\n" +
            "    \"4uV6us3PArKBaAEiBVvX13GWGqbJhgdMsQp6c4yYoHDAcsSRN35BgVERZfghN7vq7QSWRa4WW8NKDJLDmcdcXjv2\"\n" +
            "  ],\n" +
            "  \"order1\": {\n" +
            "    \"id\": \"C5ZPajrbTmze3qZTJ4XGzqyJKN8uKWA2gvW5tDRBHys9\",\n" +
            "    \"senderPublicKey\": \"6TNqGk9VFwpNoUq6ZgfHGLpbGEgn5sfheirJF96FqHBT\",\n" +
            "    \"matcherPublicKey\": \"E3UwaHCQCySghK3zwNB8EDHoc3b8uhzGPFz3gHmWon4W\",\n" +
            "    \"orderType\": \"buy\",\n" +
            "    \"sender\": \"3PATy5UyBpYDVa9bGUe14o77zarXztprXfU\",\n" +
            "    \"assetPair\": {\n" +
            "      \"amountAsset\": \"9SxLVHaEGTeEjRiAMnEw74YWWndQDRw8SZhknK9EYoUd\",\n" +
            "      \"priceAsset\": \"ARUnrACYathMJwq6gPvnb9kW2FHXTYLaVbsKwB2JC1DH\"\n" +
            "    },\n" +
            "    \"amount\": 489,\n" +
            "    \"price\": 1059987479202,\n" +
            "    \"timestamp\": 1563620267016,\n" +
            "    \"expiration\": 1566125867016,\n" +
            "    \"matcherFee\": 1100000,\n" +
            "    \"signature\": \"3GVjGqddFa5Fvcs3yNCHLiGgT3ZSvXfnYHUuL9eCALoZg9KKXgBgqPXpoFGMEFRtvMCBrRBh4DmSwS8TYB1iiSvF\",\n" +
            "    \"version\": 2,\n" +       // todo "version" field is absent in Data Services
            "    \"proofs\": [\n" +         // todo "proofs" field is absent in Data Services
            "      \"3GVjGqddFa5Fvcs3yNCHLiGgT3ZSvXfnYHUuL9eCALoZg9KKXgBgqPXpoFGMEFRtvMCBrRBh4DmSwS8TYB1iiSvF\"\n" +
            "    ]\n" +
            "  },\n" +
            "  \"order2\": {\n" +
            "    \"id\": \"6Lma6NnoWa9yd73yg3bvV8jtGqFSQmR7p5bb55DjCM2J\",\n" +
            "    \"senderPublicKey\": \"6TNqGk9VFwpNoUq6ZgfHGLpbGEgn5sfheirJF96FqHBT\",\n" +
            "    \"matcherPublicKey\": \"E3UwaHCQCySghK3zwNB8EDHoc3b8uhzGPFz3gHmWon4W\",\n" +
            "    \"orderType\": \"sell\",\n" +
            "    \"sender\": \"3PATy5UyBpYDVa9bGUe14o77zarXztprXfU\",\n" +
            "    \"assetPair\": {\n" +
            "      \"amountAsset\": \"9SxLVHaEGTeEjRiAMnEw74YWWndQDRw8SZhknK9EYoUd\",\n" +
            "      \"priceAsset\": \"ARUnrACYathMJwq6gPvnb9kW2FHXTYLaVbsKwB2JC1DH\"\n" +
            "    },\n" +
            "    \"amount\": 489,\n" +
            "    \"price\": 1059987479202,\n" +
            "    \"timestamp\": 1563620266775,\n" +
            "    \"expiration\": 1566125866775,\n" +
            "    \"matcherFee\": 1100000,\n" +
            "    \"signature\": \"5vxX4wwY4Q4r5KNpqPXE3qUG3tvEbFW4nLCXA1EEewnALk8qge4PtSPJGJzVrubcZMPVA5acddQkW97iA3wdMD8e\",\n" +
            "    \"version\": 2,\n" +       // todo "version" field is absent in Data Services
            "    \"proofs\": [\n" +         // todo "proofs" field is absent in Data Services
            "      \"5vxX4wwY4Q4r5KNpqPXE3qUG3tvEbFW4nLCXA1EEewnALk8qge4PtSPJGJzVrubcZMPVA5acddQkW97iA3wdMD8e\"\n" +
            "    ]\n" +
            "  }\n" +
            "}";

    public static final String DATA_SRV_FORMAT = "{\n" +
            "  \"__type\": \"transaction\",\n" +
            "  \"data\": {\n" +
            "    \"senderPublicKey\": \"E3UwaHCQCySghK3zwNB8EDHoc3b8uhzGPFz3gHmWon4W\",\n" +
            "    \"id\": \"EHiaiQ2ky1agH3aBGzjrrMtPSXBxVdHxYuS5xrfseMjV\",\n" +
            "    \"timestamp\": \"2019-07-20T10:57:47.142Z\",\n" +
            "    \"amount\": 0.00000489,\n" +
            "    \"fee\": 0.022,\n" +
            "    \"type\": 7,\n" +
            "    \"version\": 2,\n" +
            "    \"height\": 1623918,\n" +
            "    \"sellMatcherFee\": 0.011,\n" +
            "    \"buyMatcherFee\": 0.011,\n" +
            "    \"sender\": \"3PJjwFREg8F9V6Cp9fnUuEwRts6HQQa5nfP\",\n" +
            "    \"price\": 10599.87479202,\n" +
            "    \"proofs\": [\n" +
            "      \"4uV6us3PArKBaAEiBVvX13GWGqbJhgdMsQp6c4yYoHDAcsSRN35BgVERZfghN7vq7QSWRa4WW8NKDJLDmcdcXjv2\"\n" +
            "    ],\n" +
            "    \"order1\": {\n" +
            "      \"id\": \"C5ZPajrbTmze3qZTJ4XGzqyJKN8uKWA2gvW5tDRBHys9\",\n" +
            "      \"senderPublicKey\": \"6TNqGk9VFwpNoUq6ZgfHGLpbGEgn5sfheirJF96FqHBT\",\n" +
            "      \"matcherPublicKey\": \"E3UwaHCQCySghK3zwNB8EDHoc3b8uhzGPFz3gHmWon4W\",\n" +
            "      \"orderType\": \"buy\",\n" +
            "      \"sender\": \"3PATy5UyBpYDVa9bGUe14o77zarXztprXfU\",\n" +
            "      \"assetPair\": {\n" +
            "        \"amountAsset\": \"9SxLVHaEGTeEjRiAMnEw74YWWndQDRw8SZhknK9EYoUd\",\n" +
            "        \"priceAsset\": \"ARUnrACYathMJwq6gPvnb9kW2FHXTYLaVbsKwB2JC1DH\"\n" +
            "      },\n" +
            "      \"amount\": 0.00000489,\n" +
            "      \"price\": 10599.87479202,\n" +
            "      \"timestamp\": \"2019-07-20T10:57:47.016Z\",\n" +
            "      \"expiration\": \"2019-08-18T10:57:47.016Z\",\n" +
            "      \"matcherFee\": 0.011,\n" +
            "      \"signature\": \"3GVjGqddFa5Fvcs3yNCHLiGgT3ZSvXfnYHUuL9eCALoZg9KKXgBgqPXpoFGMEFRtvMCBrRBh4DmSwS8TYB1iiSvF\"\n" +
            "    },\n" +
            "    \"order2\": {\n" +
            "      \"id\": \"6Lma6NnoWa9yd73yg3bvV8jtGqFSQmR7p5bb55DjCM2J\",\n" +
            "      \"senderPublicKey\": \"6TNqGk9VFwpNoUq6ZgfHGLpbGEgn5sfheirJF96FqHBT\",\n" +
            "      \"matcherPublicKey\": \"E3UwaHCQCySghK3zwNB8EDHoc3b8uhzGPFz3gHmWon4W\",\n" +
            "      \"orderType\": \"sell\",\n" +
            "      \"sender\": \"3PATy5UyBpYDVa9bGUe14o77zarXztprXfU\",\n" +
            "      \"assetPair\": {\n" +
            "        \"amountAsset\": \"9SxLVHaEGTeEjRiAMnEw74YWWndQDRw8SZhknK9EYoUd\",\n" +
            "        \"priceAsset\": \"ARUnrACYathMJwq6gPvnb9kW2FHXTYLaVbsKwB2JC1DH\"\n" +
            "      },\n" +
            "      \"amount\": 0.00000489,\n" +
            "      \"price\": 10599.87479202,\n" +
            "      \"timestamp\": \"2019-07-20T10:57:46.775Z\",\n" +
            "      \"expiration\": \"2019-08-18T10:57:46.775Z\",\n" +
            "      \"matcherFee\": 0.011,\n" +
            "      \"signature\": \"5vxX4wwY4Q4r5KNpqPXE3qUG3tvEbFW4nLCXA1EEewnALk8qge4PtSPJGJzVrubcZMPVA5acddQkW97iA3wdMD8e\"\n" +
            "    }\n" +
            "  }\n" +
            "}";
}
