package http;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.concurrent.FutureCallback;
import org.mockito.Mockito;

import java.util.concurrent.Future;

public class FailClient implements AsyncHttpClient {
    @Override
    public void start() {
    }

    @Override
    public Future<HttpResponse> run(HttpUriRequest request, FutureCallback<String> callback) {
        callback.failed(new IllegalArgumentException());
        return (Future<HttpResponse>) Mockito.mock(Future.class);
    }

    @Override
    public void close() {
    }
}
