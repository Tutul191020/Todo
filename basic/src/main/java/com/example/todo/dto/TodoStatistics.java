package com.example.todo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TodoStatistics {
    private int totalTodos;
    private int completedTodos;
    private int inProgressTodos;
    private int todoTodos;
    private int month;
    private int year;
    private double completionRate;
}
