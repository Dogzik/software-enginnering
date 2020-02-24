package actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.ReceiveTimeout;
import akka.japi.pf.ReceiveBuilder;
import config.AggregatorConfig;
import config.SearchConfig;
import http.AsyncHttpClientProvider;
import messages.AggregateSearchQuery;
import messages.SearchQuery;
import messages.SearchResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class AggregateActor extends AbstractActor {
    private final CompletableFuture<Map<String, List<String>>> resultConsumer;
    private final AggregatorConfig config;
    private final AsyncHttpClientProvider clientProvider;
    private int awaitingAnswers;
    private final Map<String, List<String>> currentResult;

    public static Props props(CompletableFuture<Map<String, List<String>>> resultConsumer, AggregatorConfig config,
                              AsyncHttpClientProvider clientProvider) {
        return Props.create(AggregateActor.class, () -> new AggregateActor(resultConsumer, config, clientProvider));
    }

    private AggregateActor(CompletableFuture<Map<String, List<String>>> resultConsumer, AggregatorConfig config,
                           AsyncHttpClientProvider clientProvider) {
        this.resultConsumer = resultConsumer;
        this.config = config;
        this.clientProvider = clientProvider;
        this.awaitingAnswers = 0;
        this.currentResult = new HashMap<>();
    }

    @Override
    public Receive createReceive() {
        return new ReceiveBuilder()
                .match(AggregateSearchQuery.class, this::onAggregateSearchQuery)
                .match(SearchResult.class, this::onSearchResult)
                .match(ReceiveTimeout.class, this::onReceiveTimeout)
                .build();
    }

    private void onAggregateSearchQuery(AggregateSearchQuery query) {
        for (SearchConfig engine : query.searchEngines) {
            ActorRef searchActor = context().actorOf(SearchActor.props(clientProvider), engine.name + "_search");
            searchActor.tell(new SearchQuery(query.text, engine), self());
        }
        awaitingAnswers = query.searchEngines.size();
        context().setReceiveTimeout(config.timeout);
    }

    private void onReceiveTimeout(ReceiveTimeout msg) {
        getContext().cancelReceiveTimeout();
        context().stop(self());
    }

    private void onSearchResult(SearchResult result) {
        currentResult.put(result.engineName, result.urls);
        if (currentResult.size() == awaitingAnswers) {
            getContext().cancelReceiveTimeout();
            context().stop(self());
        }
    }

    @Override
    public void postStop() {
        resultConsumer.complete(currentResult);
    }
}