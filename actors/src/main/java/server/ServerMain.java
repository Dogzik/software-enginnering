package server;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import config.ConfigUtils;
import config.SearchConfig;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ServerMain {
    public static void main(String[] args) {
        File configFile = Paths.get("src/main/resources/search-engines.conf").toFile();
        Config rawConfig = ConfigFactory.parseFile(configFile);
        List<SearchConfig> searchEnginesConfigs = ConfigUtils.parseSearchEnginesConfigs(rawConfig);
        List<SimpleHttpSearchServer> servers = new ArrayList<>();
        for (SearchConfig engineConfig : searchEnginesConfigs) {
            try {
                SimpleHttpSearchServer newServer = new SimpleHttpSearchServer(engineConfig.host, engineConfig.port, engineConfig.name);
                servers.add(newServer);
            } catch (IOException e) {
                System.err.println(e.getMessage());
                return;
            }
        }
        try {
            servers.forEach(SimpleHttpSearchServer::start);
            System.out.println("Servers are ready");
            while (true) {
            }
        } finally {
            for (SimpleHttpSearchServer server : servers) {
                try {
                    server.close();
                } catch (Exception ex) {
                    System.err.println(ex.getMessage());
                }
            }
        }
    }
}
