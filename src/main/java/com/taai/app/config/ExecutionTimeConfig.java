package com.taai.app.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Aspect
@Configuration
public class ExecutionTimeConfig {
    @Around("@annotation(ExecutionTime)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        var begin = System.currentTimeMillis();
        var rs = joinPoint.proceed();
        var end = System.currentTimeMillis() - begin;
        log.info("{} finished in {} ms", joinPoint.getSignature().toShortString(), end);
        return rs;
    }
}