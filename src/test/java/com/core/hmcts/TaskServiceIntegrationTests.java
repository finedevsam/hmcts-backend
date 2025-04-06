package com.core.hmcts;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import com.core.hmcts.controller.TaskController;
import com.core.hmcts.handler.DataResponse;
import com.core.hmcts.model.dto.CreateTaskDto;
import com.core.hmcts.model.dto.UpdateTaskDto;
import com.core.hmcts.model.entity.Tasks;
import com.core.hmcts.model.entity.Tasks.TaskStatus;
import com.core.hmcts.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(TaskController.class)
public class TaskServiceIntegrationTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private String taskId;

    @BeforeEach
    void setup() {
        taskId = "123e4567-e89b-12d3-a456-426614174000";
    }

    @Test
    void createTask_ValidRequest_ReturnsCreated() throws Exception {
        // Arrange
        CreateTaskDto request = new CreateTaskDto("Task Title", "Task Description", "2023-12-31T23:59");

        Tasks taskResponse = new Tasks();
        taskResponse.setId(taskId);
        taskResponse.setTitle(request.getTitle());
        taskResponse.setDescription(request.getDescription());
        taskResponse.setDueDate(LocalDateTime.parse(request.getDueDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")));

        DataResponse.ResponseData responseData =
            new DataResponse.ResponseData(201, "Task created successfully", taskResponse);

        URI location = new URI("/tasks/create/" + taskId);

        ResponseEntity<Object> responseEntity = ResponseEntity.created(location).body(responseData);

        when(taskService.addTask(any(CreateTaskDto.class))).thenReturn((ResponseEntity) responseEntity);

        // Act & Assert
        mockMvc.perform(post("/tasks/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.code").value(201))
            .andExpect(jsonPath("$.message").value("Task created successfully"))
            .andExpect(jsonPath("$.data.id").value(taskId))
            .andExpect(jsonPath("$.data.title").value("Task Title"))
            .andExpect(jsonPath("$.data.description").value("Task Description"))
            .andExpect(jsonPath("$.data.dueDate").value("2023-12-31 23:59:00"));
    }

    @Test
    void getTask_ExistingId_ReturnsTask() throws Exception {
        // Arrange
        String taskId = "1";
        Tasks task = new Tasks();
        task.setId(taskId);
        task.setTitle("Existing Task");
        task.setDescription("Existing Task Description");
        task.setDueDate(LocalDateTime.now());

        // Mock the service response
        DataResponse.ResponseData responseData = 
            new DataResponse.ResponseData(200, "Task retrieve successfully", task);
        ResponseEntity<Object> responseEntity = ResponseEntity.ok().body(responseData);
        when(taskService.getTask(taskId)).thenReturn((ResponseEntity) responseEntity);

        // Act & Assert
        mockMvc.perform(get("/tasks/" + taskId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(taskId))
                .andExpect(jsonPath("$.data.title").value("Existing Task"))
                .andExpect(jsonPath("$.data.description").value("Existing Task Description"));
    }

    @Test
    void getTask_NonExistingId_ReturnsNotFound() throws Exception {
        String wrongTaskId = "123e4567-e89b-12d3-a456-426614174000";
        when(taskService.getTask(wrongTaskId))
            .thenReturn(new ResponseEntity<>(HttpStatus.NOT_FOUND));

        // Act & Assert
        mockMvc.perform(get("/tasks/" + wrongTaskId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllTasks_ReturnsPaginatedResults() throws Exception {
        // Arrange
        Tasks task1 = new Tasks();
        task1.setId("123e4567-e89b-12d3-a456-426614174000");
        Tasks task2 = new Tasks();
        task2.setId("123e4567-e89b-12d3-a456-426614174001");
        

        // Mock the service response
        DataResponse.ResponseData responseData = 
            new DataResponse.ResponseData(200, "Task retrieve successfully", List.of(task1, task2));
        ResponseEntity<Object> responseEntity = ResponseEntity.ok().body(responseData);
        
            
        when(taskService.getTasks(any(Pageable.class)))
            .thenReturn((ResponseEntity) responseEntity);

        // Act & Assert
        mockMvc.perform(get("/tasks/?page=0&size=2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value("123e4567-e89b-12d3-a456-426614174000"))
                .andExpect(jsonPath("$.data[1].id").value("123e4567-e89b-12d3-a456-426614174001"));
    }

    @Test
    void updateTask_ValidRequest_ReturnsUpdatedTask() throws Exception {
        // Arrange
        UpdateTaskDto updateRequest = new UpdateTaskDto(TaskStatus.IN_PROGRESS);
        Tasks updatedTask = new Tasks();
        updatedTask.setId(taskId);
        updatedTask.setStatus(TaskStatus.IN_PROGRESS);

        // Mock the service response
        DataResponse.ResponseData responseData = 
            new DataResponse.ResponseData(200, "Task retrieve successfully",updatedTask);
        ResponseEntity<Object> responseEntity = ResponseEntity.ok().body(responseData);


        when(taskService.updateTask(eq(taskId), any(UpdateTaskDto.class)))
            .thenReturn((ResponseEntity) responseEntity);

        // Act & Assert
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put("/tasks/" + taskId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(taskId))
                .andExpect(jsonPath("$.data.status").value("IN_PROGRESS"));
    }

    @Test
    void deleteTask_ExistingId_ReturnsNoContent() throws Exception {
        // Arrange
        when(taskService.deleteTask(eq(taskId)))
            .thenReturn(new ResponseEntity<>(HttpStatus.NO_CONTENT));
        // Act & Assert
        mockMvc.perform(delete("/tasks/" + taskId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}
