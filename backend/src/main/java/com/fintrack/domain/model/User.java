package com.fintrack.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class User {

    private UUID id;
    private String email;
    private String passwordHash;
    private String firstName;
    private String lastName;
    private Role role;
    private boolean verified;
    private boolean active;
    private String oauthProvider;
    private String oauthId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private User() {}

    public static User create(String email, String passwordHash, String firstName, String lastName) {
        User user = new User();
        user.id = UUID.randomUUID();
        user.email = email;
        user.passwordHash = passwordHash;
        user.firstName = firstName;
        user.lastName = lastName;
        user.role = Role.ROLE_USER;
        user.verified = false;
        user.active = true;
        user.createdAt = LocalDateTime.now();
        user.updatedAt = LocalDateTime.now();
        return user;
    }

    public static User reconstitute(UUID id, String email, String passwordHash, String firstName,
                                     String lastName, Role role, boolean verified, boolean active,
                                     String oauthProvider, String oauthId,
                                     LocalDateTime createdAt, LocalDateTime updatedAt) {
        User user = new User();
        user.id = id;
        user.email = email;
        user.passwordHash = passwordHash;
        user.firstName = firstName;
        user.lastName = lastName;
        user.role = role;
        user.verified = verified;
        user.active = active;
        user.oauthProvider = oauthProvider;
        user.oauthId = oauthId;
        user.createdAt = createdAt;
        user.updatedAt = updatedAt;
        return user;
    }

    public void verify() {
        this.verified = true;
        this.updatedAt = LocalDateTime.now();
    }

    public void deactivate() {
        this.active = false;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateProfile(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.updatedAt = LocalDateTime.now();
    }

    public void changePassword(String newPasswordHash) {
        this.passwordHash = newPasswordHash;
        this.updatedAt = LocalDateTime.now();
    }

    public void changeRole(Role role) {
        this.role = role;
        this.updatedAt = LocalDateTime.now();
    }

    // Getters
    public UUID getId() { return id; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public Role getRole() { return role; }
    public boolean isVerified() { return verified; }
    public boolean isActive() { return active; }
    public String getOauthProvider() { return oauthProvider; }
    public String getOauthId() { return oauthId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
