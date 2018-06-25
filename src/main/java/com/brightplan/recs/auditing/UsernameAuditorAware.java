package com.brightplan.recs.auditing;

import java.util.Optional;
import org.springframework.data.domain.AuditorAware;

public class UsernameAuditorAware implements AuditorAware<String> {

  @Override
  public Optional<String> getCurrentAuditor() {
    // TODO: Get it from the HttpServletRequest
    return Optional.of("service_user");
  }
}
