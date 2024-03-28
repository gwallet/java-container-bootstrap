package kawa.api.hello;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;

class TestHelloResource {

  HelloResource helloResource;

  @BeforeEach void setUp() {
    helloResource = new HelloResource();
  }

  @Test void should_greet_names() throws Exception {
    // Given
    final String[] names = {
      "Alice",
      "Bob",
    };

    // When
    String greetings = helloResource.greet(names);

    // Then
    assertThat(greetings)
      .isEqualTo(
        """
        Hello, Alice!
        Hello, Bob!
        """
      );
  }

}
