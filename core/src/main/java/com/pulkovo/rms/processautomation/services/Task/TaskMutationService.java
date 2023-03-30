package com.pulkovo.rms.processautomation.services.Task;

import com.pulkovo.rms.processautomation.apollo.ApolloService;
import com.pulkovo.rms.processautomation.services.Task.MutationService;
import graphql.com.pulkovo.rms.processautomation.UpdateTaskStatusMutation;
import java.util.concurrent.CompletableFuture;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class TaskMutationService implements MutationService {

    @NonNull
    private final ApolloService apolloService;

    @Override
    public CompletableFuture<UpdateTaskStatusMutation.Data> updateTaskStatus(String taskId) {
        log.info("updateTaskStatus, taskId={}",taskId);
        return apolloService.mutation(UpdateTaskStatusMutation.builder().id(taskId).build());
    }


}
