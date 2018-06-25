package com.brightplan.recs.investment.portfolio.rebalancing.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.groups.Default;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"diffAmountAfterTransfer"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class Allocation {

  @NotNull(groups = {AllocationFieldValidation.class, Default.class})
  @PositiveOrZero(message = "Amount must be zero or greater", groups = {
      AllocationFieldValidation.class, Default.class})
  private Double amount;

  @NotNull(groups = {AllocationFieldValidation.class, Default.class})
  @Positive(message = "Invalid -ve investment category id", groups = {
      AllocationFieldValidation.class, Default.class})
  private Integer investmentCategoryId;

  private Double diffAmount;

  private String transferDetail;

  @JsonIgnore
  private Double diffAmountAfterTransfer;

  private String investmentCategoryName;

  private float percent;

  public interface AllocationFieldValidation {

  }
}
