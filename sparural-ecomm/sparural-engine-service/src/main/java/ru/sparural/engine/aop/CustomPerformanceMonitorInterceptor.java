package ru.sparural.engine.aop;

import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.interceptor.PerformanceMonitorInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

/**
 * @author Vorobyev Vyacheslav
 */
@Component
@Slf4j
public class CustomPerformanceMonitorInterceptor extends PerformanceMonitorInterceptor {
    @Value("${performance_log.enabled}")
    private Boolean perfLogEnabled;

    public CustomPerformanceMonitorInterceptor() {
        setUseDynamicLogger(false);
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        if (perfLogEnabled) {
            var methodName = createInvocationTraceName(invocation);
            var stopWatch = new StopWatch(methodName);
            stopWatch.start(methodName);
            try {
                return invocation.proceed();
            } finally {
                stopWatch.stop();
                var time = stopWatch.getLastTaskTimeMillis();
                log.info("PERFORMANCE LOG: {} : total time: {} ms", methodName, time);
            }
        } else {
            return invocation.proceed();
        }
    }
}