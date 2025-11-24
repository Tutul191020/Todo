package com.example.todo.repositories;

import com.example.todo.entity.TodoItem;
import com.example.todo.entity.TodoStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TodoRepository extends JpaRepository<TodoItem, Long> {

    List<TodoItem> findByUserId(Long userId);

    List<TodoItem> findByUserIdAndStatus(Long userId, TodoStatus status);

    Long countByUserId(Long userId);

    Optional<TodoItem> findByIdAndUserId(Long id, Long userId);

    // Monthly statistics queries
    @Query("SELECT t FROM TodoItem t WHERE t.user.id = :userId AND YEAR(t.createdAt) = :year AND MONTH(t.createdAt) = :month")
    List<TodoItem> findByUserIdAndYearAndMonth(@Param("userId") Long userId, @Param("year") int year,
            @Param("month") int month);

    @Query("SELECT COUNT(t) FROM TodoItem t WHERE t.user.id = :userId AND t.status = :status AND YEAR(t.createdAt) = :year AND MONTH(t.createdAt) = :month")
    Long countByUserIdAndStatusAndYearAndMonth(@Param("userId") Long userId, @Param("status") TodoStatus status,
            @Param("year") int year, @Param("month") int month);
}
