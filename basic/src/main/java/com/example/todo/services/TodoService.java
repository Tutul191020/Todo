package com.example.todo.services;

import com.example.todo.common.ApiResponse;
import com.example.todo.dto.TodoRequest;
import com.example.todo.dto.TodoResponse;
import com.example.todo.entity.TodoItem;
import com.example.todo.entity.TodoStatus;
import com.example.todo.entity.UserAccount;
import com.example.todo.repositories.TodoRepository;
import com.example.todo.repositories.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TodoService {

    private final TodoRepository todoRepository;
    private final UserAccountRepository userAccountRepository;

    private UserAccount getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userAccountRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private TodoResponse convertToResponse(TodoItem todo) {
        return TodoResponse.builder()
                .id(todo.getId())
                .title(todo.getTitle())
                .description(todo.getDescription())
                .status(todo.getStatus())
                .createdAt(todo.getCreatedAt())
                .updatedAt(todo.getUpdatedAt())
                .userId(todo.getUser().getId())
                .username(todo.getUser().getUsername())
                .build();
    }

    @Transactional
    public ApiResponse<TodoResponse> createTodo(TodoRequest request) {
        try {
            if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
                return ApiResponse.failure("Title is required");
            }

            UserAccount user = getCurrentUser();

            TodoItem todo = new TodoItem();
            todo.setTitle(request.getTitle());
            todo.setDescription(request.getDescription());
            todo.setStatus(request.getStatus() != null ? request.getStatus() : TodoStatus.TODO);
            todo.setUser(user);

            TodoItem savedTodo = todoRepository.save(todo);
            return ApiResponse.success("Todo created successfully", convertToResponse(savedTodo));
        } catch (Exception e) {
            return ApiResponse.failure("Failed to create todo: " + e.getMessage());
        }
    }

    public ApiResponse<List<TodoResponse>> getAllTodos() {
        try {
            UserAccount user = getCurrentUser();
            List<TodoItem> todos = todoRepository.findByUserId(user.getId());
            List<TodoResponse> responses = todos.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
            return ApiResponse.success("Todos retrieved successfully", responses);
        } catch (Exception e) {
            return ApiResponse.failure("Failed to retrieve todos: " + e.getMessage());
        }
    }

    public ApiResponse<TodoResponse> getTodoById(Long id) {
        try {
            UserAccount user = getCurrentUser();
            TodoItem todo = todoRepository.findByIdAndUserId(id, user.getId())
                    .orElseThrow(() -> new RuntimeException("Todo not found or access denied"));
            return ApiResponse.success("Todo retrieved successfully", convertToResponse(todo));
        } catch (Exception e) {
            return ApiResponse.failure("Failed to retrieve todo: " + e.getMessage());
        }
    }

    @Transactional
    public ApiResponse<TodoResponse> updateTodo(Long id, TodoRequest request) {
        try {
            UserAccount user = getCurrentUser();
            TodoItem todo = todoRepository.findByIdAndUserId(id, user.getId())
                    .orElseThrow(() -> new RuntimeException("Todo not found or access denied"));

            if (request.getTitle() != null && !request.getTitle().trim().isEmpty()) {
                todo.setTitle(request.getTitle());
            }
            if (request.getDescription() != null) {
                todo.setDescription(request.getDescription());
            }
            if (request.getStatus() != null) {
                todo.setStatus(request.getStatus());
            }

            TodoItem updatedTodo = todoRepository.save(todo);
            return ApiResponse.success("Todo updated successfully", convertToResponse(updatedTodo));
        } catch (Exception e) {
            return ApiResponse.failure("Failed to update todo: " + e.getMessage());
        }
    }

    @Transactional
    public ApiResponse<Void> deleteTodo(Long id) {
        try {
            UserAccount user = getCurrentUser();
            TodoItem todo = todoRepository.findByIdAndUserId(id, user.getId())
                    .orElseThrow(() -> new RuntimeException("Todo not found or access denied"));
            todoRepository.delete(todo);
            return ApiResponse.success("Todo deleted successfully", null);
        } catch (Exception e) {
            return ApiResponse.failure("Failed to delete todo: " + e.getMessage());
        }
    }

    @Transactional
    public ApiResponse<TodoResponse> toggleTodoStatus(Long id) {
        try {
            UserAccount user = getCurrentUser();
            TodoItem todo = todoRepository.findByIdAndUserId(id, user.getId())
                    .orElseThrow(() -> new RuntimeException("Todo not found or access denied"));

            TodoStatus newStatus = todo.getStatus() == TodoStatus.TODO
                    ? TodoStatus.COMPLETED
                    : TodoStatus.TODO;
            todo.setStatus(newStatus);

            TodoItem updatedTodo = todoRepository.save(todo);
            return ApiResponse.success("Todo status toggled successfully", convertToResponse(updatedTodo));
        } catch (Exception e) {
            return ApiResponse.failure("Failed to toggle todo status: " + e.getMessage());
        }
    }

    public ApiResponse<List<TodoResponse>> getAllTodosForAdmin() {
        try {
            List<TodoItem> todos = todoRepository.findAll();
            List<TodoResponse> responses = todos.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
            return ApiResponse.success("All todos retrieved successfully", responses);
        } catch (Exception e) {
            return ApiResponse.failure("Failed to retrieve todos: " + e.getMessage());
        }
    }

    public ApiResponse<com.example.todo.dto.TodoStatistics> getMonthlyStatistics(int year, int month) {
        try {
            UserAccount user = getCurrentUser();

            // Get all todos for the month
            List<TodoItem> monthlyTodos = todoRepository.findByUserIdAndYearAndMonth(user.getId(), year, month);

            // Count by status
            long completedCount = monthlyTodos.stream()
                    .filter(t -> t.getStatus() == TodoStatus.COMPLETED || t.getStatus() == TodoStatus.DONE)
                    .count();
            long inProgressCount = monthlyTodos.stream()
                    .filter(t -> t.getStatus() == TodoStatus.IN_PROGRESS)
                    .count();
            long todoCount = monthlyTodos.stream()
                    .filter(t -> t.getStatus() == TodoStatus.TODO)
                    .count();

            int total = monthlyTodos.size();
            double completionRate = total > 0 ? (completedCount * 100.0 / total) : 0.0;

            com.example.todo.dto.TodoStatistics stats = new com.example.todo.dto.TodoStatistics(
                    total,
                    (int) completedCount,
                    (int) inProgressCount,
                    (int) todoCount,
                    month,
                    year,
                    Math.round(completionRate * 100.0) / 100.0);

            return ApiResponse.success("Statistics retrieved successfully", stats);
        } catch (Exception e) {
            return ApiResponse.failure("Failed to retrieve statistics: " + e.getMessage());
        }
    }
}
