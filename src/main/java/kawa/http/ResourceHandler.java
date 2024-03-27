package kawa.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.SequencedCollection;
import java.util.SequencedMap;
import java.util.stream.Collectors;

import static kawa.Application.sneakyThrows;

public class ResourceHandler implements HttpHandler {

  private final Object resource;

  public ResourceHandler(Object resource) { this.resource = resource; }

  @Override public void handle(HttpExchange exchange) throws IOException {
    URI uri = exchange.getRequestURI();
    String path = uri.getPath();
    SequencedMap<String, SequencedCollection<String>> query = HTTP.query(uri.getQuery());
    try {
      Arrays.stream(resource.getClass().getDeclaredMethods())
            .filter(method -> method.isAnnotationPresent(GET.class))
            .filter(method -> method.getAnnotation(GET.class).value().equals(path))
            .findFirst()
            .ifPresentOrElse(
              method -> invoke(exchange, method, query),
              () -> sendError(exchange, 404)
            );
    }
    catch (RuntimeException rootCause) {
      String response = render(rootCause);
      exchange.sendResponseHeaders(500, response.length());
      try (var responseBody = exchange.getResponseBody()) {
        responseBody.write(response.getBytes(StandardCharsets.UTF_8));
      }
    }
  }

  private String render(RuntimeException exception) {
    return Arrays.stream(exception.getStackTrace())
                 .map("\tat %s"::formatted)
                 .collect(Collectors.joining("%n", "%s%n".formatted(exception.toString()), ""));
  }

  private void invoke(HttpExchange exchange, Method method, SequencedMap<String, SequencedCollection<String>> query) {
    sneakyThrows(() -> _invoke(exchange, method, query));
  }

  private void _invoke(HttpExchange exchange,
                       Method method,
                       SequencedMap<String, SequencedCollection<String>> query) throws Exception {
    Object[] args = Arrays.stream(method.getParameters())
                          .filter(parameter -> parameter.isAnnotationPresent(RequestParam.class))
                          .map(parameter -> {
                            RequestParam metaData = parameter.getAnnotation(RequestParam.class);
                            String queryParamName = metaData.value().isBlank()
                              ? parameter.getName()
                              : metaData.value();

                            SequencedCollection<String> queryParamValues = query.get(queryParamName);
                            if (queryParamValues != null) {
                              if (parameter.getType().isArray()) {
                                return queryParamValues.toArray(String[]::new); // FIXME handle here `.fromString(…)` type mapping
                              }
                              else {
                                return queryParamValues.getFirst();
                              }
                            }
                            else {
                              return null;
                            }
                          })
                          .toArray();
    Object result = method.invoke(resource, args);
    String response = result.toString(); // Handle here the content-type: JSON, XML, HTML,
    exchange.sendResponseHeaders(200, response.length());
    try (var responseBody = exchange.getResponseBody()) {
      responseBody.write(response.getBytes(StandardCharsets.UTF_8));
    }
  }

  private void sendError(HttpExchange exchange, int httpStatusCode) {
    sneakyThrows(() -> exchange.sendResponseHeaders(httpStatusCode, 0));
  }

}
