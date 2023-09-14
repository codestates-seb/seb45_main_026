package com.server.global.initailizer.warmup;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.server.global.reponse.ApiSingleResponse;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
@Profile("prod")
@Order(Ordered.HIGHEST_PRECEDENCE)
public class WarmupFilter implements Filter {

    private final WarmupState warmupState;
    private final ObjectMapper objectMapper;

    public WarmupFilter(WarmupState warmupState, ObjectMapper objectMapper) {
        this.warmupState = warmupState;
        this.objectMapper = objectMapper;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {


        if (!warmupState.isWarmupCompleted()) {

            HttpServletRequest httpRequest = (HttpServletRequest) request;

            if (!isWarmupRequest(httpRequest)) {

                setRejectResponse(response);
                return;
            }
        }

        chain.doFilter(request, response);
    }

    private boolean isWarmupRequest(HttpServletRequest request) {

        String warmup = request.getHeader("warmup");

        return "true".equals(warmup);
    }

    private void setRejectResponse(ServletResponse response) throws IOException {

        ApiSingleResponse<String> apiSingleResponse = ApiSingleResponse.of(
                "warmup",
                HttpStatus.SERVICE_UNAVAILABLE,
                "서버가 초기화되고 있습니다. 잠시 후 다시 시도해주세요.");

        String apiResponse = objectMapper.writeValueAsString(apiSingleResponse);

        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(apiResponse);
    }
}
