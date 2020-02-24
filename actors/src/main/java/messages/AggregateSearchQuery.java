package messages;

import config.SearchConfig;

import java.util.List;

public class AggregateSearchQuery {
    public final String text;
    public final List<SearchConfig> searchEngines;

    public AggregateSearchQuery(String text, List<SearchConfig> searchEngines) {
        this.text = text;
        this.searchEngines = searchEngines;
    }
}
