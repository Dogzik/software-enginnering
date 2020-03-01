package actors;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import config.SearchConfig;
import http.AsyncHttpClientProvider;
import http.FailClientProvider;
import http.SuccessClientProvider;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class SearchActorTest {
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

    private List<String> startTest(AsyncHttpClientProvider provider, String query) {
        SearchConfig config = new SearchConfig("name", "host", 1337);
        CompletableFuture<List<String>> result = new CompletableFuture<>();
        ActorRef actor = system.actorOf(TestActor.props(query, provider, config, result));
        actor.tell("start", ActorRef.noSender());
        try {
            return result.get(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            return null;
        }
    }

    @Test
    public void successTest() {
        String query = "test";
        AsyncHttpClientProvider provider = new SuccessClientProvider();
        List<String> real = startTest(provider, query);
        List<String> expected = Collections.singletonList("Result for " + query + " from host");
        Assert.assertEquals(expected, real);
    }

    @Test
    public void failTest() {
        String query = "test";
        AsyncHttpClientProvider provider = new FailClientProvider();
        List<String> real = startTest(provider, query);
        List<String> expected = Collections.emptyList();
        Assert.assertEquals(expected, real);
    }
}
