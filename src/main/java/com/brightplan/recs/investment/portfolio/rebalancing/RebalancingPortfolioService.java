package com.brightplan.recs.investment.portfolio.rebalancing;

import static com.brightplan.recs.investment.portfolio.rebalancing.RebalancePortfolioMethod.RebalanceMethod.ForPercent;
import static java.util.Objects.isNull;

import com.brightplan.recs.errorhandler.RecsException;
import com.brightplan.recs.investment.portfolio.rebalancing.RebalancePortfolioMethod.RebalanceMethod;
import com.brightplan.recs.investment.portfolio.rebalancing.dto.Allocation;
import java.util.List;
import java.util.function.BiFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class RebalancingPortfolioService {

  private static final Logger log = LoggerFactory.getLogger(RebalancingPortfolioService.class);

  @Autowired
  private RebalancePortfolioByPercentMethod rebalancePortfolioByPercentMethod;

  public List<Allocation> rebalance(Integer riskProfileId,
      List<Allocation> allocations, RebalanceMethod rebalanceMethod) {

    return performRebalance(riskProfileId, allocations,
        getMethod(isNull(rebalanceMethod) ? ForPercent : rebalanceMethod)::rebalance);
  }

  private List<Allocation> performRebalance(Integer riskProfileId,
      List<Allocation> allocations,
      BiFunction<Integer, List<Allocation>, List<Allocation>> rebalancingMethod) {
    return rebalancingMethod.apply(riskProfileId, allocations);
  }

  private RebalancePortfolioMethod getMethod(RebalanceMethod rebalanceMethod) {
    switch (rebalanceMethod) {
      case ForPercent:
        return rebalancePortfolioByPercentMethod;
      default:
        throw new RecsException(HttpStatus.BAD_REQUEST, "Unsupported re-balance method");
    }
  }
}
