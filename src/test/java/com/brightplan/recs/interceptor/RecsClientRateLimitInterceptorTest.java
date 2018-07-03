package com.brightplan.recs.interceptor;

import static com.brightplan.recs.interceptor.RecsClientRateLimitInterceptor.CLIENT_ID;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.core.Is.is;

import com.brightplan.recs.RecsApplication;
import com.brightplan.recs.config.RecsMvcConfig;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ActiveProfiles("localIntTest")
@SpringBootTest(classes = {RecsApplication.class,
    RecsMvcConfig.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RecsClientRateLimitInterceptorTest {

  @LocalServerPort
  private int port;

  private TestRestTemplate restTemplate = new TestRestTemplate();

  private String createURLWithPort(String uri) {
    return "http://localhost:" + port + uri;
  }

  @Test
  public void should_Return429ForSome_When_MoreThan2ClientsRequests()
      throws ExecutionException, InterruptedException {

    List<String> clients = Stream
        .iterate(1, n -> n + 1).limit(5).map(i -> {
          // This is to send multiple client requests for multiple times
          List<String> multipleClients = Lists
              .newArrayList();
          multipleClients.add("client1");
          multipleClients.add("client2");
          multipleClients.add("client3");
          multipleClients.add("client2");
          multipleClients.add("client3");
          multipleClients.add("client1");
          multipleClients.add("client2");
          multipleClients.add("client3");
          multipleClients.add("client2");
          multipleClients.add("client3");
          multipleClients.add("client1");
          multipleClients.add("client2");
          multipleClients.add("client3");
          multipleClients.add("client2");
          multipleClients.add("client1");
          multipleClients.add("client2");
          multipleClients.add("client3");
          multipleClients.add("client1");
          multipleClients.add("client2");
          multipleClients.add("client3");
          multipleClients.add("client1");
          multipleClients.add("client2");
          multipleClients.add("client3");
          multipleClients.add("client2");
          multipleClients.add("client3");
          multipleClients.add("client2");
          multipleClients.add("client3");
          multipleClients.add("client1");
          multipleClients.add("client2");
          multipleClients.add("client3");
          multipleClients.add("client2");
          multipleClients.add("client1");
          multipleClients.add("client2");
          multipleClients.add("client3");
          return multipleClients;
        }).flatMap(List::stream).collect(toList());

    long start = System.nanoTime();
    ExecutorService executor = Executors.newScheduledThreadPool(clients.size());
    List<CompletableFuture<ResponseEntity<String>>> futures =
        clients.stream()
            .map(t -> CompletableFuture.supplyAsync(() -> getApiCallClient(t), executor))
            .collect(Collectors.toList());

    List<ResponseEntity<String>> result =
        futures.stream()
            .map(CompletableFuture::join)
            .collect(Collectors.toList());

    long duration = (System.nanoTime() - start) / 1_000_000;
    System.out.printf("Processed %d tasks in %d millis\n", clients.size(), duration);
    System.out.println(result);
    executor.shutdown();

    // Count the number of requests returned atleast one has status code 409
    long count409 = result.stream()
        .filter(response -> response.getStatusCode().equals(HttpStatus.TOO_MANY_REQUESTS))
        .count();
    assertThat(count409, is(greaterThan(0l)));
  }

  /**
   * Calls api.
   */
  private ResponseEntity<String> getApiCallClient(
      String client) {
    //return CompletableFuture.supplyAsync(() -> {
    HttpHeaders headers = new HttpHeaders();
    headers.add(CLIENT_ID, client);
    HttpEntity<String> entity = new HttpEntity<>(null, headers);
    return restTemplate.exchange(
        createURLWithPort("/api/v1/predefined/portfolios"),
        HttpMethod.GET, entity, String.class);
  }

}
