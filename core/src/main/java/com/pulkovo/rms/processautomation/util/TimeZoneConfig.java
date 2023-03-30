package com.pulkovo.rms.processautomation.util;

import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class TimeZoneConfig {
    @Bean
    public Clock getClock(){
        return Clock.systemUTC();
    }
}
