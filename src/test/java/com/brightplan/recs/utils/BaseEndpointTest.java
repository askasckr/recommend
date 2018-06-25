package com.brightplan.recs.utils;

import static org.junit.Assert.fail;
import static org.springframework.http.MediaType.ALL;
import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM;
import static org.springframework.http.MediaType.APPLICATION_PDF;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import com.brightplan.recs.errorhandler.RecsExceptionHandler;
import com.brightplan.recs.interceptor.RecsClientRateLimitInterceptor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.google.common.collect.ImmutableList;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.junit.Before;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.annotation.ExceptionHandlerMethodResolver;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod;

public class BaseEndpointTest<T> {

  protected final ObjectMapper objectMapper = new ObjectMapper();
  protected T endpoint;
  protected MockMvc mockMvc;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
    for (Field field : this.getClass().getDeclaredFields()) {
      if (field.isAnnotationPresent(InjectMocks.class)) {
        try {
          field.setAccessible(true);
          endpoint = (T) (field.get(this));
        } catch (IllegalAccessException e) {
          fail("Could not access field");
        }
        break;
      }
    }

    if (endpoint == null) {
      fail(
          "Cannot initialize mock endpoint class - make sure to declare a public field for the endpoint with the @InjectMocks annotation");
    }

    MappingJackson2HttpMessageConverter jsonMessageConverter = new MappingJackson2HttpMessageConverter();
    objectMapper.registerModule(new JodaModule());
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    jsonMessageConverter.setObjectMapper(objectMapper);

    ResourceHttpMessageConverter resourceMessageConverter = new ResourceHttpMessageConverter();
    resourceMessageConverter
        .setSupportedMediaTypes(ImmutableList.of(ALL, APPLICATION_OCTET_STREAM, APPLICATION_PDF));

    mockMvc = standaloneSetup(endpoint)
        .setMessageConverters(jsonMessageConverter, resourceMessageConverter,
            new StringHttpMessageConverter())
        .setHandlerExceptionResolvers(createExceptionResolver())
        .addInterceptors(RecsClientRateLimitInterceptor.builder().build())
        .setValidator(new CustomLocalValidatorFactoryBean())
        .build();
  }

  protected ExceptionHandlerExceptionResolver createExceptionResolver() {
    ExceptionHandlerExceptionResolver exceptionResolver = new ExceptionHandlerExceptionResolver() {
      @Override
      protected ServletInvocableHandlerMethod getExceptionHandlerMethod(
          HandlerMethod handlerMethod, Exception exception) {
        Method method = new ExceptionHandlerMethodResolver(RecsExceptionHandler.class)
            .resolveMethod(exception);
        if (method != null) {
          return new ServletInvocableHandlerMethod(new RecsExceptionHandler(), method);
        }
        return null;
      }
    };

    exceptionResolver.afterPropertiesSet();
    return exceptionResolver;
  }

  protected String asJson(Object request) throws JsonProcessingException {
    return objectMapper.writeValueAsString(request);
  }


}
