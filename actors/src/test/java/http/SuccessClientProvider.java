package http;

public class SuccessClientProvider implements AsyncHttpClientProvider {
    @Override
    public AsyncHttpClient getInstance() {
        return new SuccessClient();
    }
}
