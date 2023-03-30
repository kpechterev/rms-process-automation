package com.pulkovo.rms.processautomation.services.Task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.pulkovo.rms.processautomation.ApplicationTest;
import com.pulkovo.rms.processautomation.apollo.ApolloService;
import com.pulkovo.rms.processautomation.dto.TaskDto;
import graphql.com.pulkovo.rms.processautomation.GetResourceQuery;
import graphql.com.pulkovo.rms.processautomation.GetTaskQuery;
import graphql.com.pulkovo.rms.processautomation.GetTimePublishListQuery;
import graphql.com.pulkovo.rms.processautomation.UpdateTaskStatusMutation;
import graphql.com.pulkovo.rms.processautomation.type.ResourceType;
import graphql.com.pulkovo.rms.processautomation.type.TaskFilter;
import graphql.com.pulkovo.rms.processautomation.type.TaskFilterInInput;
import graphql.com.pulkovo.rms.processautomation.type.TaskStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

@ExtendWith(MockitoExtension.class)
@Slf4j
public class GetTaskService_Process_IT extends ApplicationTest {


    @Autowired
    private TaskService taskService;
    @MockBean
    private TaskQueryService queryService;
    @MockBean
    private TaskMutationService mutationService;


    private static final  GetTimePublishListQuery.Data TimePublishList_DATA = new GetTimePublishListQuery.Data(
            List.of(
                    new GetTimePublishListQuery.TaskPublicationTimeDirectoryList("SUBDIVISION", "1",
                            new GetTimePublishListQuery.Subdivision("SUBDIVISION", "subdivision_1"), 7200),
                    new GetTimePublishListQuery.TaskPublicationTimeDirectoryList("SUBDIVISION", "2",
                            new GetTimePublishListQuery.Subdivision("SUBDIVISION", "subdivision_2"), 7200)));
    private static final  GetResourceQuery.Data Resource_DATA = new GetResourceQuery.Data(
            Collections.singletonList(
                    new GetResourceQuery.Resource("RESOURCE",
                            new GetResourceQuery.AsIResource("RESOURCE", "resource_1"))));

    private static final GetTaskQuery.Data Task_DATA = new GetTaskQuery.Data(Collections.singletonList(
            new GetTaskQuery.Task("TASK", "id", OffsetDateTime.now(),
                    new GetTaskQuery.Resource("TASK", ResourceType.EMPLOYEE,
                            new GetTaskQuery.AsIResource("TASK", "task_1")))));

    private static final UpdateTaskStatusMutation.Data Update_DATA = new UpdateTaskStatusMutation.Data(
            new UpdateTaskStatusMutation.Update("UPDATE","update_1",TaskStatus.PUBLISHED));


    @Test
    public void shouldGetSubdivisionIds_whenCallGetResourceIds() throws Exception {
        //data
        final var expectedIds = List.of("subdivision_1","subdivision_2");

        // act
        Mockito.when(queryService.getPublishTime()).thenReturn(CompletableFuture.completedFuture(TimePublishList_DATA));

        final var result = taskService.getSubdivisionIds();

        verify(queryService).getPublishTime();

        // assert
        assertEquals(expectedIds, result);

    }

    @Test
    void shouldGetResourceIds_whenCallGetTaskIds() throws ExecutionException, InterruptedException {
        //data
        final var expectedIds = List.of("resource_1");
        final var notExpectedIds = List.of("resource_2");

        // mock

        Mockito.when(queryService.getPublishTime()).thenReturn(CompletableFuture.completedFuture(TimePublishList_DATA));
        Mockito.when(queryService.getResources(taskService.getSubdivisionIds())).thenReturn(CompletableFuture.completedFuture(Resource_DATA));

        //act
        final var result = taskService.getResourceIds();

        // assert
        assertEquals(expectedIds, result);
        assertNotEquals(notExpectedIds, result);

    }

    @Test
    void shouldGetTaskIds_wheCallUpdateTaskStatus() throws ExecutionException, InterruptedException, InstantiationException, IllegalAccessException {

        //data
        final var expectedIds = List.of("task_1");


        // mock
        Mockito.when(queryService.getPublishTime()).thenReturn(CompletableFuture.completedFuture(TimePublishList_DATA));
        Mockito.when(queryService.getResources(taskService.getSubdivisionIds())).thenReturn(CompletableFuture.completedFuture(Resource_DATA));

        //time
        Clock fixedClock = Clock.fixed(Clock.systemUTC().instant(), ZoneOffset.UTC);
        // Создание объектов OffsetDateTime с использованием фиктивного времени
        OffsetDateTime now = OffsetDateTime.now(fixedClock);
        OffsetDateTime later = now.plusHours(24);


        //taskFilter
        TaskFilterInInput taskFilterInInput = TaskFilterInInput.builder()
                .resourceIds(taskService.getResourceIds())
                .statuses(List.of(TaskStatus.ASSIGNED))
                .build();
        TaskFilter taskFilter = TaskFilter.builder()
                .in(taskFilterInInput)
                .queryFrom(now)
                .queryTo(later)
                .build();
        //taskFilter end

        when(queryService.getTasks(taskFilter)).thenReturn(CompletableFuture.completedFuture(Task_DATA));

        //act
        final var result = taskService.getTaskIds();

        // assert
        assertEquals(expectedIds, result);
    }

    @Test
    void shouldUpdateTaskStatus_whenCallGetPublishTask() throws ExecutionException, InterruptedException {

        //data
        final var taskDto = TaskDto.builder().id("update_1").taskStatus(TaskStatus.PUBLISHED).build();
        final var expectedIds = new ArrayList<TaskDto>();
        expectedIds.add(taskDto);

        //mock
        Mockito.when(queryService.getPublishTime()).thenReturn(CompletableFuture.completedFuture(TimePublishList_DATA));
        Mockito.when(queryService.getResources(taskService.getSubdivisionIds())).thenReturn(CompletableFuture.completedFuture(Resource_DATA));
        Mockito.when(queryService.getTasks(TaskFilter.builder().build())).thenReturn(CompletableFuture.completedFuture(Task_DATA));
        Mockito.when(mutationService.updateTaskStatus(taskService.getTaskIds().get(0))).thenReturn(CompletableFuture.completedFuture(Update_DATA));

        //act
        final var result = taskService.updateTaskStatus();

        // assert
        assertEquals(expectedIds.get(0).getId(), result.get(0).getId());

        //verify
        verify(mutationService).updateTaskStatus(taskService.getTaskIds().get(0));
    }


    @Test
    void shouldGetPublishTask_whenCallControllerPublishTask() throws ExecutionException, InterruptedException {

        var task = TaskDto.builder().id("update_1").taskStatus(TaskStatus.PUBLISHED).build();
        var expected = new ArrayList<TaskDto>();
        expected.add(task);

        // mock
        Mockito.when(queryService.getPublishTime()).thenReturn(CompletableFuture.completedFuture(TimePublishList_DATA));
        Mockito.when(queryService.getResources(taskService.getSubdivisionIds())).thenReturn(CompletableFuture.completedFuture(Resource_DATA));
        Mockito.when(queryService.getTasks(TaskFilter.builder().build())).thenReturn(CompletableFuture.completedFuture(Task_DATA));
        Mockito.when(mutationService.updateTaskStatus(taskService.getTaskIds().get(0))).thenReturn(CompletableFuture.completedFuture(Update_DATA));

        final var result = taskService.getPublishTask();

        // assert
        assertEquals(expected,result.getTaskDtoList());

    }
}