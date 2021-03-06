package com.brightplan.recs.filter;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RecsHttpRequestWrapperFilter implements Filter {

  private static final Logger log = LoggerFactory.getLogger(RecsHttpRequestWrapperFilter.class);

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    RecsHttpRequestWrapper requestWrapper = new RecsHttpRequestWrapper(
        (HttpServletRequest) request);
    chain.doFilter(requestWrapper, response);
  }

  @Override
  public void destroy() {
  }

}
