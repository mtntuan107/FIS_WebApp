package com.example.api.controller;

import com.example.api.entity.Order;
import com.example.api.repository.OrderRepository;
import com.example.api.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.variable.VariableMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/camunda")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class CamundaController {

    @Autowired
    private ProcessEngine processEngine;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @PostMapping("/start-process")
    public ResponseEntity<String> startProcess(@RequestParam String processKey, @RequestParam String username, @RequestParam Long orderId) {
        ProcessInstance processInstance = processEngine.getRuntimeService()
                .startProcessInstanceByKey(processKey, username);
        Order order = orderRepository.getReferenceById(orderId);
        if(order != null){
            order.setProcessId(processInstance.getId());
            orderRepository.save(order);
        }

        return new ResponseEntity<>(processInstance.getId(), HttpStatus.OK);
    }

    @PutMapping("/task/change-status")
    public String changeTaskStatus(
            @RequestParam String processInstanceId,
            @RequestParam String taskDefinitionKey,
            @RequestParam(required = false) String assignee) {
        TaskService taskService = processEngine.getTaskService();

        // Truy vấn các task theo processInstanceId và taskDefinitionKey
        List<Task> tasks = taskService.createTaskQuery()
                .processInstanceId(processInstanceId)  // Lọc theo processInstanceId
                .taskDefinitionKey(taskDefinitionKey)  // Lọc theo taskDefinitionKey
                .list();

        if (tasks.isEmpty()) {
            return "No tasks found for the given process instance and task definition key.";
        }

        for (Task task : tasks) {
            // Nếu cần claim task
            if (assignee != null && !assignee.isEmpty()) {
                taskService.claim(task.getId(), assignee);
                return "Task claimed by: " + assignee;
            } else {
                // Nếu unclaim task
                taskService.setAssignee(task.getId(), null);
                return "Task unclaimed";
            }
        }

        return "Task not found for the given process instance and task definition key.";
    }


    @PutMapping("/task/complete")
    public String completeTask(
            @RequestParam String processInstanceId,
            @RequestParam String taskDefinitionKey,
            @RequestParam String assignee,  // Thêm assignee để xác định người hoàn thành task
            @RequestBody(required = false) Map<String, Object> variables) {
        TaskService taskService = processEngine.getTaskService();

        // Truy vấn các task theo processInstanceId và taskDefinitionKey
        List<Task> tasks = taskService.createTaskQuery()
                .processInstanceId(processInstanceId)
                .taskDefinitionKey(taskDefinitionKey)
                .taskAssignee(assignee)  // Lọc theo assignee
                .list();

        if (tasks.isEmpty()) {
            return "No tasks found for the given process instance, task definition key, or assignee.";
        }

        // Nếu có task và assignee đúng, hoàn thành task
        for (Task task : tasks) {
            if (task != null) {
                // Nếu không có biến, hoàn thành task mà không truyền biến
                if (variables == null || variables.isEmpty()) {
                    taskService.complete(task.getId());
                } else {
                    taskService.complete(task.getId(), variables);
                }
                return "Task completed by assignee: " + assignee;
            }
        }

        return "No task found for the given parameters.";
    }

    @GetMapping("/tasks")
    public String getAllTasks() {
        TaskService taskService = processEngine.getTaskService();

        List<Task> tasks = taskService.createTaskQuery().initializeFormKeys().list();

        for (Task task : tasks) {
            System.out.println("Task ID: " + task.getId() + ", Task Name: " + task.getName() + "get id " + task.getTaskDefinitionKey());
        }

        return "OK";
    }

    @PutMapping("/task/set-variables")
    public String setTaskVariables(@RequestParam String taskDefinitionKey, @RequestParam String processInstanceId, @RequestParam Map<String, Object> variables) {
        TaskService taskService = processEngine.getTaskService();

        List<Task> tasks = taskService.createTaskQuery()
                .taskDefinitionKey(taskDefinitionKey)
                .processInstanceId(processInstanceId)
                .list();

        if (tasks != null && !tasks.isEmpty()) {
            for (Task task : tasks) {
                taskService.setVariables(task.getId(), variables);
            }

            return "Variables added to tasks with taskDefinitionKey: " + taskDefinitionKey + " in process instance: " + processInstanceId;
        } else {
            return "No task found with the given taskDefinitionKey and processInstanceId";
        }
    }
}
