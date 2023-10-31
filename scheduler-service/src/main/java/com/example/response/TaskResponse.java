package com.example.response;

import com.example.entity.ResponseStatus;
import com.example.entity.ScheduleTask;
import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TaskResponse {

    private ResponseStatus status;
    private List<ScheduleTask> data;

}
