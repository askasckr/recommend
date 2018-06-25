package com.brightplan.recs.interceptor;

import static java.util.Objects.nonNull;

import com.brightplan.recs.filter.RecsHttpRequestWrapperFilter;
import com.google.common.util.concurrent.RateLimiter;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import javax.annotation.PreDestroy;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class RecsClientRateLimitInterceptor implements HandlerInterceptor {

  public static final String CLIENT_ID = "Client-Id";
  private static final Logger log = LoggerFactory.getLogger(RecsHttpRequestWrapperFilter.class);
  @Value("${rate.limit.enabled:true}")
  private boolean enabled;

  @Value("${rate.limit.requests.perSecond:5}")
  private int requestsLimit;

  @Value("${rate.limit.warmup.millis:3}")
  private int warmupMillis;

  @Value("${rate.limit.clients.limit:2}")
  private int clientsLimit;

  private Map<String, Optional<RateLimiter>> limiters = new ConcurrentHashMap<>();
  private Map<String, String> activeClients = new ConcurrentHashMap<>();

  private RecsClientRateLimitInterceptor() {
    this(builder());
  }

  private RecsClientRateLimitInterceptor(Builder builder) {
    this.enabled = builder.enabled;
    this.requestsLimit = builder.requestsLimit;
    this.warmupMillis = builder.warmupMillis;
    this.clientsLimit = builder.clientsLimit;
  }

  public static Builder builder() {
    return new Builder();
  }

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {

    if (!enabled) {
      return true;
    }

    String clientId = request.getHeader(CLIENT_ID);
    // let non-API requests not pass
    if (clientId == null) {
      return false;
    }

    log.debug("Client Id {}", activeClients.get(clientId));
    // TODO: Confirm if existing client can get served for multiple requests at the same time
    // if(nonNull(activeClients.get(clientId)) || activeClients.size() > 2) {
    if (activeClients.size() > clientsLimit) {
      return false;
    } else {
      activeClients.putIfAbsent(clientId, clientId);
    }
    Optional<RateLimiter> rateLimiter = getRateLimiter(clientId);
    boolean allowRequest = false;
    if (rateLimiter.isPresent() && rateLimiter.get().tryAcquire()) {
      allowRequest = true;
    } else {
      response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
      response.getWriter().write("Server is busy");
    }
    response.addHeader("X-RateLimit-Limit", String.valueOf(requestsLimit));
    response.addHeader("X-RateLimit-Clients", "2");

    return allowRequest;
  }

  private Optional<RateLimiter> getRateLimiter(String clientId) {
    return limiters.computeIfAbsent(clientId, this::createRateLimiter);
  }

  public Optional<RateLimiter> createRateLimiter(String clientId) {
    return Optional.of(RateLimiter.create(requestsLimit, warmupMillis, TimeUnit.MILLISECONDS));
  }

  @Override
  public void postHandle(HttpServletRequest request, HttpServletResponse response,
      Object object, ModelAndView model)
      throws Exception {

  }

  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
      Object object, Exception arg3)
      throws Exception {
    String clientId = request.getHeader(CLIENT_ID);
    if (nonNull(clientId)) {
      activeClients.remove(clientId);
    }
  }

  @PreDestroy
  public void destroy() {
    // loop and finalize all limiters, if required
  }

  //Builder Class with default values
  public static class Builder {

    // required parameters
    private boolean enabled = true;

    private int requestsLimit = 5;

    private int warmupMillis = 3;

    private int clientsLimit = 2;

    public Builder enabled(boolean enabled) {
      this.enabled = enabled;
      return this;
    }

    public Builder requestsLimit(int requestsLimit) {
      this.requestsLimit = requestsLimit;
      return this;
    }

    public Builder warmupMillis(int warmupMillis) {
      this.warmupMillis = warmupMillis;
      return this;
    }

    public Builder clientsLimit(int clientsLimit) {
      this.clientsLimit = clientsLimit;
      return this;
    }

    public RecsClientRateLimitInterceptor build() {
      return new RecsClientRateLimitInterceptor(this);
    }

  }

}