package com.brightplan.recs.investment.portfolio.predefined;

import com.brightplan.recs.investment.category.InvestmentCategory;
import com.brightplan.recs.investment.portfolio.predefined.PredefinedPortfolioPercent.PredefinedPortfolioPercentEntityFieldValidation;
import com.brightplan.recs.investment.risk.InvestmentRisk;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PredefinedPortfolioPercentTest {
  private static Validator validator;
  private PredefinedPortfolioPercent predefinedPortfolioPercent;

  @Before
  public void before() throws Exception {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
    predefinedPortfolioPercent = PredefinedPortfolioPercent.builder()
        .investmentRisk(InvestmentRisk.builder().id(1).build())
        .investmentCategory(InvestmentCategory.builder().id(1).build())
       .build();
    predefinedPortfolioPercent.created = ZonedDateTime.now();
    predefinedPortfolioPercent.createdBy = "test";
  }

  @Test
  public void percentInvalid() {
    predefinedPortfolioPercent.setPercent(3.456788f);

    Set<ConstraintViolation<PredefinedPortfolioPercent>> constraintViolations = validator
        .validate(predefinedPortfolioPercent, PredefinedPortfolioPercentEntityFieldValidation.class);

    List<String> expected = Arrays.asList("Percent format can be ddd.fff (3 digits after decimal)");

    Assert.assertEquals(1, constraintViolations.size());
    Assert.assertTrue(expected.contains(constraintViolations.iterator().next().getMessage()));
  }

}
