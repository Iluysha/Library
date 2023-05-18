package com.epam.library.entity;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name="users")
public class User {

    public enum Role {
        ADMIN,
        LIBRARIAN,
        READER
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 64, nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(length = 45, nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private Role role;

    @Column(nullable = false)
    private long fine;

    @Column(nullable = false)
    private boolean blocked;

    public User() {}

    public User(String name, String email, String password, Role role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public long getFine() {
        return fine;
    }

    public void addFine(long add) {
        fine += add;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return fine == user.fine && blocked == user.blocked && name.equals(user.name) &&
                email.equals(user.email) && password.equals(user.password) && role == user.role;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, email, password, role, fine, blocked);
    }
}
