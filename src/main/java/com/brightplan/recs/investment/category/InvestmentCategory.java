package com.brightplan.recs.investment.category;

import com.brightplan.recs.auditing.RecsAuditableEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.groups.Default;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Entity
@Table(name = "investment_category", schema = "lookup")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@EqualsAndHashCode
public class InvestmentCategory extends RecsAuditableEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @NotBlank(groups = {InvestmentCategoryValidation.class, Default.class})
  @Length(max = 20, message = "createdBy must not be more than 20 characters", groups = {
      InvestmentCategoryValidation.class, Default.class})
  @Column(name = "name", unique = true)
  private String name;

  @NotNull(groups = {InvestmentCategoryValidation.class, Default.class})
  @Column(name = "display_order", unique = true)
  private Integer displayOrder;

  @Column(name = "info")
  private String info;

  public interface InvestmentCategoryValidation {

  }

}
