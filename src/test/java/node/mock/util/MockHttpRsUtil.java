package node.mock.util;

import com.wavesplatform.wavesj.Node;
import com.wavesplatform.wavesj.Profile;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.mockito.Mockito;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.when;

public class MockHttpRsUtil {

    public static void mockTransactionInfoRs(Node nodeMock, String txId, String rsFilePath) throws IOException {
        when(
                nodeMock.client().execute(
                        argThat(rq ->
                                rq != null && rq.getURI().equals(nodeMock.uri().resolve("/transactions/info/" + txId))
                        )
                )
        ).thenReturn(createBasicRs(rsFilePath));
    }

    public static void mockGetBlockRs(Node nodeMock, int height, String rsFilePath) throws IOException {
        when(
                nodeMock.client().execute(
                        argThat(rq ->
                                rq != null && rq.getURI().equals(nodeMock.uri().resolve("/blocks/at/" + height))
                        )
                )
        ).thenReturn(createBasicRs(rsFilePath));
    }

    public static HttpClient mockHttpClient() throws IOException {
        HttpClient mockHttpClient = Mockito.mock(HttpClient.class);
        when(
                mockHttpClient.execute(
                        argThat(rq ->
                                rq != null && rq.getURI().equals(Profile.STAGENET.uri().resolve("/addresses"))
                        )
                )
        ).thenReturn(createBasicRs("src/test/resources/stub/addresses.json"));
        return mockHttpClient;
    }

    private static HttpResponse createBasicRs(String rsFilePath) throws FileNotFoundException {
        BasicStatusLine ok = new BasicStatusLine(HttpVersion.HTTP_1_1, 200, "ok");
        BasicHttpEntity basicHttpEntity = new BasicHttpEntity();
        BasicHttpResponse basicHttpResponse = new BasicHttpResponse(ok);
        basicHttpEntity.setContent(new FileInputStream(rsFilePath));
        basicHttpResponse.setEntity(basicHttpEntity);
        return basicHttpResponse;
    }
}
