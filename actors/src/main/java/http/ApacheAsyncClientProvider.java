package http;

import org.apache.http.impl.nio.client.HttpAsyncClients;

public class ApacheAsyncClientProvider implements AsyncHttpClientProvider {
    @Override
    public AsyncHttpClient getInstance() {
        return new ApacheAsyncHttpClient(HttpAsyncClients.createDefault());
    }
}
