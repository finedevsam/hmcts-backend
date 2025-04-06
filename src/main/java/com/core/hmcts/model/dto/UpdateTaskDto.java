package com.core.hmcts.model.dto;

import com.core.hmcts.model.entity.Tasks.TaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Update task status")
public class UpdateTaskDto {
    @NotNull(message = "is required")
    private TaskStatus status;
}
