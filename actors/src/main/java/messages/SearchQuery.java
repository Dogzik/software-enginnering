package messages;

import config.SearchConfig;

public class SearchQuery {
    public final String text;
    public final SearchConfig config;

    public SearchQuery(String text, SearchConfig config) {
        this.text = text;
        this.config = config;
    }
}
