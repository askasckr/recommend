package com.brightplan.recs.investment.risk;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface InvestmentRiskRepository extends JpaRepository<InvestmentRisk, Integer> {

  Optional<InvestmentRisk> findInvestmentRiskByLevel(Integer level);
}
