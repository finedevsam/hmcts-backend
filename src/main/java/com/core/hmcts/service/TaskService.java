package com.core.hmcts.service;

import com.core.hmcts.model.dto.CreateTaskDto;
import com.core.hmcts.model.dto.UpdateTaskDto;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

public interface TaskService {
    ResponseEntity<?> addTask(CreateTaskDto task);
    ResponseEntity<?> deleteTask(String id);
    ResponseEntity<?> getTask(String id);
    ResponseEntity<?> getTasks(Pageable pageable);
    ResponseEntity<?> updateTask(String id, UpdateTaskDto updateTaskDto);
}
