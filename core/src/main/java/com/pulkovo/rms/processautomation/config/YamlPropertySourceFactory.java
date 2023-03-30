package com.pulkovo.rms.processautomation.config;

import java.util.Objects;
import lombok.NonNull;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;
import org.springframework.lang.Nullable;

public class YamlPropertySourceFactory implements PropertySourceFactory {

    @Override
    @NonNull
    public PropertySource<?> createPropertySource(@Nullable final String name,
            @NonNull final EncodedResource encodedResource) {

        final var factory = new YamlPropertiesFactoryBean();
        factory.setResources(encodedResource.getResource());

        final var properties = factory.getObject();
        return new PropertiesPropertySource(Objects.requireNonNull(encodedResource.getResource().getFilename()),
                Objects.requireNonNull(properties));
    }
}
