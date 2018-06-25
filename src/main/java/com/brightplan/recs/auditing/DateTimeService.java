package com.brightplan.recs.auditing;

import java.time.ZonedDateTime;
import java.util.Date;

public interface DateTimeService {

  ZonedDateTime getCurrentDateAndTime();

  boolean isAged(int ageInDays, Date fromDate);
}
