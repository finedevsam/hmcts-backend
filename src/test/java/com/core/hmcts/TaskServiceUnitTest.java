package com.core.hmcts;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.core.hmcts.handler.DataResponse;
import com.core.hmcts.model.dao.TasksDao;
import com.core.hmcts.model.dto.CreateTaskDto;
import com.core.hmcts.model.dto.UpdateTaskDto;
import com.core.hmcts.model.entity.Tasks;
import com.core.hmcts.service.TaskServiceImpl;


@ExtendWith(MockitoExtension.class)
public class TaskServiceUnitTest {
    
    @Mock
    private TasksDao tasksDao;

    @Mock
    private DataResponse dataResponse;

    @InjectMocks
    private TaskServiceImpl taskService;

    private String createdTaskId;

    @Test
    void addTask_ValidDto_ReturnsCreatedResponse() {
        // 👇 Mock request context if needed in the service
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        // Given
        String dueDateStr = "2023-10-10T10:10";
        CreateTaskDto dto = new CreateTaskDto("Test Task", "Test Description", dueDateStr);

        LocalDateTime expectedDueDate = LocalDateTime.parse(dueDateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));

        // Mock DAO to generate UUID on save
        when(tasksDao.save(any(Tasks.class))).thenAnswer(invocation -> {
            Tasks task = invocation.getArgument(0); 
            task.setId(UUID.randomUUID().toString());
            task.setTitle(dto.getTitle());
            task.setDescription(dto.getDescription());
            task.setDueDate(expectedDueDate);
            return task;
        });


        // When
        ResponseEntity<?> response = taskService.addTask(dto);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ArgumentCaptor<Tasks> captor = ArgumentCaptor.forClass(Tasks.class);
        verify(tasksDao).save(captor.capture());
        Tasks capturedTask = captor.getValue();
       
        // Store the ID for other tests
        createdTaskId = capturedTask.getId();

        assertThat(capturedTask.getDueDate()).isEqualTo(expectedDueDate);
        assertThat(capturedTask.getTitle()).isEqualTo(dto.getTitle());
        assertThat(capturedTask.getDescription()).isEqualTo(dto.getDescription());
    }

    @Test
    void getTasks_ReturnsPaginatedTasks() {
        // Given
        Pageable pageable = Pageable.ofSize(10).withPage(0);
        Page<Tasks> mockPage = mock(Page.class);
        when(tasksDao.findAll(pageable)).thenReturn(mockPage);
        when(mockPage.toList()).thenReturn(List.of(new Tasks(), new Tasks()));
        // When
        ResponseEntity<?> response = taskService.getTasks(pageable);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(tasksDao).findAll(pageable);
        verify(dataResponse).responseData(200, "Tasks retrieve successfully", mockPage.toList());
    }

    @Test
    void updateTask_ExistingId_UpdatesStatusAndReturnsOk() {
        // Given
        UpdateTaskDto updateDto = new UpdateTaskDto(Tasks.TaskStatus.IN_PROGRESS);
        Tasks existingTask = new Tasks();
        existingTask.setStatus(updateDto.getStatus());

        when(tasksDao.findTasksById(createdTaskId)).thenReturn(existingTask);
        when(tasksDao.save(existingTask)).thenReturn(existingTask);

        // When
        ResponseEntity<?> response = taskService.updateTask(createdTaskId, updateDto);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(existingTask.getStatus()).isEqualTo(Tasks.TaskStatus.IN_PROGRESS);
        verify(tasksDao).save(existingTask);
        verify(dataResponse).responseData(200, "Task updated successfully", existingTask);
    }

    @Test
    void updateTask_NonExistingId_ReturnsNotFound() {
        // Given
        String taskId = "ef279fa1-cffa-41e8-860c-5c4e8bafbce5";
        UpdateTaskDto updateDto = new UpdateTaskDto(Tasks.TaskStatus.IN_PROGRESS);
        Tasks existingTask = new Tasks();
        existingTask.setStatus(updateDto.getStatus());

        when(tasksDao.findTasksById(taskId)).thenReturn(null);

        // When
        ResponseEntity<?> response = taskService.updateTask(taskId, updateDto);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        verify(tasksDao, never()).save(any());
        verify(dataResponse).responseData(404, "Task not found", null);
    }

    @Test
    void getTask_ExistingId_ReturnsTask() {
        // Given
        Tasks task = new Tasks();
        task.setId(createdTaskId);
        when(tasksDao.findTasksById(createdTaskId)).thenReturn(task);

        // When
        ResponseEntity<?> response = taskService.getTask(createdTaskId);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(tasksDao).findTasksById(createdTaskId);
        verify(dataResponse).responseData(200, "Task retrieve successfully", task);
    }

    @Test
    void getTask_NonExistingId_ReturnsNotFound() {
        // Given
        String taskId = "ef279fa1-cffa-41e8-860c-5c4e8bafbce5";
        when(tasksDao.findTasksById(taskId)).thenReturn(null);

        // When
        ResponseEntity<?> response = taskService.getTask(taskId);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        verify(tasksDao).findTasksById(taskId);
        verify(dataResponse).responseData(404, "Task not found", null);
    }

    @Test
    void deleteTask_ExistingId_ReturnsNoContent() {
        System.out.println(createdTaskId);
        // Given
        Tasks task = new Tasks();
        task.setId(createdTaskId);
        when(tasksDao.findTasksById(createdTaskId)).thenReturn(task);
        // When
        ResponseEntity<?> response = taskService.deleteTask(createdTaskId);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(tasksDao).delete(task);
        verify(dataResponse).responseData(204, "Task deleted successfully", null);
    }

    @Test
    void deleteTask_NonExistingId_ReturnsNotFound() {
        // Given
        String taskId = "ef279fa1-cffa-41e8-860c-5c4e8bafbce5";
        when(tasksDao.findTasksById(taskId)).thenReturn(null);

        // When
        ResponseEntity<?> response = taskService.deleteTask(taskId);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        verify(tasksDao, never()).delete(any());
        verify(dataResponse).responseData(404, "Task not found", null);
    }

}
