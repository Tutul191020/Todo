package com.example.todo.repositories;

import com.example.todo.entity.AdminAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<AdminAccount, Long> {
    Optional<AdminAccount> findByEmail(String email);
}