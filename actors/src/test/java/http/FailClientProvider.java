package http;

public class FailClientProvider implements AsyncHttpClientProvider {
    @Override
    public AsyncHttpClient getInstance() {
        return new FailClient();
    }
}
