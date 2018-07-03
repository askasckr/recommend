package com.brightplan.recs.investment.portfolio.predefined;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static org.springframework.util.CollectionUtils.isEmpty;

import com.brightplan.recs.errorhandler.RecsException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class PredefinedPortfolioService {

  private static final Logger log = LoggerFactory.getLogger(PredefinedPortfolioService.class);

  @Autowired
  private PredefinedPortfolioPercentRepository predefinedPortfolioPercentRepository;

  @Autowired
  private PredefinedPortfolioValidation predefinedPortfolioValidation;

  public List<PredefinedPortfolioPercent> getAllPredefinedPortfolioPercents() {
    List<PredefinedPortfolioPercent> investmentRiskVsCategoryPercents = predefinedPortfolioPercentRepository
        .findAll();
    if (isEmpty(investmentRiskVsCategoryPercents)) {
      throw new RecsException(HttpStatus.NOT_FOUND, "None Found");
    }
    return investmentRiskVsCategoryPercents;
  }

  /**
   * TODO: Confirm if the UI needs a sorted matrix. Note: This is an extra effort of grouping and
   * sorting.
   */
  public Map<Integer, List<PredefinedPortfolioPercent>> getAllPredefinedPortfolioPercentsMatrix() {
    List<PredefinedPortfolioPercent> investmentRiskVsCategoryPercents = predefinedPortfolioPercentRepository
        .findAll();
    if (isEmpty(investmentRiskVsCategoryPercents)) {
      throw new RecsException(HttpStatus.NOT_FOUND, "None Found");
    }
    return investmentRiskVsCategoryPercents.stream()
        .collect(groupingBy(pp -> pp.getInvestmentRisk().getLevel(),
            collectingAndThen(toList(),
                p -> p.stream().sorted(comparing(x -> x.getInvestmentCategory().getDisplayOrder()))
                    .collect(toList()))));
  }

  public List<PredefinedPortfolioPercent> getPredefinedPortfolioPercents(Integer riskProfileId) {
    List<PredefinedPortfolioPercent> investmentRiskVsCategoryPercents = predefinedPortfolioPercentRepository
        .findPredefinedPortfolioPercentByInvestmentRisk_Id(riskProfileId);
    if (isEmpty(investmentRiskVsCategoryPercents)) {
      throw new RecsException(HttpStatus.NOT_FOUND, "None Found");
    }
    return investmentRiskVsCategoryPercents;
  }

  public List<PredefinedPortfolioPercent> savePredefinedPortfolioPercents(
      List<PredefinedPortfolioPercent> predefinedPortfolioPercents) {
    List<PredefinedPortfolioPercent> predefinedPortfolioPercentsSaved = predefinedPortfolioPercentRepository
        .saveAll(predefinedPortfolioValidation
            .performPercentsSumUpTo100AndReturnMergedList(predefinedPortfolioPercents));
    return predefinedPortfolioPercentsSaved;
  }

  public void deletePredefinedPortfolioPercents(Optional<List<Integer>> predefinedPortfolioIds) {
    if (predefinedPortfolioIds.isPresent()) {
      predefinedPortfolioIds.get().forEach(i -> {
        predefinedPortfolioPercentRepository.deleteById(i);
      });
    } else {
      predefinedPortfolioPercentRepository.deleteAllInBatch();
    }
  }
}
