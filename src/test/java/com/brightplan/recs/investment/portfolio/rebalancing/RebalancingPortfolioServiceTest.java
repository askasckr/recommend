package com.brightplan.recs.investment.portfolio.rebalancing;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.brightplan.recs.errorhandler.RecsException;
import com.brightplan.recs.investment.portfolio.rebalancing.RebalancePortfolioMethod.RebalanceMethod;
import com.brightplan.recs.investment.portfolio.rebalancing.dto.Allocation;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class RebalancingPortfolioServiceTest {

  @InjectMocks
  private RebalancingPortfolioService rebalancingPortfolioService;

  @Mock
  private RebalancePortfolioByPercentMethod rebalancePortfolioByPercentMethod;

  private Gson gson;

  @Before
  public void setup() {
    gson = new Gson();
  }

  @Test
  public void shouldRebalance() {
    List<Allocation> allocationsInput = stubAllocationsInput();
    List<Allocation> allocationsExpected = stubPostCalculationAllocationsExpected();
    when(rebalancePortfolioByPercentMethod.rebalance(1, allocationsInput))
        .thenReturn(allocationsExpected);
    List<Allocation> allocationsReturned = rebalancingPortfolioService
        .rebalance(1, allocationsInput,
            RebalanceMethod.ForPercent);
    verify(rebalancePortfolioByPercentMethod).rebalance(1, allocationsInput);
    assertThat(allocationsReturned, is(notNullValue()));
    assertThat(allocationsExpected, is(equalTo(allocationsReturned)));
  }

  @Test(expected = RecsException.class)
  public void shouldThrowException_WhenInvalidRebalanceMethod() {
    List<Allocation> allocationsInput = stubAllocationsInput();
    List<Allocation> allocationsExpected = stubPostCalculationAllocationsExpected();
    List<Allocation> allocationsReturned = rebalancingPortfolioService
        .rebalance(1, allocationsInput,
            RebalanceMethod.Unknown);
  }

  private List<Allocation> stubAllocationsInput() {
    String allocationsString = "[\n"
        + "\n"
        + "{\n"
        + "\t\"investmentCategoryId\": 1,\n"
        + "\t\"amount\": 65\n"
        + "},\n"
        + "{\n"
        + "\t\"investmentCategoryId\": 2,\n"
        + "\t\"amount\": 65\n"
        + "},\n"
        + "{\n"
        + "\t\"investmentCategoryId\": 3,\n"
        + "\t\"amount\": 567\n"
        + "},\n"
        + "{\n"
        + "\t\"investmentCategoryId\": 4,\n"
        + "\t\"amount\": 1265\n"
        + "},\n"
        + "{\n"
        + "\t\"investmentCategoryId\": 5,\n"
        + "\t\"amount\": 2465\n"
        + "}\n"
        + "\n"
        + "]";
    Type typeToken = new TypeToken<List<Allocation>>() {
    }.getType();
    return gson.fromJson(allocationsString, typeToken);
  }

  private List<Allocation> stubPostCalculationAllocationsExpected() {
    String allocationsString = "[\n"
        + "    {\n"
        + "        \"amount\": 65,\n"
        + "        \"investmentCategoryId\": 1,\n"
        + "        \"diffAmount\": 156.35,\n"
        + "        \"transferDetail\": \"Transfer $156.35 from Foreign to Bonds\",\n"
        + "        \"investmentCategoryName\": \"Bonds\",\n"
        + "        \"percent\": 5\n"
        + "    },\n"
        + "    {\n"
        + "        \"amount\": 65,\n"
        + "        \"investmentCategoryId\": 2,\n"
        + "        \"diffAmount\": 599.05,\n"
        + "        \"transferDetail\": \"Transfer $597.15 from Small Cap to Large Cap, Transfer $1.90 from Foreign to Large Cap\",\n"
        + "        \"investmentCategoryName\": \"Large Cap\",\n"
        + "        \"percent\": 15\n"
        + "    },\n"
        + "    {\n"
        + "        \"amount\": 567,\n"
        + "        \"investmentCategoryId\": 3,\n"
        + "        \"diffAmount\": 1203.8,\n"
        + "        \"transferDetail\": \"Transfer $1203.80 from Small Cap to Mid Cap\",\n"
        + "        \"investmentCategoryName\": \"Mid Cap\",\n"
        + "        \"percent\": 40\n"
        + "    },\n"
        + "    {\n"
        + "        \"amount\": 1265,\n"
        + "        \"investmentCategoryId\": 4,\n"
        + "        \"diffAmount\": -158.25,\n"
        + "        \"transferDetail\": null,\n"
        + "        \"investmentCategoryName\": \"Foreign\",\n"
        + "        \"percent\": 25\n"
        + "    },\n"
        + "    {\n"
        + "        \"amount\": 2465,\n"
        + "        \"investmentCategoryId\": 5,\n"
        + "        \"diffAmount\": -1800.95,\n"
        + "        \"transferDetail\": null,\n"
        + "        \"investmentCategoryName\": \"Small Cap\",\n"
        + "        \"percent\": 15\n"
        + "    }\n"
        + "]";
    Type typeToken = new TypeToken<List<Allocation>>() {
    }.getType();
    return gson.fromJson(allocationsString, typeToken);
  }
}
