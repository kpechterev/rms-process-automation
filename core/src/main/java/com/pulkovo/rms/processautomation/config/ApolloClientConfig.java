package com.pulkovo.rms.processautomation.config;

import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.CustomTypeAdapter;
import com.apollographql.apollo.api.CustomTypeValue;
import com.apollographql.apollo.api.ResponseField;
import com.apollographql.apollo.api.ScalarType;
import graphql.com.pulkovo.rms.processautomation.type.CustomType;
import java.time.OffsetDateTime;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApolloClientConfig {

    private static final CustomTypeAdapter<OffsetDateTime> DATETIME_ADAPTER = new CustomTypeAdapter<>() {
        @Override
        public OffsetDateTime decode(@NotNull final CustomTypeValue<?> customTypeValue) {
            return OffsetDateTime.parse(customTypeValue.value.toString());
        }

        @NotNull
        @Override
        public CustomTypeValue<?> encode(final OffsetDateTime offsetDateTime) {
            return new CustomTypeValue.GraphQLString(offsetDateTime.toString());
        }
    };

    @Bean
    public ApolloClient apolloClient(@NotNull final GraphQlApiProperties config) {
        return ApolloClient.builder()
                .serverUrl(config.getUrl())
                .okHttpClient(new OkHttpClient.Builder().readTimeout(config.getReadTimeout(), TimeUnit.MILLISECONDS)
                        .addInterceptor(chain -> chain.proceed(chain.request()
                                .newBuilder()
                                .addHeader("Authorization", "Bearer " + config.getToken())
                                .build()))
                        .build())
                .dispatcher(new ThreadPoolExecutor(10, 100, 360, TimeUnit.SECONDS, new LinkedBlockingQueue<>(),
                        runnable -> new Thread(runnable, "Apollo Dispatcher")))
                .addCustomTypeAdapter(CustomType.DATETIME, DATETIME_ADAPTER)
                .build();
    }
}
