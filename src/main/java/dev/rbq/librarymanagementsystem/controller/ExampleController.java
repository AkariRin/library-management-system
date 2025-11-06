package dev.rbq.librarymanagementsystem.controller;

import dev.rbq.librarymanagementsystem.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 示例控制器 - 演示权限控制
 *
 * 使用方法：
 * 1. 不加注解的接口：任何人都可以访问（包括未登录用户）
 * 2. @PreAuthorize("isAuthenticated()")：需要登录才能访问
 * 3. @PreAuthorize("hasRole('USER')")：需要普通用户角色
 * 4. @PreAuthorize("hasRole('ADMIN')")：需要管理员角色
 */
@RestController
@RequestMapping("/api/example")
public class ExampleController {

    /**
     * 公开接口 - 无需认证
     */
    @GetMapping("/public")
    public ResponseEntity<ApiResponse<String>> publicEndpoint() {
        return ResponseEntity.ok(ApiResponse.success("这是一个公开接口，无需登录"));
    }

    /**
     * 需要登录的接口
     */
    @GetMapping("/authenticated")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<String>> authenticatedEndpoint() {
        return ResponseEntity.ok(ApiResponse.success("这是一个需要登录的接口"));
    }

    /**
     * 需要普通用户权限的接口
     */
    @GetMapping("/user")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<String>> userEndpoint() {
        return ResponseEntity.ok(ApiResponse.success("这是一个需要普通用户权限的接口"));
    }

    /**
     * 需要管理员权限的接口
     */
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> adminEndpoint() {
        return ResponseEntity.ok(ApiResponse.success("这是一个需要管理员权限的接口"));
    }

    /**
     * 需要管理员或普通用户权限的接口
     */
    @GetMapping("/user-or-admin")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<String>> userOrAdminEndpoint() {
        return ResponseEntity.ok(ApiResponse.success("这是一个需要用户或管理员权限的接口"));
    }
}

