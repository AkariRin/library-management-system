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

    @NotBlank(message = "Username cannot be empty")
    @Size(max = 50, message = "Username length cannot exceed 50 characters")
    @Column(name = "username", length = 50, nullable = false, unique = true)
    private String username;

    @NotBlank(message = "Name cannot be left blank")
    @Size(max = 12, message = "Names must not exceed 12 characters in length")
    @Column(name = "name", length = 12, nullable = false)
    private String name;

    @NotBlank(message = "Password cannot be empty")
    @Size(max = 60, message = "Password length must not exceed 60 characters")
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
