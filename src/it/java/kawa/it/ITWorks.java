package kawa.it;

import kawa.Application;
import kawa.it.api.HelloApiClient;
import okhttp3.HttpUrl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
  classes = Application.class,
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
class ITWorks {

  @Autowired ServletWebServerApplicationContext webServerApplicationContext;
  HelloApiClient api;

  @BeforeEach void setUp() {
    int port = webServerApplicationContext.getWebServer().getPort();
    Retrofit retrofit = new Retrofit.Builder().baseUrl(HttpUrl.get("http://localhost:" + port))
                                              .addConverterFactory(ScalarsConverterFactory.create())
                                              .build();
    api = retrofit.create(HelloApiClient.class);
  }

  @Test void should_greet_names_in_query_param() throws Exception {
    //  When
    Response<String> response = api.greet("World", "Monde", "Welt").execute();

    //  Then
    assertThat(response.code())
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
