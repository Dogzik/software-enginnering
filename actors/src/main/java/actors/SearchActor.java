package actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import http.AsyncHttpClient;
import http.AsyncHttpClientProvider;
import messages.SearchQuery;
import messages.SearchResult;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.concurrent.FutureCallback;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;

public class SearchActor extends AbstractActor {
    private interface SearchResponse {
    }

    private static class SearchError implements SearchResponse {
        private static final SearchError INSTANCE = new SearchError();
    }

    private static class SearchSuccess implements SearchResponse {
        public final List<String> urls;

        public SearchSuccess(List<String> urls) {
            this.urls = urls;
        }
    }

    private static class SearchCallback implements FutureCallback<String> {
        private final ActorRef self;

        private SearchCallback(ActorRef self) {
            this.self = self;
        }

        @Override
        public void completed(String result) {
            Gson gson = new Gson();
            try {
                List<String> response = gson.fromJson(result, new TypeToken<List<String>>() {
                }.getType());
                self.tell(new SearchSuccess(response), self);
            } catch (Exception e) {
                self.tell(SearchError.INSTANCE, self);
            }
        }

        @Override
        public void failed(Exception ex) {
            self.tell(SearchError.INSTANCE, self);
        }

        @Override
        public void cancelled() {
        }
    }

    private AsyncHttpClient client;
    private Future<HttpResponse> responseFuture;
    private String searchEngine;
    private ActorRef aggregator;

    static Props props(AsyncHttpClientProvider clientProvider) {
        return Props.create(SearchActor.class, () -> new SearchActor(clientProvider));
    }

    public SearchActor(AsyncHttpClientProvider provider) {
        this.client = provider.getInstance();
        this.client.start();
        this.aggregator = null;
        this.responseFuture = null;
    }

    @Override
    public Receive createReceive() {
        return new ReceiveBuilder()
                .match(SearchQuery.class, this::onSearchQuery)
                .match(SearchResponse.class, this::onSearchResponse)
                .build();
    }

    private void onSearchResponse(SearchResponse response) {
        responseFuture = null;
        if (response instanceof SearchError) {
            aggregator.tell(new SearchResult(searchEngine, Collections.emptyList()), getSelf());
        } else if (response instanceof SearchSuccess) {
            aggregator.tell(new SearchResult(searchEngine, ((SearchSuccess) response).urls), getSelf());
        }
        context().stop(self());
    }

    private void onSearchQuery(SearchQuery query) {
        String queryUrl = "http://" + query.config.host + ':' + query.config.port + "/search?query=" + query.text;
        searchEngine = query.config.name;
        aggregator = sender();
        try {
            responseFuture = client.run(new HttpGet(queryUrl), new SearchCallback(getContext().getSelf()));
        } catch (Exception ignored) {
            self().tell(SearchError.INSTANCE, self());
        }
    }

    @Override
    public void postStop() {
        if (responseFuture != null) {
            responseFuture.cancel(true);
        }
        try {
            client.close();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
