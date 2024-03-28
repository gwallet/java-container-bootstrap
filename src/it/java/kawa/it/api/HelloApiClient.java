package kawa.it.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface HelloApiClient {

  @GET("/hello")
  Call<String> greet(@Query("name") String... names);

}
