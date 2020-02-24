package config;

import com.typesafe.config.Config;

public class SearchConfig {
    public final String name;
    public final String host;
    public final int port;

    public SearchConfig(String name, String host, int port) {
        this.name = name;
        this.host = host;
        this.port = port;
    }

    public SearchConfig(String name, Config config) {
        this(name, config.getString("host"), config.getInt("port"));
    }
}
