package kawa.api.hello;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.stream.Collectors;

@RestController
public class HelloResource {

  @GetMapping("/hello")
  public String greet(@RequestParam("name") String[] names) {
    return Arrays.stream(names)
                 .map("Hello, %s!"::formatted)
                 .collect(Collectors.joining("\n", "", "\n"));
  }

}
