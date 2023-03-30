package com.pulkovo.rms.processautomation.services.Task;

import graphql.com.pulkovo.rms.processautomation.UpdateTaskStatusMutation;
import java.util.concurrent.CompletableFuture;

public interface MutationService {

    CompletableFuture<UpdateTaskStatusMutation.Data> updateTaskStatus(String taskId);
}
