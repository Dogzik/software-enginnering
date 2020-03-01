package actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import config.SearchConfig;
import http.AsyncHttpClientProvider;
import messages.SearchQuery;
import messages.SearchResult;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class TestActor extends AbstractActor {
    private final String query;
    private final AsyncHttpClientProvider provider;
    private final SearchConfig config;
    private final CompletableFuture<List<String>> resultConsumer;

    public static Props props(String query, AsyncHttpClientProvider provider, SearchConfig config,
                              CompletableFuture<List<String>> resultConsumer) {
        return Props.create(TestActor.class, () -> new TestActor(query, provider, config, resultConsumer));
    }

    private TestActor(String query, AsyncHttpClientProvider provider, SearchConfig config,
                      CompletableFuture<List<String>> resultConsumer) {
        this.query = query;
        this.provider = provider;
        this.config = config;
        this.resultConsumer = resultConsumer;
    }

    @Override
    public Receive createReceive() {
        return new ReceiveBuilder()
                .matchEquals("start", s -> onStart())
                .match(SearchResult.class, this::onSearchResponse)
                .build();
    }

    private void onStart() {
        ActorRef searchActor = context().actorOf(SearchActor.props(provider), "TestSearch");
        searchActor.tell(new SearchQuery(query, config), self());
    }

    private void onSearchResponse(SearchResult result) {
        resultConsumer.complete(result.urls);
    }
}
