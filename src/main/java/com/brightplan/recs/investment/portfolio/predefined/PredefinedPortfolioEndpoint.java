package com.brightplan.recs.investment.portfolio.predefined;

import com.brightplan.recs.investment.portfolio.predefined.PredefinedPortfolioPercent.PredefinedPortfolioPercentEntityFieldValidation;
import com.brightplan.recs.investment.portfolio.rebalancing.RebalancePortfolioMethod.RebalanceMethod;
import com.brightplan.recs.investment.portfolio.rebalancing.RebalancingPortfolioService;
import com.brightplan.recs.investment.portfolio.rebalancing.dto.Allocation;
import com.brightplan.recs.investment.portfolio.rebalancing.dto.Allocation.AllocationFieldValidation;
import com.brightplan.recs.logging.Loggable;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/predefined/portfolios")
public class PredefinedPortfolioEndpoint {

  @Autowired
  private PredefinedPortfolioService predefinedPortfolioService;

  @Autowired
  private RebalancingPortfolioService rebalancingPortfolioService;

  @GetMapping
  @Loggable
  public List<PredefinedPortfolioPercent> getAll() {
    return predefinedPortfolioService.getAllPredefinedPortfolioPercents();
  }

  @GetMapping("/matrix")
  @Loggable
  public Map<Integer, List<PredefinedPortfolioPercent>> getRiskVsCategoryPercentsMatrix() {
    return predefinedPortfolioService.getAllPredefinedPortfolioPercentsMatrix();
  }

  @PostMapping
  @Loggable
  public List<PredefinedPortfolioPercent> save(
      @Validated(value = {
          PredefinedPortfolioPercentEntityFieldValidation.class}) @RequestBody List<PredefinedPortfolioPercent> predefinedPortfolioPercents) {
    return predefinedPortfolioService.savePredefinedPortfolioPercents(predefinedPortfolioPercents);
  }

  @PostMapping("/{riskProfileId}/rebalanced")
  @Loggable
  public List<Allocation> rebalance(@PathVariable Integer riskProfileId,
      @Validated(value = {
          AllocationFieldValidation.class}) @RequestBody List<Allocation> allocations) {
    return rebalancingPortfolioService.rebalance(riskProfileId, allocations,
        RebalanceMethod.ForPercent);
  }

}
