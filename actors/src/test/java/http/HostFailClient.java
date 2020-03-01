package http;

import com.google.gson.Gson;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.concurrent.FutureCallback;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;

public class HostFailClient implements AsyncHttpClient {
    private final String badHost;

    public HostFailClient(String badHost) {
        this.badHost = badHost;
    }

    @Override
    public void start() {
    }

    @Override
    public Future<HttpResponse> run(HttpUriRequest request, FutureCallback<String> callback) {
        String host = request.getURI().getHost();
        if (host.equals(badHost)) {
            callback.failed(new IllegalArgumentException());
        } else {
            Gson gson = new Gson();
            String requestString = request.getURI().getQuery().substring(6); // get rid of "query="
            List<String> ans = Collections.singletonList("Result for " + requestString + " from " + host);
            callback.completed(gson.toJson(ans));
        }
        return (Future<HttpResponse>) Mockito.mock(Future.class);
    }

    @Override
    public void close() {
    }
}
