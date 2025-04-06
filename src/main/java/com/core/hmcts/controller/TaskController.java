package com.core.hmcts.controller;

import com.core.hmcts.model.dto.CreateTaskDto;
import com.core.hmcts.model.dto.UpdateTaskDto;
import com.core.hmcts.model.entity.Tasks;
import com.core.hmcts.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("tasks/")
public class TaskController {

    private final TaskService taskService;
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @Operation(
                summary = "Create Task",
                description = "This endpoint allows a user to create a task",
                requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                        content = @Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = @Schema(implementation = CreateTaskDto.class)
                        )
                ),
                responses = {
                        @ApiResponse(
                        description = "Task Created Successfully",
                        content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                schema = @Schema(implementation = Tasks.class))
                        ),
                        @ApiResponse(
                        responseCode = "400",
                        description = "Something went wrong"
                        )
                }
        )
    @PostMapping(value = "create", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addTask(@RequestBody CreateTaskDto task) {
            return taskService.addTask(task);
    }


    @Operation(
            summary = "Get Task",
            description = "This endpoint allows a user to get task using the task id",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Task retrieved successfully",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Tasks.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Task not found"
                    )
            }
    )
    @GetMapping("{id}")
    public ResponseEntity<?> getTaskById(@PathVariable("id") String id) {
        return taskService.getTask(id);
    }

    @Operation(
            summary = "Get all Tasks",
            description = "This endpoint return all tasks",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Tasks retrieved successfully",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = Tasks.class))
                            )
                    )
            }
    )
    @GetMapping
    public ResponseEntity<?> getAllTasks(Pageable pageable) {
        return taskService.getTasks(pageable);
    }


    @Operation(
            summary = "Update Tasks Status",
            description = "This endpoint all user the update task status",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UpdateTaskDto.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Tasks retrieved successfully",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Tasks.class))
                    )
            }
    )
    @PutMapping(value = "{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateTask(@PathVariable("id") String id, @RequestBody UpdateTaskDto task) {
        return taskService.updateTask(id, task);
    }


    @Operation(
            summary = "Delete task",
            description = "This endpoint allow user to remove/delete task",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = MediaType.APPLICATION_FORM_URLENCODED_VALUE
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Task deleted successfully",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
                    )
            }
    )
    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteTask(@PathVariable("id") String id) {
        return taskService.deleteTask(id);
    }

}
