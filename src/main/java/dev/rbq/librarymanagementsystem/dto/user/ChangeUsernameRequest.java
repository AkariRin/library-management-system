package dev.rbq.librarymanagementsystem.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 修改用户名请求 DTO
 */
public class ChangeUsernameRequest {

    @NotBlank(message = "用户UUID不能为空")
    private String userUuid;

    @NotBlank(message = "新用户名不能为空")
    @Size(min = 3, max = 50, message = "新用户名长度必须在3-50个字符之间")
    private String newUsername;

    // Constructors
    public ChangeUsernameRequest() {
    }

    public ChangeUsernameRequest(String userUuid, String newUsername) {
        this.userUuid = userUuid;
        this.newUsername = newUsername;
    }

    // Getters and Setters
    public String getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(String userUuid) {
        this.userUuid = userUuid;
    }

    public String getNewUsername() {
        return newUsername;
    }

    public void setNewUsername(String newUsername) {
        this.newUsername = newUsername;
    }
}

