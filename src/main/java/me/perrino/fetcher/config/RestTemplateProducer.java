package me.perrino.fetcher.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;


import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.cache.CacheConfig;
import org.apache.http.impl.client.cache.CachingHttpClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Configuration
@Slf4j
public class RestTemplateProducer {

    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        RestTemplate restTemplate = new RestTemplate(requestFactory);
        restTemplate.setRequestFactory(new BufferingClientHttpRequestFactory(new HttpComponentsClientHttpRequestFactory(httpClient())));
        restTemplate.setInterceptors(Collections.singletonList(new RequestResponseLoggingInterceptor()));
        return restTemplate;
    }

    @Bean
    public HttpClient httpClient() {
        return CachingHttpClientBuilder
                .create()
                .setCacheConfig(cacheConfig())
                .build();
    }


    @Bean
    public CacheConfig cacheConfig() {
        return CacheConfig
                .custom()
                .setMaxObjectSize(500000) // 500KB
                .setMaxCacheEntries(2000)
                // Set this to false and a response with queryString
                // will be cached when it is explicitly cacheable
                .setNeverCacheHTTP10ResponsesWithQueryString(false)
                .build();
    }

    public class RequestResponseLoggingInterceptor implements ClientHttpRequestInterceptor {

        private List<HttpStatus> traceableStatus;

        {
            traceableStatus =
                    Arrays.asList(HttpStatus.BAD_REQUEST, HttpStatus.NOT_FOUND, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        @Override
        public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
                throws IOException {
            ClientHttpResponse response = execution.execute(request, body);
            logResponse(response, request);
            return response;
        }

        private void logResponse(ClientHttpResponse response, HttpRequest request) throws IOException {
            if (traceableStatus.contains(response.getStatusCode())) {
                log.error("ERROR  Response {} {}  with Headers {} || Request {} {} with Headers {}", response.getStatusCode(),
                        response.getStatusText(), response.getHeaders(), request.getMethodValue().toUpperCase() ,
                        request.getURI(), request.getHeaders());
            }
        }
    }

}
