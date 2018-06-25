package com.brightplan.recs;

import static org.springframework.util.ObjectUtils.isEmpty;

import com.brightplan.recs.filter.RecsHttpRequestWrapperFilter;
import com.brightplan.recs.seeddata.SeedDataService;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class RecsApplication implements CommandLineRunner {

  @Autowired
  private SeedDataService seedDataService;

  @Autowired
  private Environment env;

  public static void main(String[] args) {
    SpringApplication.run(RecsApplication.class, args);
  }

  @Bean
  public FilterRegistrationBean<RecsHttpRequestWrapperFilter> wrapperFilterRegistrationBean() {
    FilterRegistrationBean<RecsHttpRequestWrapperFilter> registrationBean = new FilterRegistrationBean<>();
    registrationBean.setFilter(wrapperFilter());
    registrationBean.setOrder(1);
    registrationBean.addUrlPatterns("/*");
    registrationBean.setAsyncSupported(true);
    return registrationBean;
  }

  @Bean
  public RecsHttpRequestWrapperFilter wrapperFilter() {
    return new RecsHttpRequestWrapperFilter();
  }

  public void run(String... args) throws Exception {
    List<String> activeProfiles = Arrays.asList(env.getActiveProfiles());
    if (isEmpty(activeProfiles) || activeProfiles.contains("localIntTest") || activeProfiles.contains("local")) {
      // Cleanup all data
      seedDataService.cleanUp();
      // Save the seed data for Investment Risk and PredefinedPortfolios
      seedDataService.populateDBWithInvestmentRiskCategoryPercentSeed();
    }
  }


}
