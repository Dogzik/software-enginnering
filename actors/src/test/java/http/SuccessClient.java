package http;

import com.google.gson.Gson;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.concurrent.FutureCallback;
import org.mockito.Mockito;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.Future;

public class SuccessClient implements AsyncHttpClient {
    @Override
    public void start() {
    }

    @Override
    public Future<HttpResponse> run(HttpUriRequest request, FutureCallback<String> callback) {
        Gson gson = new Gson();
        String requestString = request.getURI().getQuery().substring(6); // get rid of "query="
        callback.completed(gson.toJson(Collections.singletonList("Result for " + requestString)));
        return (Future<HttpResponse>) Mockito.mock(Future.class);
    }

    @Override
    public void close() {
    }
}
