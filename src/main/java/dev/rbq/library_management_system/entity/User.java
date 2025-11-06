package dev.rbq.library_management_system.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

/**
 * 用户实体类
 */
@Entity
@Table(name = "user")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "uuid", length = 36, nullable = false)
    private String uuid;

    @NotBlank(message = "用户名不能为空")
    @Size(max = 50, message = "用户名长度不能超过50个字符")
    @Column(name = "username", length = 50, nullable = false, unique = true)
    private String username;

    @NotBlank(message = "昵称不能为空")
    @Size(max = 12, message = "昵称长度不能超过12个字符")
    @Column(name = "name", length = 12, nullable = false)
    private String name;

    @NotBlank(message = "密码不能为空")
    @Size(max = 60, message = "密码长度不能超过60个字符")
    @Column(name = "password", length = 60, nullable = false)
    private String password;

    @Column(name = "admin", nullable = false)
    private Boolean admin = false;

    // Constructors
    public User() {
    }

    public User(String uuid, String username, String name, String password, Boolean admin) {
        this.uuid = uuid;
        this.username = username;
        this.name = name;
        this.password = password;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }
}

