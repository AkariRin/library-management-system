package dev.rbq.library_management_system.controller;

import dev.rbq.library_management_system.dto.ApiResponse;
import dev.rbq.library_management_system.dto.admin.AdminUserResponse;
import dev.rbq.library_management_system.dto.admin.UpdateAdminRequest;
import dev.rbq.library_management_system.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理员管理控制器
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    /**
     * 获取所有用户列表
     * @return 用户列表
     */
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<AdminUserResponse>>> getAllUsers() {
        try {
            List<AdminUserResponse> users = adminService.getAllUsers();
            return ResponseEntity.ok(ApiResponse.success("User list retrieved successfully", users));
        } catch (SecurityException e) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve user list: " + e.getMessage()));
        }
    }

    /**
     * 更新用户的管理员权限
     * @param request 更新请求
     * @return 更新后的用户信息
     */
    @PostMapping("/users/admin-status")
    public ResponseEntity<ApiResponse<AdminUserResponse>> updateAdminStatus(
            @Valid @RequestBody UpdateAdminRequest request) {
        try {
            AdminUserResponse response = adminService.updateAdminStatus(request);
            String message = request.getAdmin()
                    ? "Administrator privileges granted successfully"
                    : "Administrator privileges revoked successfully";
            return ResponseEntity.ok(ApiResponse.success(message, response));
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
                    .body(ApiResponse.error("Failed to update administrator privileges: " + e.getMessage()));
        }
    }

    /**
     * 删除用户
     * @param userUuid 用户UUID
     * @return 删除结果
     */
    @DeleteMapping("/users/{userUuid}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable String userUuid) {
        try {
            adminService.deleteUser(userUuid);
            return ResponseEntity.ok(ApiResponse.success("User deleted successfully", null));
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
                    .body(ApiResponse.error("Failed to delete user: " + e.getMessage()));
        }
    }
}
