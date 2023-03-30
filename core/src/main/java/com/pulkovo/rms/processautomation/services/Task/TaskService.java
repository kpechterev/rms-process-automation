package com.pulkovo.rms.processautomation.services.Task;

import brave.Tracer;
import com.pulkovo.rms.processautomation.dto.TaskDto;
import com.pulkovo.rms.processautomation.dto.TaskDtoList;
import graphql.com.pulkovo.rms.processautomation.GetResourceQuery;
import graphql.com.pulkovo.rms.processautomation.GetTaskQuery;
import graphql.com.pulkovo.rms.processautomation.UpdateTaskStatusMutation;
import graphql.com.pulkovo.rms.processautomation.type.TaskFilter;
import graphql.com.pulkovo.rms.processautomation.type.TaskFilterInInput;
import graphql.com.pulkovo.rms.processautomation.type.TaskStatus;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {
    @NonNull
    private final Tracer tracer;

    @NonNull
    private final Clock clockdb;

    @NonNull
    private final TaskMutationService mutationService;

    @NonNull
    private final TaskQueryService queryService;

    public TaskDtoList getPublishTask() throws ExecutionException, InterruptedException {
        log.info("getUpdateTaskStatus ListTaskDto={}", updateTaskStatus());
        return TaskDtoList.builder().taskDtoList(updateTaskStatus()).build();

    }

    public  List<TaskDto> updateTaskStatus() throws ExecutionException, InterruptedException {
        log.info("processUpdate, getTaskIds={}", getTaskIds());
        return getTaskIds().stream()
                .map(taskId->mutationService.updateTaskStatus(taskId).join())
                .map(data -> convertToDto(data.update()))
                .toList();
    }


    public List<String> getTaskIds() throws ExecutionException, InterruptedException {
        log.info("getTaskIds, resourceIds={}", getResourceIds());

        TaskFilterInInput taskFilterInInput = TaskFilterInInput.builder()
                .resourceIds(getResourceIds())
                .statuses(List.of(TaskStatus.ASSIGNED))
                .build();
        TaskFilter taskFilter = TaskFilter.builder()
                .in(taskFilterInInput)
                .queryFrom(OffsetDateTime.now(clockdb))
                .queryTo(OffsetDateTime.now(clockdb).plusHours(24))
                .build();

        var resources = queryService.getTasks(taskFilter).
                thenApply(taskList->taskList.task().stream()
                            .filter(task -> task.scheduledStart().compareTo(task.scheduledStart().plusHours(24)) <= 0)
                            .map(GetTaskQuery.Task::resource)
                            .toList()).get();

        List<String> resourceIds = new ArrayList<>();
        for (int i = 0; i < resources.size(); i++) {
            resourceIds.add(((GetTaskQuery.AsIResource) resources.get(i).resource()).id());
        }
        return resourceIds;
    }

    public List<String> getResourceIds() throws ExecutionException, InterruptedException {
        log.info("getResourceIds, subdivisionsIds={}", getSubdivisionIds());

        var resources = queryService.getResources(getSubdivisionIds()).get().resources();
        List<String> resourceIds = new ArrayList<>();
        for (int i = 0; i < resources.size(); i++) {
            resourceIds.add(((GetResourceQuery.AsIResource) resources.get(i).resource()).id());
        }
        return resourceIds;
    }

    public List<String> getSubdivisionIds() throws ExecutionException, InterruptedException {
        log.info("getSubdivisionIds");

        return queryService.getPublishTime()
                .thenApply(timeList-> timeList.taskPublicationTimeDirectoryList().stream()
                        .map(time->time.subdivision().id())
                        .toList()).get();
    }


    private TaskDto convertToDto(UpdateTaskStatusMutation.Update update){
        return  TaskDto.builder().id(update.id()).taskStatus(update.status()).build();
    }

}
