package com.core.hmcts.model.dao;

import com.core.hmcts.model.entity.Tasks;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TasksDao extends JpaRepository<Tasks, String> {
    Tasks findTasksById(String id);
}
