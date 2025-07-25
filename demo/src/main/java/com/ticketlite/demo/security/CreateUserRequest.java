package com.ticketlite.demo.security;

import jakarta.validation.constraints.NotBlank;

import java.util.Set;

public class CreateUserRequest {
    private String email;
    @NotBlank(message = "La contraseña es obligatoria")
    private String password;
    private String name;
    private String lastName;
    private String phone;
    private String city;
    private String bio;
    private String avatarUrl;
    private Set<Long> roleIds;

    // Getters y Setters
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

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }

    public String getBio() {
        return bio;
    }
    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }
    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public Set<Long> getRoleIds() {
        return roleIds;
    }
    public void setRoleIds(Set<Long> roleIds) {
        this.roleIds = roleIds;
    }
}