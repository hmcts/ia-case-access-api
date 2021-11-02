package uk.gov.hmcts.reform.iacaseaccessapi.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import feign.codec.Decoder;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

@Configuration
public class FeignConfiguration {

    @Bean
    public Decoder decoder() {
        HttpMessageConverter jacksonConverter = new MappingJackson2HttpMessageConverter(objectMapper());

        return new ResponseEntityDecoder(new SpringDecoder(() -> new HttpMessageConverters(jacksonConverter)));
    }

    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new ParameterNamesModule());
        return objectMapper;
    }

}
