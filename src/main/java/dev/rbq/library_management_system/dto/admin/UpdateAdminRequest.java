package dev.rbq.library_management_system.dto.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 更新管理员权限请求 DTO
 */
public class UpdateAdminRequest {

    @NotBlank(message = "User UUID cannot be empty")
    private String userUuid;

    @NotNull(message = "Admin status cannot be null")
    private Boolean admin;

    // Constructors
    public UpdateAdminRequest() {
    }

    public UpdateAdminRequest(String userUuid, Boolean admin) {
        this.userUuid = userUuid;
        this.admin = admin;
    }

    // Getters and Setters
    public String getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(String userUuid) {
        this.userUuid = userUuid;
    }

    public Boolean getAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }
}
