package com.brightplan.recs.auditing;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Objects;
import org.springframework.stereotype.Component;

@Component("dateTimeService")
public class CurrentDateTimeService implements DateTimeService {

  @Override
  public ZonedDateTime getCurrentDateAndTime() {
    return ZonedDateTime.now(ZoneOffset.UTC);
  }

  @Override
  public boolean isAged(int ageInDays, Date fromDate) {
    if (Objects.isNull(fromDate) || ageInDays < 0) {
      return false;
    }
    LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
    LocalDateTime givenDatePlusAgeInDays = LocalDateTime
        .ofInstant(Instant.ofEpochMilli(fromDate.getTime()), ZoneOffset.UTC).plusDays(ageInDays);
    return givenDatePlusAgeInDays.isAfter(now) ? false : true;
  }
}

