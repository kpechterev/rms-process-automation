package com.pulkovo.rms.processautomation.services.graphql;

import static graphql.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.cloud.contract.wiremock.WireMockSpring.options;
import static wiremock.org.eclipse.jetty.util.resource.Resource.newClassPathResource;

import com.github.tomakehurst.wiremock.WireMockServer;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;

import com.pulkovo.rms.processautomation.AbstractWiremockTest;

import com.pulkovo.rms.processautomation.services.Task.TaskMutationService;
import com.pulkovo.rms.processautomation.services.Task.TaskQueryService;
import graphql.com.pulkovo.rms.processautomation.GetResourceQuery;
import graphql.com.pulkovo.rms.processautomation.GetTaskQuery;
import graphql.com.pulkovo.rms.processautomation.GetTimePublishListQuery;
import graphql.com.pulkovo.rms.processautomation.UpdateTaskStatusMutation;
import graphql.com.pulkovo.rms.processautomation.type.TaskFilter;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

@Slf4j
@ExtendWith(WireMockExtension.class)
public class GetResourceForTaskService_Process_IT extends AbstractWiremockTest {

    @Value("classpath:graphql/json/taskPublicationTimeDirectoryList.json")
    private Resource taskTimePublishList;

    @Value("classpath:graphql/json/tasks.json")
    private Resource tasks;
    @Value("classpath:graphql/json/resources.json")
    private Resource resources;
    @Value("classpath:graphql/json/changeTaskStatus.json")
    private Resource changeTaskStatus;
    @Autowired
    private TaskQueryService queryService;
    @Autowired
    private TaskMutationService mutationService;
    @BeforeEach
    void setUp() {
        wireMockServer = new WireMockServer(options().dynamicPort());
        wireMockServer.start();
    }
    @AfterEach
    void tearDown() {
        wireMockServer.stop();
    }

    @Test
    void shouldGetQueryTaskPublicationTimeDirectoryList_whenCallGetPublishList() throws IOException, ExecutionException, InterruptedException {


        /*wireMockServer.stubFor(WireMock.post("/api/graphql")
                .withRequestBody(new RegexPattern(".*GetTimePublishList.*"))
                .withHeader("Content-Type", new EqualToPattern("application/json; charset=UTF-8"))
                .willReturn(new ResponseDefinitionBuilder().withHeader("Content-Type", "application/json")
                        .withBody(ResourceUtils.contentOf(resource))));*/

        stubFor(".*GetTimePublishList.*",taskTimePublishList);
        log.warn(taskTimePublishList.getFile().toString());
        log.warn(String.valueOf(taskTimePublishList.contentLength()));

        CompletableFuture<GetTimePublishListQuery.Data> result = queryService.getPublishTime();

        assertNotNull(result);
        assertNotNull(result.get());
        assertNotNull(result.get().taskPublicationTimeDirectoryList());
        assertEquals(2, result.get().taskPublicationTimeDirectoryList().size());

    }

    @Test
    void shouldGetQueryTasks_whenCallGetTask() throws IOException, ExecutionException, InterruptedException {


        stubFor(".*GetTask.*",tasks);

        CompletableFuture<GetTaskQuery.Data> result = queryService.getTasks(TaskFilter.builder().build());

        assertNotNull(result);
        assertNotNull(result.get());
        assertNotNull(result.get().task());
        assertEquals(2, result.get().task().size());

    }

    @Test
    void shouldGetQueryResources_whenCallGetGetResource() throws IOException, ExecutionException, InterruptedException {

        stubFor(".*Resource.*",resources);

        CompletableFuture<GetResourceQuery.Data> result = queryService.getResources(List.of("subdivision_1"));

        assertNotNull(result);
        assertNotNull(result.get());


    }

    @Test
    void shouldMutationUpdateTaskStatus_whenCallChangeTaskStatus() throws IOException, ExecutionException, InterruptedException {

        stubFor(".*UpdateTaskStatus.*",changeTaskStatus);

        CompletableFuture<UpdateTaskStatusMutation.Data> result = mutationService.updateTaskStatus("task_1") ;

        assertNotNull(result);
        assertNotNull(result.get());
        assertNotNull(result.get().update());
        assertEquals(1, result.get().update().id());

    }
}
