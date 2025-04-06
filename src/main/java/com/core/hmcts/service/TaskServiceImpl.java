package com.core.hmcts.service;

import com.core.hmcts.handler.DataResponse;
import com.core.hmcts.model.dao.TasksDao;
import com.core.hmcts.model.dto.CreateTaskDto;
import com.core.hmcts.model.dto.UpdateTaskDto;
import com.core.hmcts.model.entity.Tasks;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Service
public class TaskServiceImpl implements TaskService{
    private final DataResponse dataResponse;

    private final TasksDao tasksDao;

    public TaskServiceImpl(DataResponse dataResponse, TasksDao tasksDao) {
        this.dataResponse = dataResponse;
        this.tasksDao = tasksDao;
    }


    @Override
    public ResponseEntity<?> addTask(CreateTaskDto task) {
        Tasks tasks = new Tasks();
        tasks.setTitle(task.getTitle());
        tasks.setDescription(task.getDescription());
        tasks.setDueDate(dateTimeFormatter(task.getDueDate()));
        tasksDao.save(tasks);

        // Construct URI for the created resource
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(tasks.getId())
                .toUri();
        return ResponseEntity.created(location).body(dataResponse.responseData(201, "Task created successfully", tasks));
    }

    @Override
    public ResponseEntity<?> deleteTask(String id) {
        Tasks tasks = tasksDao.findTasksById(id);
        if (Objects.isNull(tasks)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(dataResponse.responseData(404, "Task not found", null));
        }
        tasksDao.delete(tasks);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(dataResponse.responseData(204, "Task deleted successfully", null));
    }

    @Override
    public ResponseEntity<?> getTask(String id) {
        Tasks tasks = tasksDao.findTasksById(id);
        if (Objects.isNull(tasks)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(dataResponse.responseData(404, "Task not found", null));
        }
        return ResponseEntity.ok().body(dataResponse.responseData(200, "Task retrieve successfully", tasks));
    }

    @Override
    public ResponseEntity<?> getTasks(Pageable pageable) {
        Page<Tasks> tasks = tasksDao.findAll(pageable);
        return ResponseEntity.ok().body(dataResponse.responseData(200, "Tasks retrieve successfully", tasks.toList()));
    }

    @Override
    public ResponseEntity<?> updateTask(String id, UpdateTaskDto updateTaskDto) {
        Tasks tasks = tasksDao.findTasksById(id);
        if (Objects.isNull(tasks)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(dataResponse.responseData(404, "Task not found", null));
        }
        tasks.setStatus(updateTaskDto.getStatus());
        tasksDao.save(tasks);
        return ResponseEntity.ok().body(dataResponse.responseData(200, "Task updated successfully", tasks));
    }

    private LocalDateTime dateTimeFormatter(String datetime){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        return LocalDateTime.parse(datetime, formatter);
    }

}
