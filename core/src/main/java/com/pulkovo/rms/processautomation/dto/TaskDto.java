package com.pulkovo.rms.processautomation.dto;

import graphql.com.pulkovo.rms.processautomation.type.TaskStatus;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
public class TaskDto {

    private String id;
    private TaskStatus taskStatus;
}
