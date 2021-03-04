package com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.spi_and_aop.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
@Aspect
@Component
public class MainAspect {
    @Pointcut("within(com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.spi_and_aop.service.*) || " +
              "within(com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.jpa_repository.*)")
    public void callAtMyService() {
    }
    @Before("callAtMyService()")
    public void beforeCallAtMethod1(JoinPoint joinPoint) {
        String args = Arrays
                .stream(joinPoint.getArgs())
                .map(Object::toString)
                .collect(Collectors.joining(","));
        LOGGER.info("before " + joinPoint.toString() + ", args=[" + args + "]");
    }

    @After("callAtMyService()")
    public void afterCallAt(JoinPoint jp) {
        LOGGER.info("after " + jp.toString());
    }

    @AfterThrowing(pointcut = "within(com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.spi_and_aop.service.*) || " +
            "within(com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.jpa_repository.*)",
            throwing = "exception")
    public void logAfterThrowingAllMethods(Exception exception) throws Throwable {
        LOGGER.error("Exception: ", exception);
    }

}
