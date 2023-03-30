package com.pulkovo.rms.processautomation.services.Task;

import com.pulkovo.rms.processautomation.apollo.ApolloService;
import com.pulkovo.rms.processautomation.services.Task.QueryService;
import graphql.com.pulkovo.rms.processautomation.GetResourceQuery;
import graphql.com.pulkovo.rms.processautomation.GetTaskQuery;
import graphql.com.pulkovo.rms.processautomation.GetTimePublishListQuery;
import graphql.com.pulkovo.rms.processautomation.type.TaskFilter;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class TaskQueryService implements QueryService {

    @NonNull
    private final ApolloService apolloService;

    @Override
    public CompletableFuture<GetTimePublishListQuery.Data> getPublishTime() {
        log.info("taskPublicationTimeDirectoryList");
        return apolloService.query(GetTimePublishListQuery.builder().build());
    }

    @Override
    public CompletableFuture<GetTaskQuery.Data> getTasks(TaskFilter taskFilter) {
        log.info("getTasks, taskFilter={}",taskFilter);
        return apolloService.query(GetTaskQuery.builder().filter(taskFilter).build());
    }

    @Override
    public CompletableFuture<GetResourceQuery.Data> getResources(List<String> subdivision) {
        log.info("getResources, subdivision={}",subdivision);
        return apolloService.query(GetResourceQuery.builder().subdivision(subdivision).build());
    }

}