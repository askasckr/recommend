package com.brightplan.recs.investment.portfolio.rebalancing;

import static com.brightplan.recs.common.RecsConstant.DOUBLE_ZERO;
import static com.brightplan.recs.common.RecsConstant.INT_100;
import static com.brightplan.recs.common.RecsConstant.SCALE_2;
import static com.brightplan.recs.investment.portfolio.rebalancing.RebalancePortfolioByPercentValidation.validateAllocationsCategoriesEqualsInvestmentCategories;
import static com.brightplan.recs.investment.portfolio.rebalancing.RebalancePortfolioByPercentValidation.validateSumOfPostCalulationDiffAmounts;
import static java.lang.Math.abs;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.partitioningBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import com.brightplan.recs.investment.portfolio.predefined.PredefinedPortfolioPercent;
import com.brightplan.recs.investment.portfolio.predefined.PredefinedPortfolioService;
import com.brightplan.recs.investment.portfolio.rebalancing.RebalancePortfolioByPercentValidation.ByPercentTransferCriteria;
import com.brightplan.recs.investment.portfolio.rebalancing.dto.Allocation;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RebalancePortfolioByPercentMethod implements RebalancePortfolioMethod {

  @Autowired
  private PredefinedPortfolioService predefinedPortfolioService;

  public List<Allocation> rebalance(Integer riskProfileId,
      List<Allocation> allocations) {
    //List<Allocation> allocations, PredefinedPortfolioService predefinedPortfolioService) {
    List<Allocation> allocationsWithAdjustments = allocations;
    // Get total allocation amount
    Double totalAllocatedAmt = allocations.stream().mapToDouble(Allocation::getAmount).sum();
    List<PredefinedPortfolioPercent> categoryPercents = predefinedPortfolioService
        .getPredefinedPortfolioPercents(riskProfileId);
    // Prepare categoryIdNamePercents matrix
    Map<Integer, Map<String, Float>> categoryIdNamePercents = categoryPercents.stream()
        .collect(groupingBy(p -> p.getInvestmentCategory().getId(),
            toMap(p -> p.getInvestmentCategory().getName(),
                PredefinedPortfolioPercent::getPercent)));
    // Number of AllocationsEqualsCategories
    validateAllocationsCategoriesEqualsInvestmentCategories(categoryIdNamePercents, allocations);
    // Update diff amounts
    allocationsWithAdjustments = calculateDiffAmounts(allocations,
        totalAllocatedAmt, categoryIdNamePercents);
    // PostCalculateDiffAmountsValidation
    validateSumOfPostCalulationDiffAmounts(allocationsWithAdjustments);
    // Get Partitioned excessAndNeededAllocations
    Map<Boolean, Map<Integer, Allocation>> excessAndNeededAllocations = allocationsWithAdjustments
        .stream()
        .collect(partitioningBy(p -> p.getDiffAmountAfterTransfer() >= DOUBLE_ZERO,
            toMap(Allocation::getInvestmentCategoryId, Function.identity())));
    // Prepare initial criteria for transfer
    ByPercentTransferCriteria transferCriteria = new ByPercentTransferCriteria(
        excessAndNeededAllocations.get(Boolean.TRUE),
        excessAndNeededAllocations.get(Boolean.FALSE));
    // CalculateTransferAmounts based on diff amounts
    calculateTransferAmounts(excessAndNeededAllocations.get(Boolean.TRUE),
        excessAndNeededAllocations.get(Boolean.FALSE), transferCriteria);

    return allocationsWithAdjustments;
  }

  private List<Allocation> calculateDiffAmounts(List<Allocation> allocations,
      double totalAllocatedAmt, Map<Integer, Map<String, Float>> riskVsCategoryPercentsMap) {
    return allocations.stream().peek(a -> {
      String category = riskVsCategoryPercentsMap.get(a.getInvestmentCategoryId()).keySet().stream()
          .findFirst().get();
      Float percentage = riskVsCategoryPercentsMap.get(a.getInvestmentCategoryId()).values()
          .stream().findFirst().get();
      a.setPercent(percentage);
      a.setInvestmentCategoryName(category);
      calculateAndUpdateDiffAmount(a, totalAllocatedAmt);
    }).collect(toList());
  }

  private void calculateAndUpdateDiffAmount(Allocation allocation, double totalAllocatedAmt) {
    BigDecimal sumOfAllocatedAmt = new BigDecimal(totalAllocatedAmt);
    BigDecimal currentAmt = new BigDecimal(allocation.getAmount());
    if (allocation.getPercent() > DOUBLE_ZERO) {
      BigDecimal percent = new BigDecimal(allocation.getPercent());
      allocation.setDiffAmount(
          sumOfAllocatedAmt.multiply(percent)
              .divide(new BigDecimal(INT_100), SCALE_2, RoundingMode.CEILING).subtract(currentAmt)
              .doubleValue());
      allocation.setDiffAmountAfterTransfer(
          sumOfAllocatedAmt.multiply(percent)
              .divide(new BigDecimal(INT_100), SCALE_2, RoundingMode.CEILING).subtract(currentAmt)
              .doubleValue());
    } else { // Zero percent
      allocation.setDiffAmount(-currentAmt.doubleValue());
      allocation.setDiffAmountAfterTransfer(-currentAmt.doubleValue());
    }
  }

  private void calculateTransferAmounts(Map<Integer, Allocation> neededAllocMap,
      Map<Integer, Allocation> excessAmtsMap, ByPercentTransferCriteria transferCriteria) {
    transferCriteria.getMaxExcessAlloc().ifPresent(excess -> {
      if (!excess.getDiffAmountAfterTransfer().equals(DOUBLE_ZERO)) { // excess amt will be -ve
        transferCriteria.getMaxNeededAlloc().ifPresent(needed -> {
          if (!needed.getDiffAmountAfterTransfer().equals(DOUBLE_ZERO)) { // needed amt will be +ve
            double remainingAmountAfterTransfer =
                excess.getDiffAmountAfterTransfer() + needed.getDiffAmountAfterTransfer();
            if (remainingAmountAfterTransfer < DOUBLE_ZERO) {
              updateAllocationsForAfterTransferNegativeRemainingAmount(excess, needed,
                  remainingAmountAfterTransfer);
            } else {
              updateAllocationsForAfterTransferPositiveRemainingAmount(excess, needed,
                  remainingAmountAfterTransfer);
            }
          }
        });
      }
    });
    // Prepare initial criteria for transfer
    ByPercentTransferCriteria nextTransferCriteria = new ByPercentTransferCriteria(neededAllocMap,
        excessAmtsMap);
    // Check if needMoreTransfers
    nextTransferCriteria.getMaxExcessAlloc().ifPresent(excess -> {
      if (!excess.getDiffAmountAfterTransfer().equals(DOUBLE_ZERO)) { // excess amt will be -ve
        nextTransferCriteria.getMaxNeededAlloc().ifPresent(needed -> {
          if (!needed.getDiffAmountAfterTransfer().equals(DOUBLE_ZERO)) { // needed amt will be +ve
            calculateTransferAmounts(neededAllocMap, excessAmtsMap, nextTransferCriteria);
          }
        });
      }
    });
  }

  private void updateAllocationsForAfterTransferNegativeRemainingAmount(Allocation excess,
      Allocation needed, double remainingAmountAfterTransfer) {
    String transferText = String
        .format("Transfer $%.2f from %s to %s", needed.getDiffAmountAfterTransfer(),
            excess.getInvestmentCategoryName(), needed.getInvestmentCategoryName());
    needed.setTransferDetail(String.join(", ", isNull(needed.getTransferDetail()) ? transferText
        : needed.getTransferDetail() + ", " + transferText));
    needed.setDiffAmountAfterTransfer(DOUBLE_ZERO);
    excess.setDiffAmountAfterTransfer(remainingAmountAfterTransfer);
  }

  private void updateAllocationsForAfterTransferPositiveRemainingAmount(Allocation excess,
      Allocation needed, double remainingAmountAfterTransfer) {
    if (remainingAmountAfterTransfer > DOUBLE_ZERO) {
      String transferText = String
          .format("Transfer $%.2f from %s to %s", abs(excess.getDiffAmountAfterTransfer()),
              excess.getInvestmentCategoryName(), needed.getInvestmentCategoryName());
      needed.setTransferDetail(String.join(", ",
          isNull(needed.getTransferDetail()) ? transferText
              : needed.getTransferDetail() + ", " + transferText));
    }
    needed.setDiffAmountAfterTransfer(remainingAmountAfterTransfer);
    excess.setDiffAmountAfterTransfer(DOUBLE_ZERO);
  }

}
