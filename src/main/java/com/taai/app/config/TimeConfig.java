package com.taai.app.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.TimeZone;

@Configuration
@Slf4j
public class TimeConfig {

    @PostConstruct
    public void init() {
        String timezone = "Asia/Ho_Chi_Minh";
        System.setProperty("user.timezone", timezone);
        TimeZone.setDefault(TimeZone.getTimeZone(timezone));
        log.info("System timezone set to UTC+7 at {}", LocalDateTime.now());
    }
}
