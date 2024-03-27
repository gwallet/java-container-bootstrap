package kawa.it;

import kawa.junit.HttpServerExtension;
import kawa.junit.HttpServerExtension.ServerURI;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(HttpServerExtension.class)
class ITWorks {

  @ServerURI URI uri;
  HttpClient api;

  @BeforeEach void setUp() {
    api = HttpClient.newBuilder().build();
  }

  @AfterEach void tearDown() {
    api.close();
  }

  @Test void should_greet_names_in_query_param() throws Exception {
    //  Given
    HttpRequest request = HttpRequest.newBuilder()
                                     .method("GET", BodyPublishers.noBody())
                                     .uri(uri.resolve("/hello?name=World&name=Monde&name=Welt"))
                                     .build();

    //  When
    HttpResponse<String> response = api.send(request, BodyHandlers.ofString());

    //  Then
    assertThat(response.statusCode())
      .isEqualTo(200);
    assertThat(response.body())
      .isEqualTo(
        """
        Hello, World!
        Hello, Monde!
        Hello, Welt!
        """
      );
  }

}
