package kawa.http;

import com.sun.net.httpserver.HttpServer;
import kawa.api.hello.HelloResource;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.SequencedCollection;
import java.util.SequencedMap;

public class HTTP {

  public static final String QUERY_KEY_VALUE_PAIR_SEPARATOR = "&";
  public static final String QUERY_KEY_VALUE_SEPARATOR      = "=";

  public static SequencedMap<String, SequencedCollection<String>> query(String queryString) {
    var query = new LinkedHashMap<String, SequencedCollection<String>>();
    Arrays.stream(queryString.split(QUERY_KEY_VALUE_PAIR_SEPARATOR))
          .forEach(keyEqualValue -> {
            String[] keyAndValue = keyEqualValue.split(QUERY_KEY_VALUE_SEPARATOR);
            String key = keyAndValue[0];
            String value = keyAndValue[1];
            query.compute(key, (__, values) -> {
              if (values == null) {
                return List.of(value);
              }
              else {
                var newValues = new ArrayList<>(values);
                newValues.add(value);
                return List.copyOf(newValues);
              }
            });
          });
    return query;
  }

  public static class Server {

    public static final int        DEFAULT_PORT = 8000;
    public static final int        RANDOM_PORT  = 0;
    private             HttpServer server;

    public int port() {
      return server.getAddress().getPort();
    }

    public void start(int port) {
      if (server != null) return; // already started

      try {
        server = HttpServer.create(new InetSocketAddress(port), 0);
      }
      catch (IOException cause) {
        throw new RuntimeException(cause);
      }
      server.createContext("/", new ResourceHandler(new HelloResource()));
      server.setExecutor(null); // creates a default executor
      server.start();
    }

    public void stop() {
      server.stop(0 /* seconds */);
      server = null;
    }

  }

}
