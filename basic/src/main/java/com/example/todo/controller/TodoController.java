package com.example.todo.controller;

import com.example.todo.common.ApiResponse;
import com.example.todo.dto.TodoRequest;
import com.example.todo.dto.TodoResponse;
import com.example.todo.services.TodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/todos")
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;

    @PostMapping
    public ResponseEntity<ApiResponse<TodoResponse>> createTodo(@RequestBody TodoRequest request) {
        ApiResponse<TodoResponse> response = todoService.createTodo(request);
        return ResponseEntity.status(response.isSuccess() ? 201 : 400).body(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TodoResponse>>> getAllTodos() {
        ApiResponse<List<TodoResponse>> response = todoService.getAllTodos();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TodoResponse>> getTodoById(@PathVariable Long id) {
        ApiResponse<TodoResponse> response = todoService.getTodoById(id);
        return ResponseEntity.status(response.isSuccess() ? 200 : 404).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TodoResponse>> updateTodo(
            @PathVariable Long id,
            @RequestBody TodoRequest request) {
        ApiResponse<TodoResponse> response = todoService.updateTodo(id, request);
        return ResponseEntity.status(response.isSuccess() ? 200 : 400).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTodo(@PathVariable Long id) {
        ApiResponse<Void> response = todoService.deleteTodo(id);
        return ResponseEntity.status(response.isSuccess() ? 200 : 400).body(response);
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<ApiResponse<TodoResponse>> toggleTodoStatus(@PathVariable Long id) {
        ApiResponse<TodoResponse> response = todoService.toggleTodoStatus(id);
        return ResponseEntity.status(response.isSuccess() ? 200 : 400).body(response);
    }

    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<TodoResponse>>> getAllTodosForAdmin() {
        ApiResponse<List<TodoResponse>> response = todoService.getAllTodosForAdmin();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/statistics")
    public ResponseEntity<ApiResponse<com.example.todo.dto.TodoStatistics>> getMonthlyStatistics(
            @RequestParam(defaultValue = "0") int year,
            @RequestParam(defaultValue = "0") int month) {

        // If year/month not provided, use current month
        if (year == 0 || month == 0) {
            java.time.LocalDate now = java.time.LocalDate.now();
            year = year == 0 ? now.getYear() : year;
            month = month == 0 ? now.getMonthValue() : month;
        }

        ApiResponse<com.example.todo.dto.TodoStatistics> response = todoService.getMonthlyStatistics(year, month);
        return ResponseEntity.ok(response);
    }
}
