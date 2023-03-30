package com.pulkovo.rms.processautomation.services;

import com.pulkovo.rms.processautomation.dto.TaskDtoList;
import com.pulkovo.rms.processautomation.services.Task.TaskService;
import java.util.concurrent.ExecutionException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class PublishTaskController {

    @NonNull
    private final TaskService taskService;

    @GetMapping("/task")
    public TaskDtoList publishTask() throws ExecutionException, InterruptedException {
      log.info("publishTasks");
      return taskService.getPublishTask();
    }
}
