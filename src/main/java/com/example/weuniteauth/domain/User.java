package com.example.weuniteauth.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@Entity
@NoArgsConstructor
@Table(name = "tb_user")
public class User extends BaseEntity {

    public User(String name, String username, String email, String password) {
        this.name = name;
        this.username = username;
        this.email = email;
        this.password = password;
        this.emailVerified = false;
        this.isPrivate = false;
    }

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 30)
    private String username;

    @Column(nullable = false, unique = true, length = 50)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private boolean emailVerified;

    @Column
    private String verificationToken;

    @Column
    private Instant verificationTokenExpires;

    @Column(nullable = false)
    private boolean isPrivate;

    @Column
    private String profileImg;

    @Column(nullable = false)
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "tb_user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "followed", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Follow> followers = new HashSet<>();

    @OneToMany(mappedBy = "follower", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Follow> following = new HashSet<>();

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Post> posts = new HashSet<>();

}
