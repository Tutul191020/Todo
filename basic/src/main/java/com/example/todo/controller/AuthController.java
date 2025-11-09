package com.example.todo.controller;

import com.example.todo.common.ApiResponse;
import com.example.todo.dto.AdminRegisterRequest;
import com.example.todo.dto.JwtResponse;
import com.example.todo.dto.LoginRequest;
import com.example.todo.dto.RegisterRequest;
import com.example.todo.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/public/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<JwtResponse>> login(@RequestBody LoginRequest request) {
        ApiResponse<JwtResponse> response = authService.login(request);
        return ResponseEntity.status(response.isSuccess() ? 200 : 401).body(response);
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(@RequestBody RegisterRequest request) {
        ApiResponse<String> response = authService.register(request);
        return ResponseEntity.status(response.isSuccess() ? 201 : 400).body(response);
    }

    @PostMapping("/admin/register")
    public ResponseEntity<ApiResponse<String>> registerAdmin(@RequestBody AdminRegisterRequest request) {
        ApiResponse<String> response = authService.registerAdmin(request);
        return ResponseEntity.status(response.isSuccess() ? 201 : 400).body(response);
    }
}