package com.pulkovo.rms.processautomation.config;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "task-manager.api.graph-ql")
@Setter
@Getter
public class GraphQlApiProperties {

    @NonNull
    private String url;
    private String token;
    @NonNull
    private Integer readTimeout;
}
