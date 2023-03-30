package com.pulkovo.rms.processautomation.services.Task;

import graphql.com.pulkovo.rms.processautomation.GetResourceQuery;
import graphql.com.pulkovo.rms.processautomation.GetTaskQuery;
import graphql.com.pulkovo.rms.processautomation.GetTimePublishListQuery;
import graphql.com.pulkovo.rms.processautomation.UpdateTaskStatusMutation;
import graphql.com.pulkovo.rms.processautomation.type.TaskFilter;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface QueryService {
    CompletableFuture<GetTimePublishListQuery.Data> getPublishTime();

    CompletableFuture<GetTaskQuery.Data> getTasks(TaskFilter taskFilter);

    CompletableFuture<GetResourceQuery.Data> getResources(List<String> subdivision);

}
