package com.brightplan.recs.investment.portfolio.predefined;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

import com.brightplan.recs.errorhandler.RecsException;
import com.brightplan.recs.investment.category.InvestmentCategory;
import com.brightplan.recs.investment.risk.InvestmentRisk;
import com.google.common.collect.Lists;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PredefinedPortfolioValidationTest {

  @InjectMocks
  private PredefinedPortfolioValidation predefinedPortfolioValidation;

  @Mock
  private PredefinedPortfolioPercentRepository predefinedPortfolioPercentRepository;

  @Test
  public void shouldReturnValidMergedList_WhenPercentsSumUpTo100ByRisk() {
    List<PredefinedPortfolioPercent> input = stubGeneratePredefinedPortfolioPercents(true);
    when(predefinedPortfolioPercentRepository.findPredefinedPortfolioPercentByInvestmentRisk_Id(1))
        .thenReturn(stubGeneratePredefinedPortfolioPercents(true, 1));
    when(predefinedPortfolioPercentRepository.findPredefinedPortfolioPercentByInvestmentRisk_Id(2))
        .thenReturn(stubGeneratePredefinedPortfolioPercents(true, 2));
    when(predefinedPortfolioPercentRepository.findPredefinedPortfolioPercentByInvestmentRisk_Id(3))
        .thenReturn(stubGeneratePredefinedPortfolioPercents(true, 3));
    when(predefinedPortfolioPercentRepository.findPredefinedPortfolioPercentByInvestmentRisk_Id(4))
        .thenReturn(stubGeneratePredefinedPortfolioPercents(true, 4));
    when(predefinedPortfolioPercentRepository.findPredefinedPortfolioPercentByInvestmentRisk_Id(5))
        .thenReturn(stubGeneratePredefinedPortfolioPercents(true, 5));
    when(predefinedPortfolioPercentRepository.findPredefinedPortfolioPercentByInvestmentRisk_Id(6))
        .thenReturn(stubGeneratePredefinedPortfolioPercents(true, 6));
    when(predefinedPortfolioPercentRepository.findPredefinedPortfolioPercentByInvestmentRisk_Id(7))
        .thenReturn(stubGeneratePredefinedPortfolioPercents(true, 7));
    when(predefinedPortfolioPercentRepository.findPredefinedPortfolioPercentByInvestmentRisk_Id(8))
        .thenReturn(stubGeneratePredefinedPortfolioPercents(true, 8));
    when(predefinedPortfolioPercentRepository.findPredefinedPortfolioPercentByInvestmentRisk_Id(9))
        .thenReturn(stubGeneratePredefinedPortfolioPercents(true, 9));
    when(predefinedPortfolioPercentRepository.findPredefinedPortfolioPercentByInvestmentRisk_Id(10))
        .thenReturn(stubGeneratePredefinedPortfolioPercents(true, 10));
    assertThat(predefinedPortfolioValidation
            .performPercentsSumUpTo100AndReturnMergedList(input).size(),
        is(equalTo(input.size())));
  }

  @Test(expected = RecsException.class)
  public void shouldThrowException_WhenMergedPercentsDoNotSumUpTo100ByRisk() {
    List<PredefinedPortfolioPercent> input = stubGeneratePredefinedPortfolioPercents(true);
    when(predefinedPortfolioPercentRepository
        .findPredefinedPortfolioPercentByInvestmentRisk_Id(anyInt()))
        .thenReturn(stubGeneratePredefinedInvalidPortfolioPercents(true, anyInt()));
    predefinedPortfolioValidation
        .performPercentsSumUpTo100AndReturnMergedList(input);
  }

  @Test(expected = RecsException.class)
  public void shouldTrowException_WhenPercentsDonotSumUpTo100ByRisk() {
    predefinedPortfolioValidation
        .performPercentsSumUpTo100AndReturnMergedList(
            stubGeneratePredefinedPortfolioPercents(false));
  }

  private List<PredefinedPortfolioPercent> stubGeneratePredefinedPortfolioPercents(
      boolean with100Percent) {
    String[] investmentCategories = {"Bonds", "Large Cap", "Mid Cap", "Foreign", "Small Cap"};

    return Stream.iterate(1, n -> n + 1).limit(10).map(r -> {
      List<PredefinedPortfolioPercent> percents = Lists.newArrayList();
      int i = 1;
      for (String category : investmentCategories) {
        percents.add(
            PredefinedPortfolioPercent.builder().id((percents.size() + 1))
                .percent(with100Percent ? new BigDecimal(100 / investmentCategories.length)
                    .floatValue()
                    : new BigDecimal(100 / (investmentCategories.length + 1)).floatValue())
                .investmentRisk(InvestmentRisk.builder().id(r).level(r).build())
                .investmentCategory(InvestmentCategory.builder().id(i).name(category)
                    .displayOrder(i)
                    .build()).build());
        i++;
      }

      return percents;

    }).flatMap(List::stream).collect(toList());
  }


  private List<PredefinedPortfolioPercent> stubGeneratePredefinedPortfolioPercents(
      boolean with100Percent, Integer riskId) {
    String[] investmentCategories = {"Bonds", "Large Cap", "Mid Cap", "Foreign", "Small Cap"};

    return Stream.iterate(1, n -> n + 1).limit(10).map(r -> {
      List<PredefinedPortfolioPercent> percents = Lists.newArrayList();
      int i = 1;
      if (riskId == r) {
        for (String category : investmentCategories) {
          percents.add(
              PredefinedPortfolioPercent.builder().id((percents.size() + 1))
                  .percent(with100Percent ? new BigDecimal(100 / investmentCategories.length)
                      .floatValue()
                      : new BigDecimal(100 / (investmentCategories.length + 1)).floatValue())
                  .investmentRisk(InvestmentRisk.builder().id(r).level(r).build())
                  .investmentCategory(InvestmentCategory.builder().id(i).name(category)
                      .displayOrder(i)
                      .build()).build());
          i++;
        }
      }

      return percents;

    }).flatMap(List::stream).collect(toList());
  }

  private List<PredefinedPortfolioPercent> stubGeneratePredefinedInvalidPortfolioPercents(
      boolean with100Percent, Integer riskId) {
    String[] investmentCategories = {"Bonds", "Large Cap", "Mid Cap", "Foreign", "Small Cap",
        "Extra Small"};

    return Stream.iterate(1, n -> n + 1).limit(10).map(r -> {
      List<PredefinedPortfolioPercent> percents = Lists.newArrayList();
      int i = 1;
      if (riskId == r) {
        for (String category : investmentCategories) {
          percents.add(
              PredefinedPortfolioPercent.builder().id((percents.size() + 1))
                  .percent(with100Percent ? new BigDecimal(100 / investmentCategories.length + 1)
                      .floatValue()
                      : new BigDecimal(100 / (investmentCategories.length + 1)).floatValue())
                  .investmentRisk(InvestmentRisk.builder().id(r).level(r).build())
                  .investmentCategory(InvestmentCategory.builder().id(i).name(category)
                      .displayOrder(i)
                      .build()).build());
          i++;
        }
      }

      return percents;

    }).flatMap(List::stream).collect(toList());
  }

}
