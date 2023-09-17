package com.taai.app.config;

import com.taai.app.repository.remote.FirstDatasourceClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackageClasses = FirstDatasourceClient.class)
public class FeignClientConfig {
}
