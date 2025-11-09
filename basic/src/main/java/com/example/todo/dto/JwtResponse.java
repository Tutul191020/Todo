package com.example.todo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class JwtResponse {
    private String token;
    private String tokenType;
    private Instant expiresAt;
    private String username;
    private String role;
}