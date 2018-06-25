package com.brightplan.recs.auditing;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.time.ZonedDateTime;
import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AuditingDateTimeProviderTest {

  @Mock
  private DateTimeService dateTimeService;

  @Test
  public void shouldReturnCalendar() {
    ZonedDateTime now = ZonedDateTime.now();
    when(dateTimeService.getCurrentDateAndTime()).thenReturn(now);
    AuditingDateTimeProvider timeProvider = new AuditingDateTimeProvider(dateTimeService);

    ZonedDateTime expected = ZonedDateTime.from(dateTimeService.getCurrentDateAndTime());
    assertThat(timeProvider.getNow(), is(Optional.of(expected)));
  }

}