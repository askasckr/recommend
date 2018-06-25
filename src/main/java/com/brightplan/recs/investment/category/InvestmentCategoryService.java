package com.brightplan.recs.investment.category;

import static java.util.Objects.nonNull;

import com.brightplan.recs.errorhandler.RecsException;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class InvestmentCategoryService {

  @Autowired
  private InvestmentCategoryRepository investmentCategoryRepository;

  public InvestmentCategory create(InvestmentCategory investmentCategory) {
    if (nonNull(investmentCategory.getId())) {
      throw new RecsException(HttpStatus.BAD_REQUEST,
          "Could not create investment category with an existing id");
    }
    return investmentCategoryRepository.save(investmentCategory);
  }

  public void delete(Optional<List<Integer>> investmentCategoryIds) {
    if (investmentCategoryIds.isPresent()) {
      investmentCategoryIds.get().forEach(i -> {
        investmentCategoryRepository.deleteById(i);
      });
    } else {
      investmentCategoryRepository.deleteAllInBatch();
    }
  }
}
