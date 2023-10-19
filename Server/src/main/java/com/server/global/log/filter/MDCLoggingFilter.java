package com.server.global.log.filter;

import com.server.global.initailizer.warmup.WarmupState;
import com.sun.management.OperatingSystemMXBean;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.UUID;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
@Slf4j
public class MDCLoggingFilter implements Filter {

    private final WarmupState warmupState;

    public MDCLoggingFilter(WarmupState warmupState) {
        this.warmupState = warmupState;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        UUID uuid = UUID.randomUUID();
        MDC.put("request_id", uuid.toString());
        MDC.put("email", "anonymous");

        long startTime = System.currentTimeMillis();

        if(warmupState.isWarmupCompleted()) {
            OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
            double sysCpuLoad = osBean.getSystemCpuLoad() * 100;

            log.info("Thread Count : {}, CPU usage : {}%", Thread.activeCount(), sysCpuLoad);
        }


        chain.doFilter(request, response);

        long endTime = System.currentTimeMillis();

        long duration = endTime - startTime;

        String method = ((HttpServletRequest) request).getMethod();

        String requestFullUri = getRequestFullUri(request);

        String firstHost = getFirstHost(request);

        if(warmupState.isWarmupCompleted()){
            log.info("API : {} {}{} duration: {} ms ([{}])", method, "/", firstHost, duration, requestFullUri);
        }

        MDC.clear();
    }

    private String getRequestFullUri(ServletRequest request) {
        String requestUrl = ((HttpServletRequest) request).getRequestURI();
        String queryString = ((HttpServletRequest) request).getQueryString() == null ?
                "" : "?" + ((HttpServletRequest) request).getQueryString();

        return requestUrl + queryString;
    }

    private String getFirstHost(ServletRequest request) {

        String requestUrl = ((HttpServletRequest) request).getRequestURI();

        String[] urlArr = requestUrl.split("/");

        String url = "";
        if(urlArr.length > 1){
            url = urlArr[1];
        }
        return url;
    }
}
