package io.olofsson.springboot.mdcfilter.requestid;

import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.util.UUID;

import static org.springframework.util.StringUtils.hasText;

public class HttpRequestMDCFilter extends OncePerRequestFilter {

    private static final String REQUEST_ID_HEADER_NAME = "X-Request-ID";
    private static final String REQUEST_ID_MDC_KEY = "request_id";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestId = getOrGenerateRequestId(request);
        try {
            MDC.put(REQUEST_ID_MDC_KEY, requestId);
            WrappedResponse wrappedResponse = new WrappedResponse(response);
            wrappedResponse.addHeader(REQUEST_ID_HEADER_NAME, requestId);
            filterChain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }

    private String getOrGenerateRequestId(HttpServletRequest request) {
        String requestId = request.getHeader(REQUEST_ID_HEADER_NAME);
        if (hasText(requestId)) {
            return requestId;
        } else {
            return UUID.randomUUID().toString();
        }
    }

    private static class WrappedResponse extends HttpServletResponseWrapper {
        WrappedResponse(HttpServletResponse response) {
            super(response);
        }
    }
}