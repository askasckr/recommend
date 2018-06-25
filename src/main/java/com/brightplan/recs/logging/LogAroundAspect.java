package com.brightplan.recs.logging;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Aspect
public class LogAroundAspect {

  private static Logger LOG = LoggerFactory.getLogger(LogAroundAspect.class);

  @Around("@annotation(com.brightplan.recs.logging.Loggable)")
  public Object logAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
    String methodName = extractMethodName(proceedingJoinPoint);
    String className = extractClassName(proceedingJoinPoint);
    List<Object> parameters = Lists.newArrayList(proceedingJoinPoint.getArgs());

    logRequestEntry(className, methodName);
    logParameters(parameters);

    Object output = proceedingJoinPoint.proceed();

    logReturnSize(output);
    logReturnOutput(output);
    logRequestReturn(className, methodName);

    return output;
  }

  @Around("@annotation(com.brightplan.recs.logging.LoggableNoOutput)")
  public Object logAroundWithNoOutput(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
    String methodName = extractMethodName(proceedingJoinPoint);
    String className = extractClassName(proceedingJoinPoint);
    List<Object> parameters = Lists.newArrayList(proceedingJoinPoint.getArgs());

    logRequestEntry(className, methodName);
    logParameters(parameters);

    Object output = proceedingJoinPoint.proceed();

    logReturnSize(output);
    logRequestReturn(className, methodName);

    return output;
  }

  private void logRequestEntry(String className, String methodName) {
    LOG.info("request start entry point is " + className + "/" + methodName);
  }

  private void logRequestReturn(String className, String methodName) {
    LOG.info("request returned from " + className + "/" + methodName);
  }

  private void logReturnSize(Object output) {
    if (output instanceof ArrayList) {
      LOG.info("result is an instance of ArrayList");
      List<Object> arrayListObject = (ArrayList<Object>) output;
      LOG.info("returning " + arrayListObject.size() + " results");
    }
    if (output instanceof Map) {
      LOG.info("result is an instance of Map");
      Map<Object, Object> mapObject = (Map<Object, Object>) output;
      LOG.info("returning " + mapObject.size() + " results");
    } else {
      if (output != null) {
        LOG.info("output object is of type " + output.getClass());
      }
    }
  }

  private void logReturnOutput(Object output) {
    LOG.debug("output is: " + output);
  }

  private void logParameters(List<Object> parameters) {
    LOG.info("parameters are: " + parameters);
  }

  private String extractClassName(ProceedingJoinPoint proceedingJoinPoint) {
    MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
    return signature.getMethod().getDeclaringClass().getName();
  }

  public String extractMethodName(ProceedingJoinPoint proceedingJoinPoint) {
    MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
    return signature.getMethod().getName();
  }
}
