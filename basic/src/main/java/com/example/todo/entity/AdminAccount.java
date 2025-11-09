package com.example.todo.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "admins")
@PrimaryKeyJoinColumn(name = "user_id")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AdminAccount extends UserAccount {

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private boolean isAdmin;
}
