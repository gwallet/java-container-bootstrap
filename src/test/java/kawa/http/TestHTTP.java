package kawa.http;

import org.junit.jupiter.api.Test;

import java.util.SequencedCollection;
import java.util.SequencedMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;

class TestHTTP {

  @Test void should_parse_simple_query() throws Exception {
    // Given
    String queryString = "key=value";

    // When
    var query = HTTP.query(queryString);

    // Then
    assertThat(query)
      .hasEntrySatisfying("key", v -> assertThat(v).containsExactly("value"));
  }

  @Test void should_parse_sample_query() throws Exception {
    // Given
    String queryString = "key=value&foo=bar";

    // When
    var query = HTTP.query(queryString);

    // Then
    assertThat(query)
      .hasEntrySatisfying("key", v -> assertThat(v).containsExactly("value"))
      .hasEntrySatisfying("foo", v -> assertThat(v).containsExactly("bar"));
  }

  @Test void should_parse_array_value_in_query() throws Exception {
    // Given
    String queryString = "key=value1&key=value2";

    // When
    var query = HTTP.query(queryString);

    // Then
    assertThat(query)
      .hasEntrySatisfying("key", v -> assertThat(v).containsExactly("value1", "value2"));
  }

}
