package kawa.junit;

import kawa.http.HTTP;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.net.URI;
import java.util.Arrays;

import static kawa.Application.sneakyThrows;

public class HttpServerExtension implements BeforeAllCallback, AfterAllCallback, TestInstancePostProcessor {

  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.FIELD)
  public @interface ServerURI { }

  private HTTP.Server httpServer;

  @Override public void beforeAll(ExtensionContext context) throws Exception {
    httpServer = new HTTP.Server();
    httpServer.start(HTTP.Server.RANDOM_PORT);
  }

  @Override public void postProcessTestInstance(Object testInstance, ExtensionContext context) throws Exception {
    URI uri = URI.create("http://localhost:" + httpServer.port());
    Arrays.stream(testInstance.getClass().getDeclaredFields())
          .filter(field -> field.isAnnotationPresent(ServerURI.class))
          .findFirst()
          .ifPresent(field -> sneakyThrows(() -> {
            field.setAccessible(true);
            field.set(testInstance, uri);
          }));
  }

  @Override public void afterAll(ExtensionContext context) throws Exception {
    httpServer.stop();
    httpServer = null;
  }

}
