package com.brightplan.recs.auditing;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class CurrentDateTimeServiceTest {

  private CurrentDateTimeService currentDateTimeService;

  @Before
  public void setup() {
    currentDateTimeService = new CurrentDateTimeService();
  }

  @Test
  public void shouldReturnTrueIfAgedDateIsInPast() {
    Date twoDaysAgo = Date
        .from(LocalDateTime.now(ZoneOffset.UTC).minusDays(2).toInstant(ZoneOffset.UTC));
    assertThat(currentDateTimeService.isAged(1, twoDaysAgo), is(true));
  }

  @Test
  public void shouldReturnFalseIfAgedDateIsInFuture() {
    Date tomorrow = Date
        .from(LocalDateTime.now(ZoneOffset.UTC).plusDays(2).toInstant(ZoneOffset.UTC));
    assertThat(currentDateTimeService.isAged(2, tomorrow), is(false));
  }

  @Test
  public void shouldReturnFalseIfAgedDateIsToday() {
    Date today = Date.from(LocalDateTime.now(ZoneOffset.UTC).toInstant(ZoneOffset.UTC));
    assertThat(currentDateTimeService.isAged(1, today), is(false));
  }
}
