package com.brightplan.recs.investment.portfolio.predefined;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
interface PredefinedPortfolioPercentRepository extends
    JpaRepository<PredefinedPortfolioPercent, Integer> {

  @Query("select count(p) from PredefinedPortfolioPercent p left join fetch InvestmentCategory ic on (p.investmentCategory = ic) where p.investmentCategory is null")
  Long findMissingCategoryCount();

  List<PredefinedPortfolioPercent> findPredefinedPortfolioPercentByInvestmentRisk_Id(
      Integer investmentRiskId);
}
