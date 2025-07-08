package com.ticketlite.demo.security;

import jakarta.validation.constraints.NotBlank;

import java.util.Set;

public class RegisterRequestDTO {
    private String username;
    private String password;
    @NotBlank
    private String name;
    private String email;

    // Getters y setters

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
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
}
