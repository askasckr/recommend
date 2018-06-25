package com.brightplan.recs.investment.risk;

import com.brightplan.recs.auditing.RecsAuditableEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
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
@Table(name = "investment_risk", schema = "lookup")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@EqualsAndHashCode
public class InvestmentRisk extends RecsAuditableEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @NotNull(groups = {InvestmentRiskValidation.class, Default.class})
  @Max(value = 10, groups = {InvestmentRiskValidation.class, Default.class})
  @Min(value = 1, groups = {InvestmentRiskValidation.class, Default.class})
  @Column(name = "level", unique = true)
  private Integer level;

  @Column(name = "info")
  private String info;

  public interface InvestmentRiskValidation {

  }

}
