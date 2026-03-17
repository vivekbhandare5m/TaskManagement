package com.example.taskmanager.service;

import com.example.taskmanager.dto.TaskRequestDTO;
import com.example.taskmanager.dto.TaskResponseDTO;
import com.example.taskmanager.entity.Task;
import com.example.taskmanager.exception.ResourceNotFoundException;
import com.example.taskmanager.repository.TaskRepository;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final ModelMapper modelMapper;

    public TaskServiceImpl(TaskRepository taskRepository,ModelMapper modelMapper) {
        this.taskRepository = taskRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public Page<TaskResponseDTO> getAllTasks(Pageable pageable) {
    	Page<Task> taskPage=taskRepository.findAll(pageable);
        return taskPage.map(task->modelMapper.map(task,TaskResponseDTO.class));
    }

    @Override
    public TaskResponseDTO getTaskById(Long id) {

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        return modelMapper.map(task,TaskResponseDTO.class);
    }

    @Override
    public TaskResponseDTO createTask(TaskRequestDTO request) {
        Task task = new Task();

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setPriority(request.getPriority());
        task.setDueDate(request.getDueDate());
        task.setStatus("PENDING");
        task.setCreatedAt(LocalDateTime.now());
        System.out.println(task);
        Task savedTask = taskRepository.save(task);

        return modelMapper.map(savedTask,TaskResponseDTO.class);
    }

    @Override
    public TaskResponseDTO updateTask(Long id, TaskRequestDTO request) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setPriority(request.getPriority());
        task.setDueDate(request.getDueDate());

        Task updatedTask = taskRepository.save(task);
        
        return modelMapper.map(updatedTask, TaskResponseDTO.class);
    }

    @Override
    public void deleteTask(Long id) {
        if(!taskRepository.existsById(id)){
            throw new ResourceNotFoundException("Task not found");
        }

        taskRepository.deleteById(id);
    }

    @Override
    public TaskResponseDTO updateStatus(Long id, String status) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        task.setStatus(status);
        Task updatedTask = taskRepository.save(task);

        return modelMapper.map(updatedTask, TaskResponseDTO.class);
    }

	
}