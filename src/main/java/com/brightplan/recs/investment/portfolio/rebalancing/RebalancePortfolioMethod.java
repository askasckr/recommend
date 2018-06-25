package com.brightplan.recs.investment.portfolio.rebalancing;

import com.brightplan.recs.investment.portfolio.rebalancing.dto.Allocation;
import java.util.List;

public interface RebalancePortfolioMethod {

  List<Allocation> rebalance(Integer riskProfileId,
      List<Allocation> allocations);
  // List<Allocation> allocations, PredefinedPortfolioService predefinedPortfolioService);

  static enum RebalanceMethod {
    ForPercent,
    Unknown
  }
}
