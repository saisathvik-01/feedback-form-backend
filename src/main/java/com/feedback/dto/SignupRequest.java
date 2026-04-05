package com.feedback.dto;

import com.feedback.model.User;

public class SignupRequest {
    private String username;
    private String email;
    private String password;
    private User.Role role;
    private String facultyName;
    private String section;

    public SignupRequest() {}

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public User.Role getRole() { return role; }
    public void setRole(User.Role role) { this.role = role; }

    public String getFacultyName() { return facultyName; }
    public void setFacultyName(String facultyName) { this.facultyName = facultyName; }

    public String getSection() { return section; }
    public void setSection(String section) { this.section = section; }
}