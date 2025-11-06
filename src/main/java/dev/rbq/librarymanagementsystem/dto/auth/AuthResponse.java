package dev.rbq.librarymanagementsystem.dto.auth;

/**
 * 认证响应 DTO
 */
public class AuthResponse {

    private String uuid;
    private String username;
    private Boolean admin;

    // Constructors
    public AuthResponse() {
    }

    public AuthResponse(String uuid, String username, Boolean admin) {
        this.uuid = uuid;
        this.username = username;
        this.admin = admin;
    }

    // Getters and Setters
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Boolean getAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }
}

