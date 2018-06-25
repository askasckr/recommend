package com.brightplan.recs.investment.category;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;

import com.brightplan.recs.RecsApplication;
import com.brightplan.recs.auditing.PersistenceContext;
import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ActiveProfiles("localUnitAndIntTest")
@SpringBootTest(classes = {RecsApplication.class, PersistenceContext.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class InvestmentCategoryRepositoryTest {

  @Autowired
  private InvestmentCategoryRepository investmentCategoryRepository;

  @Test
  public void shouldFindAllInvestmentCategories() {
    assertThat(investmentCategoryRepository.findAll().size(), is(equalTo(0)));
  }

  @Test
  public void shouldSaveInvestmentCategory() {
    String bonds1 = "Bonds";
    InvestmentCategory investmentCategory = InvestmentCategory.builder().name(bonds1)
        .displayOrder(1).build();

    InvestmentCategory savedInvestmentCategory = investmentCategoryRepository
        .save(investmentCategory);
    assertThat(savedInvestmentCategory.getId(), notNullValue());

    Optional<InvestmentCategory> investmentCategoryFound = investmentCategoryRepository
        .findInvestmentCategoryByName(bonds1);
    assertThat(investmentCategoryFound.isPresent(), is(true));
    assertThat(investmentCategoryFound.get().getName(), notNullValue());
    assertThat(investmentCategoryFound.get().getDisplayOrder(), is(equalTo(1)));
  }
}
