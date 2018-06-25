package com.brightplan.recs.investment.portfolio.predefined;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.brightplan.recs.investment.category.InvestmentCategory;
import com.brightplan.recs.investment.portfolio.rebalancing.RebalancePortfolioMethod.RebalanceMethod;
import com.brightplan.recs.investment.portfolio.rebalancing.RebalancingPortfolioService;
import com.brightplan.recs.investment.portfolio.rebalancing.dto.Allocation;
import com.brightplan.recs.investment.risk.InvestmentRisk;
import com.brightplan.recs.utils.BaseEndpointTest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;

@RunWith(MockitoJUnitRunner.class)
public class PredefinedPortfolioEndpointTest extends BaseEndpointTest<PredefinedPortfolioEndpoint> {

  @Mock
  private RebalancingPortfolioService rebalancingPortfolioService;

  @Mock
  private PredefinedPortfolioService predefinedPortfolioService;

  @InjectMocks
  private PredefinedPortfolioEndpoint predefinedPortfolioEndpoint;

  @Captor
  private ArgumentCaptor<List<PredefinedPortfolioPercent>> listPercentArgumentCaptor;

  @Captor
  private ArgumentCaptor<List<Allocation>> listAllocationArgumentCaptor;

  @Captor
  private ArgumentCaptor<Integer> intCaptor;

  @Captor
  private ArgumentCaptor<RebalanceMethod> rebalanceMethodArgumentCaptor;

  @Test
  public void shouldReturnInvestmentRiskAndCategoryPercents() throws Exception {
    List<PredefinedPortfolioPercent> percentsExpected = stubGeneratePredefinedPortfolioPercents(
        true);
    when(predefinedPortfolioService.getAllPredefinedPortfolioPercents())
        .thenReturn(percentsExpected);
    mockMvc.perform(
        get("/api/v1/predefined/portfolios").accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .header("Client-Id", "test"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
        .andDo(result -> {
          try {
            // Extract the results and check the size and compare with expected result
            List<PredefinedPortfolioPercent> results = objectMapper
                .readValue(result.getResponse().getContentAsString(),
                    new TypeReference<ArrayList<PredefinedPortfolioPercent>>() {
                    });
            assertThat(results, is(notNullValue()));
            assertThat(results.size(), is(greaterThan(0)));
            assertThat(results, is(percentsExpected));
          } catch (IOException e) {
            fail("Response could not be deserialized: " + e.getMessage());
          }
        });
    verify(predefinedPortfolioService).getAllPredefinedPortfolioPercents();
  }

  @Test
  public void shouldReturnInvestmentRiskAndCategoryPercentsMatrix() throws Exception {
    Map<Integer, List<PredefinedPortfolioPercent>> percentsExpected = stubGeneratePredefinedPortfolioPercentsMatrix(
        true);
    when(predefinedPortfolioService.getAllPredefinedPortfolioPercentsMatrix())
        .thenReturn(percentsExpected);
    mockMvc.perform(
        get("/api/v1/predefined/portfolios/matrix").accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .header("Client-Id", "test"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
        .andDo(result -> {
          try {
            // Extract the results and check the size and compare with expected result
            Map<Integer, List<PredefinedPortfolioPercent>> results = objectMapper
                .readValue(result.getResponse().getContentAsString(),
                    new TypeReference<Map<Integer, List<PredefinedPortfolioPercent>>>() {
                    });
            assertThat(results, is(notNullValue()));
            assertThat(results.size(), is(greaterThan(0)));
            assertThat(results, is(percentsExpected));
          } catch (IOException e) {
            fail("Response could not be deserialized: " + e.getMessage());
          }
        });
    verify(predefinedPortfolioService).getAllPredefinedPortfolioPercentsMatrix();
  }

  @Test
  public void shouldSavePredifinedPortfolioWithValidPredefinedPortfolioPercents()
      throws Exception {
    List<PredefinedPortfolioPercent> percentsInput = stubGeneratePredefinedPortfolioPercents(false);
    List<PredefinedPortfolioPercent> percentsExpected = stubGeneratePredefinedPortfolioPercents(
        true);
    when(predefinedPortfolioService.savePredefinedPortfolioPercents(percentsInput))
        .thenReturn(percentsExpected);
    mockMvc.perform(post("/api/v1/predefined/portfolios").accept(MediaType.APPLICATION_JSON_VALUE)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .header("Client-Id", "test")
        .content(objectMapper.writeValueAsString(percentsInput)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
        .andDo(result -> {
          try {
            // Extract the results and check the size and compare with expected result
            List<PredefinedPortfolioPercent> results = objectMapper
                .readValue(result.getResponse().getContentAsString(),
                    new TypeReference<ArrayList<PredefinedPortfolioPercent>>() {
                    });
            assertThat(percentsInput.size(), is(equalTo(results.size())));
            assertThat(results, is(percentsExpected));
          } catch (IOException e) {
            fail("Response could not be deserialized: " + e.getMessage());
          }
        });
    // Check the argument captor
    verify(predefinedPortfolioService)
        .savePredefinedPortfolioPercents(listPercentArgumentCaptor.capture());
    assertThat(listPercentArgumentCaptor.getValue(), is(percentsInput));
  }

  @Test
  public void shouldRebalancePredifinedPortfolio()
      throws Exception {
    List<Allocation> allocationsInput = stubAllocations();
    List<Allocation> allocationsExpected = stubPostRebalancedAllocationsExpected();
    when(rebalancingPortfolioService.rebalance(1, allocationsInput, RebalanceMethod.ForPercent))
        .thenReturn(allocationsExpected);

    mockMvc.perform(
        post("/api/v1/predefined/portfolios/1/rebalanced").accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .header("Client-Id", "test")
            .content(objectMapper.writeValueAsString(allocationsInput)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
        .andDo(result -> {
          try {
            // Extract the results and check the size and compare with expected result
            List<Allocation> results = objectMapper
                .readValue(result.getResponse().getContentAsString(),
                    new TypeReference<ArrayList<Allocation>>() {
                    });
            assertThat(allocationsInput.size(), is(equalTo(results.size())));
            assertThat(results, is(allocationsExpected));
          } catch (IOException e) {
            fail("Response could not be deserialized: " + e.getMessage());
          }
        });
    // Check the argument captor
    verify(rebalancingPortfolioService)
        .rebalance(eq(1), listAllocationArgumentCaptor.capture(), eq(RebalanceMethod.ForPercent));
    assertThat(listAllocationArgumentCaptor.getValue(), is(allocationsInput));
  }

  private List<PredefinedPortfolioPercent> stubGeneratePredefinedPortfolioPercents(
      boolean withIds) {
    String[] investmentCategories = {"Bonds", "Large Cap", "Mid Cap", "Foreign", "Small Cap"};
    return Stream.iterate(1, n -> n + 1).limit(10).map(r -> {
      List<PredefinedPortfolioPercent> percents = Lists.newArrayList();
      int i = 1;
      for (String category : investmentCategories) {
        percents.add(
            PredefinedPortfolioPercent.builder().id(withIds ? r : null)
                .percent(new BigDecimal(100 / investmentCategories.length).floatValue())
                .investmentRisk(InvestmentRisk.builder().id(r).level(r).build())
                .investmentCategory(InvestmentCategory.builder().id(i).name(category)
                    .displayOrder(i)
                    .build()).build());
      }
      return percents;

    }).flatMap(List::stream).collect(toList());

  }

  private Map<Integer, List<PredefinedPortfolioPercent>> stubGeneratePredefinedPortfolioPercentsMatrix(
      boolean withIds) {
    String[] investmentCategories = {"Bonds", "Large Cap", "Mid Cap", "Foreign", "Small Cap"};
    return Stream.iterate(1, n -> n + 1).limit(10).map(r -> {
      List<PredefinedPortfolioPercent> percents = Lists.newArrayList();
      int i = 1;
      for (String category : investmentCategories) {
        percents.add(
            PredefinedPortfolioPercent.builder().id(withIds ? r : null)
                .percent(new BigDecimal(100 / investmentCategories.length).floatValue())
                .investmentRisk(InvestmentRisk.builder().id(r).level(r).build())
                .investmentCategory(InvestmentCategory.builder().id(i).name(category)
                    .displayOrder(i)
                    .build()).build());
      }

      return percents;

    }).flatMap(List::stream).collect(groupingBy(pp -> pp.getInvestmentRisk().getLevel(),
        collectingAndThen(toList(),
            p -> p.stream().sorted(comparing(x -> x.getInvestmentCategory().getDisplayOrder()))
                .collect(toList()))));
  }

  private List<PredefinedPortfolioPercent> stubInvalidPredefinedPortfolioPercentsWithMoreThan100Percent()
      throws IOException {
    String percentsString = " [\n"
        + "        {\n"
        + "            \"id\": 1,\n"
        + "            \"investmentCategory\": {\n"
        + "                \"id\": 2,\n"
        + "                \"name\": \"Bonds\",\n"
        + "                \"displayOrder\": 0,\n"
        + "                \"info\": null\n"
        + "            },\n"
        + "            \"investmentRisk\": {\n"
        + "                \"id\": 1,\n"
        + "                \"level\": 1,\n"
        + "                \"info\": null\n"
        + "            },\n"
        + "            \"percent\": 180\n"
        + "        },\n"
        + "        {\n"
        + "            \"id\": 2,\n"
        + "            \"investmentCategory\": {\n"
        + "                \"id\": 3,\n"
        + "                \"name\": \"Large Cap\",\n"
        + "                \"displayOrder\": 1,\n"
        + "                \"info\": null\n"
        + "            },\n"
        + "            \"investmentRisk\": {\n"
        + "                \"id\": 1,\n"
        + "                \"level\": 1,\n"
        + "                \"info\": null\n"
        + "            },\n"
        + "            \"percent\": 20\n"
        + "        },\n"
        + "        {\n"
        + "            \"id\": 3,\n"
        + "            \"investmentCategory\": {\n"
        + "                \"id\": 4,\n"
        + "                \"name\": \"Mid Cap\",\n"
        + "                \"displayOrder\": 2,\n"
        + "                \"info\": null\n"
        + "            },\n"
        + "            \"investmentRisk\": {\n"
        + "                \"id\": 1,\n"
        + "                \"level\": 1,\n"
        + "                \"info\": null\n"
        + "            },\n"
        + "            \"percent\": 0\n"
        + "        },\n"
        + "        {\n"
        + "            \"id\": 4,\n"
        + "            \"investmentCategory\": {\n"
        + "                \"id\": 5,\n"
        + "                \"name\": \"Foreign\",\n"
        + "                \"displayOrder\": 3,\n"
        + "                \"info\": null\n"
        + "            },\n"
        + "            \"investmentRisk\": {\n"
        + "                \"id\": 1,\n"
        + "                \"level\": 1,\n"
        + "                \"info\": null\n"
        + "            },\n"
        + "            \"percent\": 0\n"
        + "        },\n"
        + "        {\n"
        + "            \"id\": 5,\n"
        + "            \"investmentCategory\": {\n"
        + "                \"id\": 6,\n"
        + "                \"name\": \"Small Cap\",\n"
        + "                \"displayOrder\": 4,\n"
        + "                \"info\": null\n"
        + "            },\n"
        + "            \"investmentRisk\": {\n"
        + "                \"id\": 1,\n"
        + "                \"level\": 1,\n"
        + "                \"info\": null\n"
        + "            },\n"
        + "            \"percent\": 0\n"
        + "        }\n"
        + "    ]";

    return objectMapper
        .readValue(percentsString,
            new TypeReference<ArrayList<PredefinedPortfolioPercent>>() {
            });
  }

  private List<Allocation> stubAllocations() throws IOException {
    Gson gson = new Gson();
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
    return objectMapper
        .readValue(allocationsString,
            new TypeReference<ArrayList<Allocation>>() {
            });
  }

  private List<Allocation> stubPostRebalancedAllocationsExpected() throws IOException {
    Gson gson = new Gson();
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
    return objectMapper
        .readValue(allocationsString,
            new TypeReference<ArrayList<Allocation>>() {
            });
  }
}
