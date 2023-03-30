package com.pulkovo.rms.processautomation.apollo;

import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Mutation;
import com.apollographql.apollo.api.Operation;
import com.apollographql.apollo.api.Query;
import com.apollographql.apollo.request.RequestHeaders;
import java.util.concurrent.CompletableFuture;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import brave.Tracer;

@Service
@RequiredArgsConstructor
public class ApolloService {

    @NonNull
    private final ApolloClient apolloClient;
    @NotNull
    private final Tracer tracer;


    public <D extends Operation.Data, T, V extends Operation.Variables> CompletableFuture<T> query(
            @NonNull final Query<D, T, V> query) {

        final var callback = new ApolloRequestCallback<T>(tracer);

        apolloClient.query(query).toBuilder().requestHeaders(requestHeaders()).build().enqueue(callback);

        return callback.getCompletableFuture();
    }


    public <D extends Operation.Data, T, V extends Operation.Variables> CompletableFuture<T> mutation(
            @NonNull final Mutation<D, T, V> mutation) {

        final var callback = new ApolloRequestCallback<T>(tracer);

        apolloClient.mutate(mutation).toBuilder().requestHeaders(requestHeaders()).build().enqueue(callback);

        return callback.getCompletableFuture();
    }

    private RequestHeaders requestHeaders() {
        final var builder = RequestHeaders.builder();
        final var span = tracer.currentSpan();

        if (span != null) {
            final var spanContext = span.context();
            builder.addHeader(ApolloTracing.HEADER_TRACE_ID, spanContext.traceIdString())
                    .addHeader(ApolloTracing.HEADER_SPAN_ID, spanContext.spanIdString());

            final var parentSpanId = spanContext.parentIdString();
            if (parentSpanId != null) {
                builder.addHeader(ApolloTracing.HEADER_PARENT_SPAN_ID, parentSpanId);
            }

            final var sampled = spanContext.sampled();
            if (sampled != null) {
                builder.addHeader(ApolloTracing.HEADER_SAMPLED, sampled ? "1" : "0");
            }
        }

        return builder.build();
    }
}