package ru.sparural.engine.aop;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy
@Aspect
public class AopPerformanceMeasureConfiguration {

    @Pointcut(
            "within(ru.sparural.engine.loymax.rest.LoymaxRestClient)"
    )
    public void monitor() {
    }

    @Bean
    public Advisor performanceMonitorAdvisor(CustomPerformanceMonitorInterceptor performanceMonitorInterceptor) {
        var pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("ru.sparural.engine.aop.AopPerformanceMeasureConfiguration.monitor()");
        return new DefaultPointcutAdvisor(pointcut, performanceMonitorInterceptor);
    }
}