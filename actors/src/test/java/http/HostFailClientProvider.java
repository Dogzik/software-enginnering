package http;

public class HostFailClientProvider implements AsyncHttpClientProvider {
    private final String badHost;

    public HostFailClientProvider(String badHost) {
        this.badHost = badHost;
    }

    @Override
    public AsyncHttpClient getInstance() {
        return new HostFailClient(badHost);
    }
}
