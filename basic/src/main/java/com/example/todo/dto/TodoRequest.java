package com.example.todo.dto;

import com.example.todo.entity.TodoStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TodoRequest {
    private String title;
    private String description;
    private TodoStatus status;
}
