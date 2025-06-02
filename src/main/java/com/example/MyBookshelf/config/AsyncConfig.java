package com.example.MyBookshelf.config;

import org.springframework.context.annotation.*;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.concurrent.DelegatingSecurityContextExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean("taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor delegate = new ThreadPoolTaskExecutor();
        delegate.setCorePoolSize(5);
        delegate.setMaxPoolSize(10);
        delegate.setQueueCapacity(100);
        delegate.setThreadNamePrefix("BookAsync-");
        delegate.initialize();

        return new DelegatingSecurityContextExecutor(delegate);
    }
}
