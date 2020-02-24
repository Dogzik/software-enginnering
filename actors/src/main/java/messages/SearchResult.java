package messages;

import java.util.List;

public class SearchResult {
    public final String engineName;
    public final List<String> urls;

    public SearchResult(String engineName, List<String> urls) {
        this.engineName = engineName;
        this.urls = urls;
    }
}
