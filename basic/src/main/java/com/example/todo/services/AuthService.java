package com.example.todo.services;

import com.example.todo.common.ApiResponse;
import com.example.todo.dto.AdminRegisterRequest;
import com.example.todo.dto.JwtResponse;
import com.example.todo.dto.LoginRequest;
import com.example.todo.dto.RegisterRequest;
import com.example.todo.entity.AccountStatus;
import com.example.todo.entity.AdminAccount;
import com.example.todo.entity.UserAccount;
import com.example.todo.repositories.AdminRepository;
import com.example.todo.repositories.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final UserAccountRepository userRepository;
    private final AdminRepository adminRepository;

    public ApiResponse<JwtResponse> login(LoginRequest request) {
        try {
            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

            UserPrincipal userDetails = (UserPrincipal) authentication.getPrincipal();
            UserAccount user = userDetails.user();

            boolean isAdmin = user instanceof AdminAccount admin && admin.isAdmin();

            String token = jwtUtil.generateToken(user.getUsername(), isAdmin);
            Instant expiresAt = jwtUtil.getExpirationInstant();

            JwtResponse jwtResponse = new JwtResponse(
                    token,
                    "Bearer",
                    expiresAt,
                    user.getUsername(),
                    isAdmin ? "ADMIN" : "USER");

            return ApiResponse.success("Login successful", jwtResponse);

        } catch (Exception e) {
            return ApiResponse.failure("Invalid username or password");
        }
    }

    public ApiResponse<String> register(RegisterRequest req) {
        if (userRepository.findByUsername(req.getUsername()).isPresent()) {
            return ApiResponse.failure("Username already exists");
        }

        UserAccount user = new UserAccount();
        user.setUsername(req.getUsername());
        user.setFirstName(req.getFirstName());
        user.setLastName(req.getLastName());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setStatus(AccountStatus.ENABLED);
        userRepository.save(user);
        return ApiResponse.success("User registered successfully", String.format(
                "User '%s' registered successfully with userName '%s' ", user.getFirstName(), user.getUsername()));
    }

    public ApiResponse<String> registerAdmin(AdminRegisterRequest req) {
        if (req.getUsername() == null || req.getUsername().isBlank())
            return ApiResponse.failure("Username cannot be empty");

        if (req.getPassword() == null || req.getPassword().isBlank())
            return ApiResponse.failure("Password cannot be empty");

        if (req.getEmail() == null || req.getEmail().isBlank())
            return ApiResponse.failure("Email cannot be empty");

        if (adminRepository.findByEmail(req.getEmail()).isPresent())
            return ApiResponse.failure("Email already in use");

        if (userRepository.findByUsername(req.getUsername()).isPresent())
            return ApiResponse.failure("Username already exists provide another one");

        AdminAccount admin = new AdminAccount();
        admin.setUsername(req.getUsername());
        admin.setFirstName(req.getFirstName());
        admin.setLastName(req.getLastName());
        admin.setPassword(passwordEncoder.encode(req.getPassword()));
        admin.setEmail(req.getEmail());
        admin.setStatus(AccountStatus.ENABLED);
        admin.setAdmin(true);

        adminRepository.save(admin);

        return ApiResponse.success(
                "Admin registered successfully",
                String.format("Admin '%s' registered with email '%s'", req.getUsername(), req.getEmail()));
    }
}
