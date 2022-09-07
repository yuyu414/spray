package com.no.hurdles.spray;

import com.no.hurdles.spray.utils.LarkTalkingUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * RestTemplate配置
 */
@Configuration
public class AppConfig {

    @Value("lark.url")
    private String larkUrl;

    @Value("lark.secret")
    private String larkSecret;

    @Autowired
    private RestTemplate restTemplate;
 
    @Bean
    public RestTemplate restTemplate(@Qualifier("simpleClientHttpRequestFactory") ClientHttpRequestFactory factory){
        return new RestTemplate(factory);
    }
 
    @Bean
    public ClientHttpRequestFactory simpleClientHttpRequestFactory(){
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(15000);
        // 获取数据超时时间毫秒
        factory.setReadTimeout(5000);
        return factory;
    }

    @Bean
    public LarkTalkingUtil larkTalkingUtil(){
        return new LarkTalkingUtil(restTemplate, larkUrl, larkSecret);
    }
 
}