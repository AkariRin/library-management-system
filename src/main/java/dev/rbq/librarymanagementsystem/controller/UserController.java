package dev.rbq.librarymanagementsystem.controller;

import dev.rbq.librarymanagementsystem.dto.ApiResponse;
import dev.rbq.librarymanagementsystem.dto.auth.AuthResponse;
import dev.rbq.librarymanagementsystem.dto.user.ChangePasswordRequest;
import dev.rbq.librarymanagementsystem.dto.user.ChangeUsernameRequest;
import dev.rbq.librarymanagementsystem.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<AuthResponse>> getCurrentUser() {
        try {
            AuthResponse response = userService.getCurrentUser();
            if (response == null) {
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("未登录"));
            }
            return ResponseEntity.ok(ApiResponse.success("获取用户信息成功", response));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("获取用户信息失败：" + e.getMessage()));
        }
    }

    /**
     * 修改密码
     * @param userUuid 用户UUID
     * @param request 修改密码请求
     */
    @PutMapping("/{userUuid}/password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @PathVariable String userUuid,
            @Valid @RequestBody ChangePasswordRequest request) {
        try {
            userService.changePassword(userUuid, request);
            return ResponseEntity.ok(ApiResponse.success("密码修改成功", null));
        } catch (SecurityException e) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (BadCredentialsException | IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("密码修改失败：" + e.getMessage()));
        }
    }

    /**
     * 修改用户名
     * @param userUuid 用户UUID
     * @param request 修改用户名请求
     */
    @PutMapping("/{userUuid}/username")
    public ResponseEntity<ApiResponse<AuthResponse>> changeUsername(
            @PathVariable String userUuid,
            @Valid @RequestBody ChangeUsernameRequest request) {
        try {
            AuthResponse response = userService.changeUsername(userUuid, request);
            return ResponseEntity.ok(ApiResponse.success("用户名修改成功", response));
        } catch (SecurityException e) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("用户名修改失败：" + e.getMessage()));
        }
    }
}

