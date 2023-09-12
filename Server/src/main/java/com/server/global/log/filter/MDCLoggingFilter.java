package com.server.global.log.filter;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.UUID;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class MDCLoggingFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        UUID uuid = UUID.randomUUID();
        MDC.put("request_id", uuid.toString());
        MDC.put("email", "anonymous");

        long startTime = System.currentTimeMillis();

        chain.doFilter(request, response);

        long endTime = System.currentTimeMillis();

        long duration = endTime - startTime;

        String method = ((HttpServletRequest) request).getMethod();

        String requestFullUri = getRequestFullUri(request);

        String firstHost = getFirstHost(requestFullUri);

        log.info("API : {} {}{} duration: {} ms ([{}])", method, "/", firstHost, duration, requestFullUri);

        MDC.clear();
    }

    private String getRequestFullUri(ServletRequest request) {
        String requestUrl = ((HttpServletRequest) request).getRequestURI();
        String queryString = ((HttpServletRequest) request).getQueryString() == null ?
                "" : ((HttpServletRequest) request).getQueryString();

        return requestUrl + "?" + queryString;
    }

    private String getFirstHost(String requestFullUrl) {

        String[] urlArr = requestFullUrl.split("/");

        String url = "";
        if(urlArr.length > 2){
            url = urlArr[1];
        }
        return url;
    }
}
