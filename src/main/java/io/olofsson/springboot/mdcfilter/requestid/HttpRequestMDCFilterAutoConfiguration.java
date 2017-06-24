package io.olofsson.springboot.mdcfilter.requestid;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class HttpRequestMDCFilterAutoConfiguration {
    @Bean
    HttpRequestMDCFilter httpRequestMDCFilter() {
        return new HttpRequestMDCFilter();
    }
}
