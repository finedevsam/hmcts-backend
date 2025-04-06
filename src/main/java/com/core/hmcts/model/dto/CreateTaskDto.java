package com.core.hmcts.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor 
@Schema(description = "Create new task")
public class CreateTaskDto {

    @NotNull(message = "Title is required")
    private String title;

    private String description;

    @NotNull(message = "Due date is required")
    @Schema(
            description = "Due date and time",
            type = "string",
            format = "date-time",
            example = "2025-04-03T14:30"
    )
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm")
    private String dueDate;
}
