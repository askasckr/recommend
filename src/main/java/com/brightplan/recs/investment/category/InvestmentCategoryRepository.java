package com.brightplan.recs.investment.category;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface InvestmentCategoryRepository extends JpaRepository<InvestmentCategory, Integer> {

  Optional<InvestmentCategory> findInvestmentCategoryByName(String name);

}
