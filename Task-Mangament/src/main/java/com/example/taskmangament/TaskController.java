package com.example.taskmangament;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TaskController{
    private TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public List<Task>  getAllTask(){
        return taskService.getAll();
    }

    @GetMapping("/{id}")
    public Task getTaskById(@PathVariable("id") Long id){
        return taskService.getById(id);
    }


}
