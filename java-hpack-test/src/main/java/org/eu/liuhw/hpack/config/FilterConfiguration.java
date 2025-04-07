package org.eu.liuhw.hpack.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eu.liuhw.hpack.filter.TestFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import javax.annotation.Resource;
import javax.servlet.DispatcherType;

/**
 * @author JavierHouse
 */
@Configuration
public class FilterConfiguration {

    @Resource
    private ObjectMapper objectMapper;

    @Bean
    public FilterRegistrationBean<TestFilter> tokenAuthFilter() {
        TestFilter filter = new TestFilter();
        filter.setObjectMapper(objectMapper);
        FilterRegistrationBean<TestFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(filter);
        bean.addUrlPatterns("/*");
        bean.setName("testFilter");
        bean.setOrder(Ordered.LOWEST_PRECEDENCE);
        bean.setDispatcherTypes(DispatcherType.REQUEST);

        return bean;
    }

}
