package com.brightplan.recs.config;

import static com.google.common.base.Predicates.or;
import static springfox.documentation.builders.PathSelectors.ant;

import com.brightplan.recs.interceptor.RecsClientRateLimitInterceptor;
import com.google.common.base.Predicate;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class RecsMvcConfig extends WebMvcConfigurationSupport {

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

  @Bean
  public Docket api() throws IOException, XmlPullParserException {
    return new Docket(DocumentationType.SWAGGER_2)
        .groupName("recommend")
        .select()
        .apis(RequestHandlerSelectors.any())
        .paths(recommendPaths())
        .build()
        .apiInfo(apiInfo());

  }

  private Predicate<String> recommendPaths() {
    return or(
        ant("/api/v1/**"),
        ant("/api/v2/**")
    );
  }

  private ApiInfo apiInfo() throws IOException, XmlPullParserException {
    MavenXpp3Reader reader = new MavenXpp3Reader();
    Model model = reader.read(new FileReader("pom.xml"));
    ApiInfo apiInfo = new ApiInfo(
        "Recommend API",
        "API for Recommend Portfolio application",
        model.getVersion(),
        "Terms of service",
        new Contact("Sarath Annareddy", "http://www.brightplan.com", "askasckr@gmail.com"),
        "Apache License Version 2.0",
        "https://www.apache.org/licenses/LICENSE-2.0", Collections.emptyList());
    return apiInfo;
  }

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("swagger-ui.html")
        .addResourceLocations("classpath:/META-INF/resources/");

    registry.addResourceHandler("/webjars/**")
        .addResourceLocations("classpath:/META-INF/resources/webjars/");
  }


}
