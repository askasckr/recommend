package com.brightplan.recs.auditing;

import com.brightplan.recs.common.RecsBaseEntity;
import java.io.Serializable;
import java.time.ZonedDateTime;
import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Base class for auditing
 */
@MappedSuperclass
@EntityListeners(value = {AuditingEntityListener.class})
public abstract class RecsAuditableEntity extends RecsBaseEntity implements Serializable {

  @NotBlank
  @Length(max = 50, message = "createdBy must not be more than 50 characters")
  @Column(name = "created_by", nullable = false, updatable = false)
  @CreatedBy
  public String createdBy;

  @NotNull
  @Column(name = "created", nullable = false, updatable = false)
  @CreatedDate
  public ZonedDateTime created;

  @Length(max = 50, message = "modifiedBy must be between 1 and 50 characters")
  @Column(name = "modified_by")
  @LastModifiedBy
  public String modifiedBy;

  @Column(name = "modified")
  @LastModifiedDate
  public ZonedDateTime modified;
}
