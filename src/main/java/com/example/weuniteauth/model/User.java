package com.example.weuniteauth.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Entity
@NoArgsConstructor
@Table(name = "user")
public class User {

    public User(String name, String username, String email, String password) {
        this.name = name;
        this.username = username;
        this.email = email;
        this.password = password;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    @Size(min = 5, max = 100)
    @NotNull
    private String name;

    @Column(nullable = false, unique = true, length = 30)
    @Size(min = 5, max = 30)
    @NotNull
    private String username;

    @Column(nullable = false, unique = true, length = 50)
    @Email
    @Size(max = 50)
    @NotNull
    private String email;

    @Column(nullable = false)
    @NotNull
    private String password;

}
