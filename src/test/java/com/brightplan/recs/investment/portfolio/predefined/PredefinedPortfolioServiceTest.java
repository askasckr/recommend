package com.brightplan.recs.investment.portfolio.predefined;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.brightplan.recs.errorhandler.RecsException;
import com.brightplan.recs.investment.category.InvestmentCategory;
import com.brightplan.recs.investment.risk.InvestmentRisk;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

@RunWith(MockitoJUnitRunner.class)
public class PredefinedPortfolioServiceTest {

  @Mock
  private PredefinedPortfolioPercentRepository predefinedPortfolioPercentRepository;

  @Mock
  private PredefinedPortfolioValidation predefinedPortfolioValidation;

  @InjectMocks
  private PredefinedPortfolioService predefinedPortfolioService;

  @Test
  public void shouldReturnInvestmentRiskVsCategoryPercents() {
    when(predefinedPortfolioPercentRepository.findAll())
        .thenReturn(stubGeneratePredefinedPortfolioPercents(true));
    predefinedPortfolioService.getAllPredefinedPortfolioPercents();
    verify(predefinedPortfolioPercentRepository).findAll();
  }

  @Test(expected = RecsException.class)
  public void shouldThrowException_WhenFoundEmptyInvestmentRiskVsCategoryPercents() {
    when(predefinedPortfolioPercentRepository.findAll()).thenReturn(ImmutableList.of());
    predefinedPortfolioService.getAllPredefinedPortfolioPercents();
  }

  @Test
  public void shouldReturnInvestmentRiskVsCategoryPercentsMatrix() {
    when(predefinedPortfolioPercentRepository.findAll())
        .thenReturn(stubGeneratePredefinedPortfolioPercents(true));
    Map<Integer, List<PredefinedPortfolioPercent>> matrix = predefinedPortfolioService
        .getAllPredefinedPortfolioPercentsMatrix();
    verify(predefinedPortfolioPercentRepository).findAll();
    assertThat(matrix, is(notNullValue()));
    assertThat(matrix.size(), is(equalTo(10)));
    assertThat(matrix.get(1).size(), is(equalTo(5)));
  }

  @Test(expected = RecsException.class)
  public void shouldThrowException_WhenFoundEmptyInvestmentRiskVsCategoryPercentsForMatrix() {
    when(predefinedPortfolioPercentRepository.findAll()).thenReturn(ImmutableList.of());
    predefinedPortfolioService.getAllPredefinedPortfolioPercentsMatrix();
  }

  @Test
  public void shouldReturnPredefinedPortfolioPercents_GivenRiskId() {
    when(predefinedPortfolioPercentRepository
        .findPredefinedPortfolioPercentByInvestmentRisk_Id(1))
        .thenReturn(stubGeneratePredefinedPortfolioPercentsForRiskId(1));
    predefinedPortfolioService.getPredefinedPortfolioPercents(1);
    verify(predefinedPortfolioPercentRepository)
        .findPredefinedPortfolioPercentByInvestmentRisk_Id(1);
  }

  @Test(expected = RecsException.class)
  public void shouldThrowException_WhenFoundEmptyPredefinedPortfolioPercents_GivenRiskId() {
    when(predefinedPortfolioPercentRepository
        .findPredefinedPortfolioPercentByInvestmentRisk_Id(1)).thenReturn(ImmutableList.of());
    predefinedPortfolioService.getPredefinedPortfolioPercents(1);
  }

  @Test
  public void shouldSavePredefinedPortfolioPercents() {
    List<PredefinedPortfolioPercent> predefinedPortfolioPercentsInput = stubGeneratePredefinedPortfolioPercents(
        false);
    when(predefinedPortfolioPercentRepository.saveAll(predefinedPortfolioPercentsInput))
        .thenReturn(stubGeneratePredefinedPortfolioPercents(true));
    when(predefinedPortfolioValidation
        .performPercentsSumUpTo100AndReturnMergedList(predefinedPortfolioPercentsInput))
        .thenReturn(predefinedPortfolioPercentsInput);
    predefinedPortfolioService.savePredefinedPortfolioPercents(predefinedPortfolioPercentsInput);
    verify(predefinedPortfolioValidation)
        .performPercentsSumUpTo100AndReturnMergedList(predefinedPortfolioPercentsInput);
    verify(predefinedPortfolioPercentRepository).saveAll(predefinedPortfolioPercentsInput);
  }

  @Test(expected = RecsException.class)
  public void shouldThrow_WhenFoundInvalidPredefinedPortfolioPercents() {
    List<PredefinedPortfolioPercent> predefinedPortfolioPercentsInput = stubGeneratePredefinedPortfolioPercents(
        false);
    when(predefinedPortfolioValidation
        .performPercentsSumUpTo100AndReturnMergedList(predefinedPortfolioPercentsInput))
        .thenThrow(new RecsException(
            HttpStatus.BAD_REQUEST, "Invalid data")).thenReturn(predefinedPortfolioPercentsInput);
    predefinedPortfolioService.savePredefinedPortfolioPercents(predefinedPortfolioPercentsInput);
    verify(predefinedPortfolioValidation)
        .performPercentsSumUpTo100AndReturnMergedList(predefinedPortfolioPercentsInput);
  }

  @Test
  public void shouldDeleteAllPredefinedPortfolioPercents() {
    doNothing().when(predefinedPortfolioPercentRepository).deleteAllInBatch();
    predefinedPortfolioService.deletePredefinedPortfolioPercents(Optional.empty());
    verify(predefinedPortfolioPercentRepository).deleteAllInBatch();
  }

  @Test
  public void shouldDeletePredefinedPortfolioPercents() {
    Optional<List<Integer>> listOfIds = Optional.of(ImmutableList.of(1, 2));
    doNothing().when(predefinedPortfolioPercentRepository).deleteById(1);
    predefinedPortfolioService.deletePredefinedPortfolioPercents(listOfIds);
    verify(predefinedPortfolioPercentRepository).deleteById(1);
    verify(predefinedPortfolioPercentRepository).deleteById(2);
  }

  private List<PredefinedPortfolioPercent> stubGeneratePredefinedPortfolioPercents(
      boolean withIds) {
    String[] investmentCategories = {"Bonds", "Large Cap", "Mid Cap", "Foreign", "Small Cap"};
    return Stream.iterate(1, n -> n + 1).limit(10).map(r -> {
      List<PredefinedPortfolioPercent> percents = Lists.newArrayList();
      int i = 1;
      for (String category : investmentCategories) {
        percents.add(
            PredefinedPortfolioPercent.builder().id(withIds ? r : null)
                .percent(new BigDecimal(100 / investmentCategories.length).floatValue())
                .investmentRisk(InvestmentRisk.builder().id(r).level(r).build())
                .investmentCategory(InvestmentCategory.builder().id(i).name(category)
                    .displayOrder(i)
                    .build()).build());
      }

      return percents;

    }).flatMap(List::stream).collect(toList());
  }

  private List<PredefinedPortfolioPercent> stubGeneratePredefinedPortfolioPercentsForRiskId(
      int riskId) {
    String[] investmentCategories = {"Bonds", "Large Cap", "Mid Cap", "Foreign", "Small Cap"};
    List<PredefinedPortfolioPercent> percents = Lists.newArrayList();
    int i = 1;
    for (String category : investmentCategories) {
      percents.add(
          PredefinedPortfolioPercent.builder().id(i)
              .percent(new BigDecimal(100 / investmentCategories.length).floatValue())
              .investmentRisk(InvestmentRisk.builder().id(riskId).level(riskId).build())
              .investmentCategory(InvestmentCategory.builder().id(i).name(category)
                  .displayOrder(i)
                  .build()).build());
      i++;
    }

    return percents;

  }
}
