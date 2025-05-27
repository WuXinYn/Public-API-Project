package com.wxy.api.gateway.config;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * 自定义异步配置
 */
@Configuration
public class AsyncConfig implements AsyncConfigurer
{
    /**
     * 配置自定义线程池
     * @return
     */
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5); // 核心线程数
        executor.setMaxPoolSize(10); // 最大线程数
        executor.setQueueCapacity(25); // 队列容量
        executor.setThreadNamePrefix("AsyncExecutor-"); // 线程名前缀
        executor.setKeepAliveSeconds(60); // 空闲线程存活时间
        executor.setWaitForTasksToCompleteOnShutdown(true); // 关闭时等待任务完成
        executor.setAwaitTerminationSeconds(30); // 最大等待时间
        executor.initialize(); // 初始化线程池
        return executor;
    }

    /**
     * 配置异步任务未捕获异常的处理
     * @return
     */
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (ex, method, params) -> {
            System.err.println("Exception in async method: " + method.getName());
            ex.printStackTrace();
        };
    }
}

