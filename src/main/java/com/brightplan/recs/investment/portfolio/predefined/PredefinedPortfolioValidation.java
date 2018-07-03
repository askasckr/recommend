package com.brightplan.recs.investment.portfolio.predefined;

import static com.brightplan.recs.common.RecsConstant.DOUBLE_100;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.joining;

import com.brightplan.recs.errorhandler.RecsException;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class PredefinedPortfolioValidation {

  private static final Logger log = LoggerFactory.getLogger(PredefinedPortfolioValidation.class);

  @Autowired
  private PredefinedPortfolioPercentRepository predefinedPortfolioPercentRepository;

  public List<PredefinedPortfolioPercent> performPercentsSumUpTo100AndReturnMergedList(
      List<PredefinedPortfolioPercent> predefinedPortfolioPercents) {
    return predefinedPortfolioPercents.stream()
        .collect(groupingBy(p -> new SimpleEntry<>(p.getInvestmentRisk().getId(),
            p.getInvestmentRisk().getLevel())))
        .entrySet().stream().map(
            r -> {
              List<PredefinedPortfolioPercent> suppliedPercents = r.getValue();
              // 1. Check the sum of percents within supplied list
              double sumOfSuppliedPercents = suppliedPercents.stream()
                  .mapToDouble(PredefinedPortfolioPercent::getPercent)
                  .sum();
              if (DOUBLE_100 != sumOfSuppliedPercents) {
                throw new RecsException(HttpStatus.BAD_REQUEST, String
                    .format(
                        "Supplied Percents for Risk(id = %d, level = %d) ARE NOT summing up to 100: (%s) = %.2f",
                        r.getKey().getKey(), r.getKey().getValue(),
                        predefinedPortfolioPercents.stream()
                            .map(pp -> String.valueOf(pp.getPercent()))
                            .collect(joining(" + ")), sumOfSuppliedPercents));
              }
              // Get the merged list from existing and suppliedPercents
              List<PredefinedPortfolioPercent> mergedPercents = PredefinedPortfolioPercent
                  .mergeLists(
                      predefinedPortfolioPercentRepository
                          .findPredefinedPortfolioPercentByInvestmentRisk_Id(r.getKey().getKey()),
                      suppliedPercents);
              // 1. Check the sum of percents within supplied list
              double sumOfMergedPercents = mergedPercents.stream()
                  .mapToDouble(PredefinedPortfolioPercent::getPercent)
                  .sum();
              if (DOUBLE_100 != sumOfMergedPercents) {
                throw new RecsException(HttpStatus.BAD_REQUEST, String
                    .format(
                        "Merged Percents for Risk(id = %d, level = %d) ARE NOT summing up to 100: (%s) = %.2f",
                        r.getKey().getKey(), r.getKey().getValue(),
                        predefinedPortfolioPercents.stream()
                            .map(pp -> String.valueOf(pp.getPercent()))
                            .collect(joining(" + ")), sumOfMergedPercents));
              }
              return mergedPercents;
            }).flatMap(List::stream).collect(Collectors.toList());
  }

  //  public static boolean missingCategoryPercentsCheck(
//      JpaRepository<? extends RecsBaseEntity, ? extends Number> predefinedPortfolioPercentRepository) {
//    long missingCategoryPercentsCount = ((PredefinedPortfolioPercentRepository) predefinedPortfolioPercentRepository)
//        .findMissingCategoryCount();
//    if (missingCategoryPercentsCount > 0) {
//      log.error("missingCategoryPercentsCount: {}", missingCategoryPercentsCount);
//      throw new RecsException(HttpStatus.INTERNAL_SERVER_ERROR, "Encountered data inconsistency");
//    }
//    return true;
//  }
}
