package com.pulkovo.rms.processautomation.apollo;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.apollographql.apollo.http.OkHttpExecutionContext;
import java.util.concurrent.CompletableFuture;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

@Getter
@Slf4j
@RequiredArgsConstructor
public class ApolloRequestCallback<T> extends ApolloCall.Callback<T> {

    @NotNull
    private final Tracer tracer;
    private final CompletableFuture<T> completableFuture = new CompletableFuture<>();

    @Override
    public void onResponse(@NotNull Response<T> response) {
        if (response.getData() == null) {
            final OkHttpExecutionContext ctx = response.getExecutionContext().get(OkHttpExecutionContext.KEY);
            if (ctx != null) {
                final var headers = ctx.getResponse().request().headers();
                final var cb = TraceContext.newBuilder();
                var hasTraceId = false;
                var hasSpanId = false;

                final var traceId = headers.get(ApolloTracing.HEADER_TRACE_ID);
                if (StringUtils.isNotBlank(traceId)) {
                    cb.traceId(ApolloTracing.requiredLong(traceId));
                    hasTraceId = true;
                }

                final var spanId = headers.get(ApolloTracing.HEADER_SPAN_ID);
                if (StringUtils.isNotBlank(spanId)) {
                    cb.spanId(ApolloTracing.requiredLong(spanId));
                    hasSpanId = true;
                }

                cb.parentId(ApolloTracing.optionalLong(headers.get(ApolloTracing.HEADER_PARENT_SPAN_ID)));
                cb.sampled(ApolloTracing.optionalBool(headers.get(ApolloTracing.HEADER_SAMPLED)));

                if (hasTraceId && hasSpanId) {
                    try (var ignored = tracer.withSpanInScope(tracer.joinSpan(cb.build()))) {
                        log.warn("GraphQL error: {}", response.getErrors());
                    }
                } else {
                    log.warn("GraphQL error: {}", response.getErrors());
                }
            }
            completableFuture.completeExceptionally(new ApolloGraphQlErrorException(response.getErrors()));
        } else {
            completableFuture.completeAsync(response::getData);
        }
    }

    @Override
    public void onFailure(@NotNull ApolloException e) {
        completableFuture.completeExceptionally(e);
    }
}
