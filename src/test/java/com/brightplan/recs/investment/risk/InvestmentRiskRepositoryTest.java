package com.brightplan.recs.investment.risk;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;

import com.brightplan.recs.RecsApplication;
import com.brightplan.recs.auditing.PersistenceContext;
import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ActiveProfiles("localUnitAndIntTest")
@SpringBootTest(classes = {RecsApplication.class, PersistenceContext.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class InvestmentRiskRepositoryTest {

  @Autowired
  private InvestmentRiskRepository investmentRiskRepository;

  @Test
  public void shouldFindAllInvestmentRisks() {
    assertThat(investmentRiskRepository.findAll().size(), is(equalTo(0)));
  }

  @Test
  public void shouldSaveInvestmentRisk() {
    Integer level = 1;
    InvestmentRisk investmentRisk = InvestmentRisk.builder().level(level)
        .build();

    InvestmentRisk savedInvestmentRisk = investmentRiskRepository
        .save(investmentRisk);
    assertThat(savedInvestmentRisk.getId(), notNullValue());

    Optional<InvestmentRisk> investmentRiskFound = investmentRiskRepository
        .findInvestmentRiskByLevel(level);
    assertThat(investmentRiskFound.isPresent(), is(true));
    assertThat(investmentRiskFound.get().getLevel(), notNullValue());
    assertThat(investmentRiskFound.get().getLevel(), is(equalTo(1)));
  }

}
