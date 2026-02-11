package com.example.taskmangament;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TaskService {


    private Map<Long, Task> data = new HashMap<>();


    public TaskService() {
        create(new Task(
                1L,
                1L,
                1L,
                Status.CREATED,
                LocalDateTime.now(),
                LocalDateTime.now(),
                Priority.HIGH

        ));

        create(new Task(
                2L,
                1L,
                1L,
                Status.CREATED,
                LocalDateTime.now(),
                LocalDateTime.now(),
                Priority.LOW
        ));

        create(new Task(
                3L,
                1L,
                1L,
                Status.IN_PROGRESS,
                LocalDateTime.now(),
                LocalDateTime.now(),
                Priority.MEDIUM
        ));
    }
    public void create(Task task) {
        data.put(task.id(), task);
    }



    public Task getById(Long id) {
        return data.get(id);
    }

    public List<Task> getAll(){
        return data.values().stream().toList();

    }




}
