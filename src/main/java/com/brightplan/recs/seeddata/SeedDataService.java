package com.brightplan.recs.seeddata;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import com.brightplan.recs.investment.category.InvestmentCategory;
import com.brightplan.recs.investment.category.InvestmentCategoryService;
import com.brightplan.recs.investment.portfolio.predefined.PredefinedPortfolioPercent;
import com.brightplan.recs.investment.portfolio.predefined.PredefinedPortfolioService;
import com.brightplan.recs.investment.risk.InvestmentRisk;
import com.brightplan.recs.investment.risk.InvestmentRiskService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SeedDataService {

  private static final Logger log = LoggerFactory.getLogger(SeedDataService.class);

  @Autowired
  private InvestmentCategoryService investmentCategoryService;

  @Autowired
  private InvestmentRiskService investmentRiskService;

  @Autowired
  private PredefinedPortfolioService predefinedPortfolioService;

  /**
   * Cleans up all table data.
   */
  public void cleanUp() {
    predefinedPortfolioService.deletePredefinedPortfolioPercents(Optional.empty());
    ;
    investmentRiskService.delete(Optional.empty());
    investmentCategoryService.delete(Optional.empty());
  }


  /**
   * Prepare Seed data for the investment_category table.
   */
  private String[] getInvestmentCategoriesSeed() {
    String[] investmentCategories = {"Bonds", "Large Cap", "Mid Cap", "Foreign", "Small Cap"};
    return investmentCategories;
  }

  /**
   * Prepare Seed data for the display_order of investment_category table.
   */
  private Map<String, Integer> getInvestmentCategoriesWithDisplayOrder() {
    Map<String, Integer> investmentCategories = Maps.newHashMap();
    int i = 0;
    for (String category : getInvestmentCategoriesSeed()) {
      investmentCategories.put(category, i);
      i++;
    }
    return investmentCategories;
  }

  /**
   * Prepare Seed data for the riskLevels.
   */
  private List<Integer> getInvesmentRiskLevelSeed() {
    return Stream.iterate(1, n -> n + 1).limit(10).collect(toList());
  }

  /**
   * Save Seed data for the investment_category table.
   */
  private Map<String, InvestmentCategory> saveInvestmentCategoriesSeed() {
    investmentCategoryService
        .create(InvestmentCategory.builder().name("hello").displayOrder(-1).build());
    Map<String, Integer> displayOrder = getInvestmentCategoriesWithDisplayOrder();
    return Stream.of(getInvestmentCategoriesSeed()).map(
        s -> investmentCategoryService
            .create(InvestmentCategory.builder().name(s).displayOrder(displayOrder.get(s)).build()))
        .collect(toMap(InvestmentCategory::getName, Function.identity()));
  }

  /**
   * Prepare Seed the predefined_portfolio for all risk categories
   */
  private List<?>[] getInvestmentRiskCategoryPercentMatrixSeed() {
    List<?>[] riskCategoryPercentMatrix = {
        Arrays.asList(80, 20, 0, 0, 0),
        Arrays.asList(70, 15, 15, 0, 0),
        Arrays.asList(60, 15, 15, 10, 0),
        Arrays.asList(50, 20, 20, 10, 0),
        Arrays.asList(40, 20, 20, 20, 0),
        Arrays.asList(35, 25, 5, 30, 5),
        Arrays.asList(20, 25, 25, 25, 5),
        Arrays.asList(10, 20, 40, 20, 10),
        Arrays.asList(5, 15, 40, 25, 15),
        Arrays.asList(0, 5, 25, 30, 40)
    };
    return riskCategoryPercentMatrix;
  }

  /**
   * Save the seed data for Investment Risk and PredefinedPortfolios.
   */
  public void populateDBWithInvestmentRiskCategoryPercentSeed() {
    String[] investmentCategories = getInvestmentCategoriesSeed();
    Map<String, InvestmentCategory> investmentCategoryMap = saveInvestmentCategoriesSeed();
    List<?>[] riskCategoryPercentMatrix = getInvestmentRiskCategoryPercentMatrixSeed();

    // Save the seed data for Investment Risk and PredefinedPortfolios
    getInvesmentRiskLevelSeed().forEach(i -> {
      // Save investmentRisk for level i, where i ranging from 1 to 10
      InvestmentRisk investmentRisk = investmentRiskService
          .create(InvestmentRisk.builder().level(i).build());

      List<PredefinedPortfolioPercent> predefinedPortfolioPercentList = Lists.newArrayList();
      AtomicInteger j = new AtomicInteger(-1);
      riskCategoryPercentMatrix[i - 1].forEach(
          percent -> {

            // Get investmentCategory
            InvestmentCategory investmentCategory = investmentCategoryMap
                .get(investmentCategories[j.incrementAndGet()]);
            predefinedPortfolioPercentList
                .add(PredefinedPortfolioPercent.builder().investmentRisk(investmentRisk)
                    .investmentCategory(investmentCategory)
                    .percent(Float.valueOf(percent.toString())).build());

          }
      );
      predefinedPortfolioService.savePredefinedPortfolioPercents(predefinedPortfolioPercentList);
    });

  }
}
