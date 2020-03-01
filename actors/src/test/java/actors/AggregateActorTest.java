package actors;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import config.AggregatorConfig;
import config.SearchConfig;
import http.AsyncHttpClientProvider;
import http.FailClientProvider;
import http.HostFailClientProvider;
import http.SuccessClientProvider;
import messages.AggregateSearchQuery;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import scala.concurrent.duration.Duration;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class AggregateActorTest {
    private ActorSystem system;

    @Before
    public void initSystem() {
        system = ActorSystem.create("TestSystem");
    }

    @After
    public void stopSystem() {
        system.terminate();
        system = null;
    }

    private Map<String, List<String>> startTest(List<String> names, String queryStr, AsyncHttpClientProvider provider) {
        CompletableFuture<Map<String, List<String>>> result = new CompletableFuture<>();
        Duration duration = Duration.create(2, TimeUnit.SECONDS);
        AggregatorConfig aggregatorConfig = new AggregatorConfig(duration);
        ActorRef aggregator = system.actorOf(AggregateActor.props(result, aggregatorConfig, provider));
        List<SearchConfig> configs = names
                .stream()
                .map(name -> new SearchConfig(name, name + "Host", 1488))
                .collect(Collectors.toList());
        AggregateSearchQuery query = new AggregateSearchQuery(queryStr, configs);
        aggregator.tell(query, ActorRef.noSender());
        try {
            return result.get(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            return null;
        }
    }

    @Test
    public void successTest() {
        List<String> names = Arrays.asList("a", "b", "c");
        String queryStr = "query";
        AsyncHttpClientProvider provider = new SuccessClientProvider();
        Map<String, List<String>> real = startTest(names, queryStr, provider);
        Map<String, List<String>> expected = names
                .stream()
                .collect(Collectors.toMap(
                        name -> name,
                        name -> Collections.singletonList("Result for " + queryStr + " from " + name + "Host")
                ));
        Assert.assertEquals(expected, real);
    }

    @Test
    public void partialFailTest() {
        List<String> names = Arrays.asList("a", "b", "c");
        String badName = "a";
        String queryStr = "query";
        AsyncHttpClientProvider provider = new HostFailClientProvider("a");
        Map<String, List<String>> real = startTest(names, queryStr, provider);
        Map<String, List<String>> expected = names
                .stream()
                .collect(Collectors.toMap(
                        name -> name,
                        name -> {
                            if (name.equals(badName + "Host")) {
                                return Collections.emptyList();
                            } else {
                                return Collections.singletonList("Result for " + queryStr + " from " + name + "Host");
                            }
                        }
                ));
        Assert.assertEquals(expected, real);
    }

    @Test
    public void allFailTest() {
        List<String> names = Arrays.asList("a", "b", "c");
        String queryStr = "query";
        AsyncHttpClientProvider provider = new FailClientProvider();
        Map<String, List<String>> real = startTest(names, queryStr, provider);
        Map<String, List<String>> expected = names
                .stream()
                .collect(Collectors.toMap(
                        name -> name,
                        name -> Collections.emptyList()
                ));
        Assert.assertEquals(expected, real);
    }
}
