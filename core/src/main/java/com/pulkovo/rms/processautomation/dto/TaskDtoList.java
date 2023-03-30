package com.pulkovo.rms.processautomation.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TaskDtoList {

    private List<TaskDto> taskDtoList;
}
