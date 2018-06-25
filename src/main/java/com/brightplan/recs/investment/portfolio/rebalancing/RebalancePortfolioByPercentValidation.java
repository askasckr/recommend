package com.brightplan.recs.investment.portfolio.rebalancing;

import static java.util.Comparator.comparing;
import static java.util.Objects.isNull;

import com.brightplan.recs.errorhandler.RecsException;
import com.brightplan.recs.investment.portfolio.rebalancing.dto.Allocation;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.Data;
import org.springframework.http.HttpStatus;

public class RebalancePortfolioByPercentValidation {

  public static boolean validateAllocationsCategoriesEqualsInvestmentCategories(
      Map<Integer, Map<String, Float>> categoryIdNamePercents, List<Allocation> allocations) {
    // NumberOfAmountAllocationsValidation
    if (categoryIdNamePercents.isEmpty()) {
      throw new RecsException(HttpStatus.INTERNAL_SERVER_ERROR, "Risk profile not found");
    } else if (allocations.stream().map(Allocation::getInvestmentCategoryId).distinct().count()
        != categoryIdNamePercents.size()) {
      throw new RecsException(HttpStatus.BAD_REQUEST,
          "Number of amount allocations must be equal to investment categories");
    } else {
      allocations.forEach(a -> {
        if (isNull(categoryIdNamePercents.get(a.getInvestmentCategoryId()))) {
          throw new RecsException(HttpStatus.BAD_REQUEST,
              "Encountered some of allocations with invalid investment category");
        }
      });
    }
    return true;
  }

  public static boolean validateSumOfPostCalulationDiffAmounts(
      List<Allocation> allocationsWithDiffs) {
    // PostCalculateDiffAmountsValidation
    if (0.0 != allocationsWithDiffs.stream().mapToDouble(Allocation::getDiffAmount)
        .sum()) {
      throw new RecsException(HttpStatus.INTERNAL_SERVER_ERROR,
          "Post diff calculation check error: Calculated Diff Amounts sum is non-zero, must be zero.");
    }
    return true;
  }

  @Data
  static class ByPercentTransferCriteria {

    private Optional<Allocation> maxNeededAlloc;

    private Optional<Allocation> maxExcessAlloc;

    public ByPercentTransferCriteria(Map<Integer, Allocation> neededAllocMap,
        Map<Integer, Allocation> excessAmtsMap) {

      maxNeededAlloc = neededAllocMap.values().stream()
          .max(comparing(Allocation::getDiffAmountAfterTransfer));
      maxExcessAlloc = excessAmtsMap.values().stream()
          .min(comparing(Allocation::getDiffAmountAfterTransfer));
    }
  }
}