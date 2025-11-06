package dev.rbq.library_management_system.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 修改昵称请求 DTO
 */
public class ChangeDisplayNameRequest {

    @NotBlank(message = "用户UUID不能为空")
    private String userUuid;

    @NotBlank(message = "新昵称不能为空")
    @Size(min = 1, max = 12, message = "新昵称长度必须在1-12个字符之间")
    private String newName;

    // Constructors
    public ChangeDisplayNameRequest() {
    }

    public ChangeDisplayNameRequest(String userUuid, String newName) {
        this.userUuid = userUuid;
        this.newName = newName;
    }

    // Getters and Setters
    public String getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(String userUuid) {
        this.userUuid = userUuid;
    }

    public String getNewName() {
        return newName;
    }

    public void setNewName(String newName) {
        this.newName = newName;
    }
}

