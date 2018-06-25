package com.brightplan.recs.investment.risk;

import static java.util.Objects.nonNull;

import com.brightplan.recs.errorhandler.RecsException;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class InvestmentRiskService {

  @Autowired
  private InvestmentRiskRepository investmentRiskRepository;

  public InvestmentRisk create(InvestmentRisk investmentRisk) {
    if (nonNull(investmentRisk.getId())) {
      throw new RecsException(HttpStatus.BAD_REQUEST,
          "Could not create investment category with an existing id");
    }
    return investmentRiskRepository.save(investmentRisk);
  }

  public void delete(Optional<List<Integer>> investmentRiskIds) {
    if (investmentRiskIds.isPresent()) {
      investmentRiskIds.get().forEach(i -> {
        investmentRiskRepository.deleteById(i);
      });
    } else {
      investmentRiskRepository.deleteAllInBatch();
    }
  }
}
