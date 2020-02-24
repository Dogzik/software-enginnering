package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SearchQueryHandler implements HttpHandler {
    private final String engineName;

    public SearchQueryHandler(String engineName) {
        this.engineName = engineName;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String query = httpExchange.getRequestURI().getQuery();
        String goodPattern = "query=";
        if (!query.startsWith(goodPattern)) {
            sendError(httpExchange);
        } else {
            String queryStr = query.substring(goodPattern.length());
            if (queryStr.equals("slow")) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ignored) {
                }
            } else if (queryStr.equals("bad")) {
                sendError(httpExchange);
            }
            Gson gson = new Gson();
            List<String> response = IntStream.range(1, 6)
                    .mapToObj(i -> "host" + i + ".ru/info_about_" + queryStr + "/from_" + engineName)
                    .collect(Collectors.toList());
            byte[] answer = gson.toJson(response).getBytes();
            httpExchange.sendResponseHeaders(200, answer.length);
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(answer);
            }
        }
    }

    private void sendError(HttpExchange httpExchange) throws IOException {
        byte[] errorStr = "Bad query".getBytes();
        httpExchange.sendResponseHeaders(400, errorStr.length);
        try (OutputStream os = httpExchange.getResponseBody()) {
            os.write(errorStr);
        }
    }
}
