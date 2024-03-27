package kawa;

import kawa.http.HTTP;

import java.util.concurrent.Callable;

public class Application {

  public static void main(String[] args) {
    HTTP.Server server = new HTTP.Server();
    server.start(serverPort());
    Runtime.getRuntime().addShutdownHook(new Thread(server::stop));
    System.out.println("Server listening on port " + server.port());
  }

  private static int serverPort() {
    String serverPort = System.getenv("SERVER_PORT");
    return serverPort == null || serverPort.isBlank()
      ? HTTP.Server.DEFAULT_PORT
      : Integer.parseInt(serverPort);
  }

  @FunctionalInterface
  public interface DangerousRunnable {

    void run() throws Exception;

  }

  public static void sneakyThrows(DangerousRunnable block) {
    sneakyThrows((Callable<Void>) () -> {
      block.run();
      return null;
    });
  }

  public static <V> V sneakyThrows(Callable<V> block) {
    try {
      return block.call();
    }
    catch (Throwable rootCause) {
      rootCause.printStackTrace(System.err);
      throw new RuntimeException(rootCause);
    }
  }

}
