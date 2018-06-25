package com.brightplan.recs.config;

import com.brightplan.recs.interceptor.RecsClientRateLimitInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class RecsMvcConfig implements WebMvcConfigurer {

  @Autowired
  private RecsClientRateLimitInterceptor recsClientRateLimitInterceptor;

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(recsClientRateLimitInterceptor);
  }

//  @Override
//  public void addCorsMappings(CorsRegistry registry) {
//
//    registry.addMapping("/**")
//        .allowedOrigins(
//            "http://localhost:8080", "http://brightplan.com")
//        .allowedMethods("GET", "POST", "PUT", "DELETE", "HEAD")
//        .allowCredentials(true)
//    ;
//  }

}
