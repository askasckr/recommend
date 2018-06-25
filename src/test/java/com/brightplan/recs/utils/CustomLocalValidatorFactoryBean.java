package com.brightplan.recs.utils;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import javax.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.validation.Errors;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

public class CustomLocalValidatorFactoryBean extends LocalValidatorFactoryBean {

  private Logger logger = LoggerFactory.getLogger(this.getClass());

  @Override
  public void validate(@Nullable Object target, Errors errors,
      @Nullable Object... validationHints) {
    Set<Validator> concreteValidators = new LinkedHashSet<>();
    Set<Class<?>> interfaceGroups = new LinkedHashSet<>();
    extractConcreteValidatorsAndInterfaceGroups(concreteValidators, interfaceGroups,
        validationHints);
    proccessConcreteValidators(target, errors, concreteValidators);
    processConstraintViolations(
        super.validate(target, interfaceGroups.toArray(new Class<?>[interfaceGroups.size()])),
        errors);
  }

  private void proccessConcreteValidators(Object target, Errors errors,
      Set<Validator> concreteValidators) {
    for (Validator validator : concreteValidators) {
      validator.validate(target);
    }
  }

  private void extractConcreteValidatorsAndInterfaceGroups(Set<Validator> concreteValidators,
      Set<Class<?>> groups, Object... validationHints) {
    if (validationHints != null) {
      for (Object hint : validationHints) {
        if (hint instanceof Class) {
          if (((Class<?>) hint).isInterface()) {
            groups.add((Class<?>) hint);
          } else {
            Optional<Validator> validatorOptional = getValidatorFromGenericClass(hint);
            if (validatorOptional.isPresent()) {
              concreteValidators.add(validatorOptional.get());
            }
          }
        }
      }
    }
  }

  @SuppressWarnings("unchecked")
  private Optional<Validator> getValidatorFromGenericClass(Object hint) {
    try {
      Class<Validator> clazz = (Class<Validator>) Class.forName(((Class<?>) hint).getName());
      return Optional.of(clazz.newInstance());
    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
      logger.info("There is a problem with the class that you passed to "
          + " @Validated annotation in the controller, we tried to "
          + " cast to org.springframework.validation.Validator and we cant do this");
    }
    return Optional.empty();
  }

}
