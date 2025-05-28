package com.example.MyBookshelf.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.*;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Pointcut("within(com.example.MyBookshelf.service..*) || within(com.example.MyBookshelf.controller..*)")
    public void appMethods() {}

    @Around("appMethods()")
    public Object logMethod(ProceedingJoinPoint pjp) throws Throwable {
        MethodSignature sig = (MethodSignature) pjp.getSignature();
        String className = sig.getDeclaringType().getSimpleName();
        String method = sig.getName();
        Object[] args = pjp.getArgs();

        log.info("▶ Entering {}.{} with args={}", className, method, args);

        long start = System.currentTimeMillis();
        try {
            Object result = pjp.proceed();
            long duration = System.currentTimeMillis() - start;
            log.info("✔ Exiting {}.{}; returned={} ({} ms)", className, method, result, duration);
            return result;
        } catch (Throwable t) {
            long duration = System.currentTimeMillis() - start;
            log.error("✖ Exception in {}.{} after {} ms: {}", className, method, duration, t.toString());
            throw t;
        }
    }
}
