package io.olofsson.springboot.mdcfilter.requestid;

import org.apache.log4j.BasicConfigurator;
import org.junit.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class HttpRequestMDCFilterTest {

    static {
        BasicConfigurator.configure();
    }

    private static final String REQUEST_ID_HEADER = "X-Request-ID";
    private static final String TEST_REQUEST_ID = "Some-Request-Id";
    private static final String REQUEST_ID_MDC_KEY = "request_id";

    private final MockHttpServletRequest request = new MockHttpServletRequest();
    private final MockHttpServletResponse response = new MockHttpServletResponse();
    private final MdcRequestIdCapturingFilterChain capturingFilterChain = new MdcRequestIdCapturingFilterChain();

    private final HttpRequestMDCFilter filter = new HttpRequestMDCFilter();

    @Test
    public void shouldSetRandomUuidMdcRequestIdWhenNoHeaderPresent() throws Exception {
        filter.doFilter(request, response, capturingFilterChain);

        assertValidUuid(capturingFilterChain.getMdcRequestId());
        assertRequestIdPresentInResponse(capturingFilterChain.getMdcRequestId());
    }

    @Test
    public void shouldSetMdcRequestIdFromHeaderWhenPresent() throws Exception {
        request.addHeader("X-Request-ID", TEST_REQUEST_ID);

        filter.doFilter(request, response, capturingFilterChain);

        assertThat(capturingFilterChain.getMdcRequestId(), is(TEST_REQUEST_ID));
        assertRequestIdPresentInResponse(TEST_REQUEST_ID);
    }

    @Test
    public void shouldClearMDCWhenDone() throws Exception {
        filter.doFilter(request, response, capturingFilterChain);

        assertNull(MDC.get(REQUEST_ID_MDC_KEY));
    }

    @Test
    public void shouldSetResponseHeader() throws Exception {
        request.addHeader(REQUEST_ID_HEADER, TEST_REQUEST_ID);

        filter.doFilter(request, response, capturingFilterChain);

        assertRequestIdPresentInResponse(TEST_REQUEST_ID);
    }

    private void assertRequestIdPresentInResponse(String expectedRequestId) {
        assertThat(response.getHeader(REQUEST_ID_HEADER), is(expectedRequestId));
    }

    private static void assertValidUuid(String requestId) {
        assertThat(UUID.fromString(requestId), notNullValue());
    }

    private static class MdcRequestIdCapturingFilterChain implements FilterChain {

        private String mdcRequestId;

        @Override
        public void doFilter(ServletRequest request, ServletResponse response) {
            mdcRequestId = MDC.get(REQUEST_ID_MDC_KEY);
        }

        String getMdcRequestId() {
            return mdcRequestId;
        }

    }

}
