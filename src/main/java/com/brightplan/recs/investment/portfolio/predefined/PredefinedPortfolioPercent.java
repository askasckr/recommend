package com.brightplan.recs.investment.portfolio.predefined;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import com.brightplan.recs.auditing.RecsAuditableEntity;
import com.brightplan.recs.investment.category.InvestmentCategory;
import com.brightplan.recs.investment.risk.InvestmentRisk;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.base.Functions;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.groups.Default;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "predefined_portfolio", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"investment_category_id", "investment_risk_id"})})
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@EqualsAndHashCode
@JsonIgnoreProperties(value = {"created", "createdBy", "modified",
    "modifiedBy"}, allowSetters = true)
public class PredefinedPortfolioPercent extends RecsAuditableEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @NotNull(message = "Invalid null investment_category_id", groups = {
      PredefinedPortfolioPercentEntityFieldValidation.class, Default.class})
  @OneToOne
  @JoinColumn(name = "investment_category_id", referencedColumnName = "id", nullable = false, updatable = false)
  @JsonIgnoreProperties(value = {"created", "createdBy", "modified",
      "modifiedBy"}, allowSetters = true)
  private InvestmentCategory investmentCategory;

  @NotNull(message = "Invalid null  investment_risk_id", groups = {
      PredefinedPortfolioPercentEntityFieldValidation.class, Default.class})
  @OneToOne
  @JoinColumn(name = "investment_risk_id", referencedColumnName = "id", nullable = false, updatable = false)
  @JsonIgnoreProperties(value = {"created", "createdBy", "modified",
      "modifiedBy"}, allowSetters = true)
  private InvestmentRisk investmentRisk;

  @NotNull(message = "Percent cannot be null", groups = {
      PredefinedPortfolioPercentEntityFieldValidation.class, Default.class})
  @Max(value = 100, message = "Percent must be greater than 0 or equal to 100", groups = {
      PredefinedPortfolioPercentEntityFieldValidation.class, Default.class})
  @Min(value = 0, message = "Percent must be greater than 0 or equal to 100", groups = {
      PredefinedPortfolioPercentEntityFieldValidation.class, Default.class})
  @Digits(integer = 3, fraction = 3, message = "Percent format can be ddd.fff (3 digits after decimal)", groups = {
      PredefinedPortfolioPercentEntityFieldValidation.class, Default.class})
  @Column(name = "percent")
  private Float percent;

  public static List<PredefinedPortfolioPercent> mergeLists(
      List<PredefinedPortfolioPercent> existingPercents,
      List<PredefinedPortfolioPercent> newPercents) {
    List<PredefinedPortfolioPercent> toInsert = newPercents.stream()
        .filter(x -> isNull(x.getId()))
        .collect(toList());
    Map<Integer, PredefinedPortfolioPercent> toUpdate = newPercents.stream()
        .filter(x -> !isNull(x.getId()))
        .collect(toMap(PredefinedPortfolioPercent::getId, Functions.identity()));
    List<PredefinedPortfolioPercent> toDelete = newPercents.stream()
        .filter(x -> !toUpdate.containsKey(x.getId()))
        .collect(toList());
    existingPercents.removeAll(toDelete);
    existingPercents.forEach(x -> x.merge(toUpdate.get(x.getId())));
    existingPercents.addAll(toInsert);
    return existingPercents;
  }

  private void merge(PredefinedPortfolioPercent predefinedPortfolioPercent) {
    this.percent = predefinedPortfolioPercent.getPercent();
  }

  public interface PredefinedPortfolioPercentEntityFieldValidation {

  }
}
