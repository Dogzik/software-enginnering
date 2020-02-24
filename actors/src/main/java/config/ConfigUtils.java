package config;

import com.typesafe.config.Config;

import java.util.List;
import java.util.stream.Collectors;

public class ConfigUtils {
    public static List<SearchConfig> parseSearchEnginesConfigs(Config enginesConfig) {
        return enginesConfig.getStringList("engineList")
                .stream()
                .map(name -> new SearchConfig(name, enginesConfig.getConfig(name)))
                .collect(Collectors.toList());

    }
}
