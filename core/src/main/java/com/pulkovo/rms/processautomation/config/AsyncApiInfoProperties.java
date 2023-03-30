package com.pulkovo.rms.processautomation.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("info")
@PropertySource(value = "classpath:asyncapi.yaml", factory = YamlPropertySourceFactory.class)
@Getter
@Setter
public class AsyncApiInfoProperties {

    private String version;
}
