package com.brightplan.recs.investment.portfolio.rebalancing;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import com.brightplan.recs.errorhandler.RecsException;
import com.brightplan.recs.investment.category.InvestmentCategory;
import com.brightplan.recs.investment.portfolio.predefined.PredefinedPortfolioPercent;
import com.brightplan.recs.investment.portfolio.rebalancing.dto.Allocation;
import com.brightplan.recs.investment.risk.InvestmentRisk;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

@RunWith(MockitoJUnitRunner.class)
public class RebalancePortfolioByPercentValidationTest {

  private static Gson gson;

  @BeforeClass
  public static void setup() {
    gson = new Gson();
  }

  public static void validatePostCalulationDiffAmounts(List<Allocation> allocationsWithDiffs) {
    // PostCalculateDiffAmountsValidation
    if (0.0 != allocationsWithDiffs.stream().mapToDouble(Allocation::getDiffAmount)
        .sum()) {
      throw new RecsException(HttpStatus.INTERNAL_SERVER_ERROR,
          "Post diff calculation check error: Calculated Diff Amounts sum is non-zero, must be zero.");
    }
  }

  @Test
  public void shouldReturnTrue_WhenInputAllocationsMatchesWithCategories() {
    assertThat(true, is(RebalancePortfolioByPercentValidation
        .validateAllocationsCategoriesEqualsInvestmentCategories(
            stubGeneratePredefinedPortfolioPercentsForRiskId(1), stubAllocations())));
  }

  @Test(expected = RecsException.class)
  public void shouldReturnFalse_WhenInputInvalidAllocations() {
    RebalancePortfolioByPercentValidation.validateAllocationsCategoriesEqualsInvestmentCategories(
        stubGeneratePredefinedPortfolioPercentsForRiskId(1), stubInvalidAllocations());
  }

  @Test(expected = RecsException.class)
  public void shouldReturnFalse_WhenInputAllocationsWithMissingCategory() {
    RebalancePortfolioByPercentValidation.validateAllocationsCategoriesEqualsInvestmentCategories(
        stubGeneratePredefinedPortfolioPercentsForRiskId(1), stubAllocationsWithMissingCategory());
  }

  @Test
  public void shouldReturnTrue_WhenEachPostCalulationDiffAmountIsAreZero() {
    assertThat(true, is(RebalancePortfolioByPercentValidation
        .validateSumOfPostCalulationDiffAmounts(stubValidPostCalculationAllocations())));
  }

  @Test(expected = RecsException.class)
  public void shouldReturnTrue_WhenEachPostCalulationDiffAmountIsAreNonZero() {
    RebalancePortfolioByPercentValidation
        .validateSumOfPostCalulationDiffAmounts(stubInvalidPostCalculationAllocations());
  }

  private List<Allocation> stubAllocations() {
    String allocationsString = "[\n"
        + "\n"
        + "{\n"
        + "\t\"investmentCategoryId\": 1,\n"
        + "\t\"amount\": 65\n"
        + "},\n"
        + "{\n"
        + "\t\"investmentCategoryId\": 2,\n"
        + "\t\"amount\": 65\n"
        + "},\n"
        + "{\n"
        + "\t\"investmentCategoryId\": 3,\n"
        + "\t\"amount\": 567\n"
        + "},\n"
        + "{\n"
        + "\t\"investmentCategoryId\": 4,\n"
        + "\t\"amount\": 1265\n"
        + "},\n"
        + "{\n"
        + "\t\"investmentCategoryId\": 5,\n"
        + "\t\"amount\": 2465\n"
        + "}\n"
        + "\n"
        + "]";
    Type typeToken = new TypeToken<List<Allocation>>() {
    }.getType();
    return gson.fromJson(allocationsString, typeToken);
  }

  private List<Allocation> stubInvalidAllocations() {
    String allocationsString = "[\n"
        + "\n"
        + "{\n"
        + "\t\"investmentCategoryId\": 1,\n"
        + "\t\"amount\": 65\n"
        + "},\n"
        + "{\n"
        + "\t\"investmentCategoryId\": 2,\n"
        + "\t\"amount\": 65\n"
        + "},\n"
        + "{\n"
        + "\t\"investmentCategoryId\": 3,\n"
        + "\t\"amount\": 567\n"
        + "},\n"
        + "{\n"
        + "\t\"investmentCategoryId\": 4,\n"
        + "\t\"amount\": 1265\n"
        + "},\n"
        + "{\n"
        + "\t\"investmentCategoryId\": 6,\n" // invalid one
        + "\t\"amount\": 2465\n"
        + "}\n"
        + "\n"
        + "]";
    Type typeToken = new TypeToken<List<Allocation>>() {
    }.getType();
    return gson.fromJson(allocationsString, typeToken);
  }

  private List<Allocation> stubAllocationsWithMissingCategory() {
    String allocationsString = "[\n"
        + "\n"
        + "{\n"
        + "\t\"investmentCategoryId\": 1,\n"
        + "\t\"amount\": 65\n"
        + "},\n"
        + "{\n"
        + "\t\"investmentCategoryId\": 2,\n"
        + "\t\"amount\": 65\n"
        + "},\n"
        + "{\n"
        + "\t\"investmentCategoryId\": 3,\n"
        + "\t\"amount\": 567\n"
        + "},\n"
        + "{\n"
        + "\t\"investmentCategoryId\": 4,\n"
        + "\t\"amount\": 1265\n"
        + "}\n"
        // Removed a category here
        + "\n"
        + "]";
    Type typeToken = new TypeToken<List<Allocation>>() {
    }.getType();
    return gson.fromJson(allocationsString, typeToken);
  }

  private Map<Integer, Map<String, Float>> stubGeneratePredefinedPortfolioPercentsForRiskId(
      int riskId) {
    String[] investmentCategories = {"Bonds", "Large Cap", "Mid Cap", "Foreign", "Small Cap"};
    List<PredefinedPortfolioPercent> categoryPercents = Lists.newArrayList();

    categoryPercents.add(
        PredefinedPortfolioPercent.builder().id(1)
            .percent(5.0f)
            .investmentRisk(InvestmentRisk.builder().id(riskId).level(riskId).build())
            .investmentCategory(InvestmentCategory.builder().id(1).name(investmentCategories[0])
                .displayOrder(1)
                .build()).build());

    categoryPercents.add(
        PredefinedPortfolioPercent.builder().id(2)
            .percent(15.0f)
            .investmentRisk(InvestmentRisk.builder().id(riskId).level(riskId).build())
            .investmentCategory(InvestmentCategory.builder().id(2).name(investmentCategories[1])
                .displayOrder(2)
                .build()).build());

    categoryPercents.add(
        PredefinedPortfolioPercent.builder().id(3)
            .percent(40.0f)
            .investmentRisk(InvestmentRisk.builder().id(riskId).level(riskId).build())
            .investmentCategory(InvestmentCategory.builder().id(3).name(investmentCategories[2])
                .displayOrder(3)
                .build()).build());

    categoryPercents.add(
        PredefinedPortfolioPercent.builder().id(4)
            .percent(25.0f)
            .investmentRisk(InvestmentRisk.builder().id(riskId).level(riskId).build())
            .investmentCategory(InvestmentCategory.builder().id(4).name(investmentCategories[3])
                .displayOrder(4)
                .build()).build());

    categoryPercents.add(
        PredefinedPortfolioPercent.builder().id(5)
            .percent(15.0f)
            .investmentRisk(InvestmentRisk.builder().id(riskId).level(riskId).build())
            .investmentCategory(InvestmentCategory.builder().id(5).name(investmentCategories[4])
                .displayOrder(5)
                .build()).build());

    return categoryPercents.stream()
        .collect(groupingBy(p -> p.getInvestmentCategory().getId(),
            toMap(p -> p.getInvestmentCategory().getName(),
                PredefinedPortfolioPercent::getPercent)));

  }

  private List<Allocation> stubValidPostCalculationAllocations() {
    String allocationsString = "[\n"
        + "    {\n"
        + "        \"amount\": 65,\n"
        + "        \"investmentCategoryId\": 1,\n"
        + "        \"diffAmount\": 156.35,\n"
        + "        \"transferDetail\": \"Transfer $156.35 from Foreign to Bonds\",\n"
        + "        \"investmentCategoryName\": \"Bonds\",\n"
        + "        \"percent\": 5\n"
        + "    },\n"
        + "    {\n"
        + "        \"amount\": 65,\n"
        + "        \"investmentCategoryId\": 2,\n"
        + "        \"diffAmount\": 599.05,\n"
        + "        \"transferDetail\": \"Transfer $597.15 from Small Cap to Large Cap, Transfer $1.90 from Foreign to Large Cap\",\n"
        + "        \"investmentCategoryName\": \"Large Cap\",\n"
        + "        \"percent\": 15\n"
        + "    },\n"
        + "    {\n"
        + "        \"amount\": 567,\n"
        + "        \"investmentCategoryId\": 3,\n"
        + "        \"diffAmount\": 1203.8,\n"
        + "        \"transferDetail\": \"Transfer $1203.80 from Small Cap to Mid Cap\",\n"
        + "        \"investmentCategoryName\": \"Mid Cap\",\n"
        + "        \"percent\": 40\n"
        + "    },\n"
        + "    {\n"
        + "        \"amount\": 1265,\n"
        + "        \"investmentCategoryId\": 4,\n"
        + "        \"diffAmount\": -158.25,\n"
        + "        \"transferDetail\": null,\n"
        + "        \"investmentCategoryName\": \"Foreign\",\n"
        + "        \"percent\": 25\n"
        + "    },\n"
        + "    {\n"
        + "        \"amount\": 2465,\n"
        + "        \"investmentCategoryId\": 5,\n"
        + "        \"diffAmount\": -1800.95,\n"
        + "        \"transferDetail\": null,\n"
        + "        \"investmentCategoryName\": \"Small Cap\",\n"
        + "        \"percent\": 15\n"
        + "    }\n"
        + "]";
    Type typeToken = new TypeToken<List<Allocation>>() {
    }.getType();
    return gson.fromJson(allocationsString, typeToken);
  }

  private List<Allocation> stubInvalidPostCalculationAllocations() {
    String allocationsString = "[\n"
        + "    {\n"
        + "        \"amount\": 65,\n"
        + "        \"investmentCategoryId\": 1,\n"
        + "        \"diffAmount\": 156.35,\n"
        + "        \"transferDetail\": \"Transfer $156.35 from Foreign to Bonds\",\n"
        + "        \"investmentCategoryName\": \"Bonds\",\n"
        + "        \"percent\": 5\n"
        + "    },\n"
        + "    {\n"
        + "        \"amount\": 65,\n"
        + "        \"investmentCategoryId\": 2,\n"
        + "        \"diffAmount\": 599.05,\n"
        + "        \"transferDetail\": \"Transfer $597.15 from Small Cap to Large Cap, Transfer $1.90 from Foreign to Large Cap\",\n"
        + "        \"investmentCategoryName\": \"Large Cap\",\n"
        + "        \"percent\": 15\n"
        + "    },\n"
        + "    {\n"
        + "        \"amount\": 567,\n"
        + "        \"investmentCategoryId\": 3,\n"
        + "        \"diffAmount\": 1203.8,\n"
        + "        \"transferDetail\": \"Transfer $1203.80 from Small Cap to Mid Cap\",\n"
        + "        \"investmentCategoryName\": \"Mid Cap\",\n"
        + "        \"percent\": 40\n"
        + "    },\n"
        + "    {\n"
        + "        \"amount\": 1265,\n"
        + "        \"investmentCategoryId\": 4,\n"
        + "        \"diffAmount\": -158.25,\n"
        + "        \"transferDetail\": null,\n"
        + "        \"investmentCategoryName\": \"Foreign\",\n"
        + "        \"percent\": 25\n"
        + "    },\n"
        + "    {\n"
        + "        \"amount\": 2465,\n"
        + "        \"investmentCategoryId\": 5,\n"
        + "        \"diffAmount\": 0.95,\n" // Invalid Amount
        + "        \"transferDetail\": null,\n"
        + "        \"investmentCategoryName\": \"Small Cap\",\n"
        + "        \"percent\": 15\n"
        + "    }\n"
        + "]";
    Type typeToken = new TypeToken<List<Allocation>>() {
    }.getType();
    return gson.fromJson(allocationsString, typeToken);
  }

}
