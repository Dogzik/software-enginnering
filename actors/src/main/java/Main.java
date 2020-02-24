import actors.AggregateActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import config.AggregatorConfig;
import config.SearchConfig;
import http.ApacheAsyncClientProvider;
import http.AsyncHttpClientProvider;
import messages.AggregateSearchQuery;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Main {
    public static void main(String[] args) {
        Config appConfig = ConfigFactory.parseFile(Paths.get("src/main/resources/application.conf").toFile());
        AggregatorConfig aggregatorConfig = new AggregatorConfig(appConfig.getConfig("aggregator"));
        Config searchEnginesConfig = appConfig.getConfig("searchEngines");
        AsyncHttpClientProvider clientProvider = new ApacheAsyncClientProvider();
        List<String> searchEngines = searchEnginesConfig.getStringList("engineList");
        List<SearchConfig> searchConfigs = new ArrayList<>();
        for (String engineName : searchEngines) {
            searchConfigs.add(new SearchConfig(engineName, searchEnginesConfig.getConfig(engineName)));
        }
        ActorSystem system = ActorSystem.create("Aggregate system");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            while (true) {
                String query = reader.readLine();
                CompletableFuture<Map<String, List<String>>> result = new CompletableFuture<>();
                ActorRef aggregator = system.actorOf(AggregateActor.props(result, aggregatorConfig, clientProvider));
                aggregator.tell(new AggregateSearchQuery(query, searchConfigs), ActorRef.noSender());
                Map<String, List<String>> readyResult = result.get();
                System.out.println(readyResult);
            }
        } catch (IOException | InterruptedException | ExecutionException e) {
            System.err.println(e.getMessage());
        } finally {
            system.terminate();
        }
    }
}
