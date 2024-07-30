package ru.sparural.rest.filter;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.sparural.kafka.KafkaSparuralBaseConfig;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

@Component
public class MDCKafkaTraceIdFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var traceId = UUID.randomUUID().toString();
        MDC.put(KafkaSparuralBaseConfig.MDC_TRACE_ID_KEY, traceId);
        response.setHeader("X-TRACE-ID", traceId);
        filterChain.doFilter(request, response);
    }
}
