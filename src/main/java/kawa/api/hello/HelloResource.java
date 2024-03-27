package kawa.api.hello;

import kawa.http.GET;
import kawa.http.RequestParam;

import java.util.Arrays;
import java.util.stream.Collectors;

public class HelloResource {

  @GET("/hello")
  public String greet(@RequestParam("name") String[] names) {
    return Arrays.stream(names)
                 .map("Hello, %s!"::formatted)
                 .collect(Collectors.joining("\n", "", "\n"));
  }

}
