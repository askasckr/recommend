package com.brightplan.recs.common;

import java.util.function.Predicate;
import org.springframework.data.jpa.repository.JpaRepository;

public class DataValidator {

  public static boolean validateData(
      JpaRepository<? extends RecsBaseEntity, ? extends Number> jpaRepository,
      Predicate<JpaRepository<? extends RecsBaseEntity, ? extends Number>> predicate) {
    return predicate.test(jpaRepository);
  }


}
