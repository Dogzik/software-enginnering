package server;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class SimpleHttpSearchServer implements AutoCloseable {
    private final HttpServer server;

    public SimpleHttpSearchServer(String host, int port, String name) throws IOException {
        server = HttpServer.create();
        server.bind(new InetSocketAddress(host, port), 0);
        server.createContext("/", new SearchQueryHandler(name));
    }

    public void start() {
        server.start();
    }

    @Override
    public void close() throws Exception {
        server.stop(0);
    }
}
