package dev.rbq.librarymanagementsystem.service;

import dev.rbq.librarymanagementsystem.dto.auth.AuthResponse;
import dev.rbq.librarymanagementsystem.dto.auth.LoginRequest;
import dev.rbq.librarymanagementsystem.dto.auth.RegisterRequest;
import dev.rbq.librarymanagementsystem.entity.User;
import dev.rbq.librarymanagementsystem.repository.UserRepository;
import dev.rbq.librarymanagementsystem.security.UserDetailsImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * 认证服务
 */
@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    /**
     * 用户注册
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("用户名已存在");
        }

        // 创建新用户
        User user = new User();
        user.setUuid(UUID.randomUUID().toString());
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setAdmin(false); // 默认为普通用户

        // 保存用户
        user = userRepository.save(user);

        return new AuthResponse(
                user.getUuid(),
                user.getUsername(),
                user.getAdmin()
        );
    }

    /**
     * 用户登录
     */
    @Transactional
    public AuthResponse login(LoginRequest request, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        try {
            // 使用 Spring Security 进行认证
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            // 认证成功，设置安全上下文
            SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
            securityContext.setAuthentication(authentication);
            SecurityContextHolder.setContext(securityContext);

            // 创建或获取 session
            HttpSession session = httpRequest.getSession(true);
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, securityContext);

            // 设置 session 超时时间（remember me 功能）
            if (Boolean.TRUE.equals(request.getRememberMe())) {
                // 记住我：7天
                session.setMaxInactiveInterval(7 * 24 * 60 * 60);
            } else {
                // 不记住：30分钟（默认）
                session.setMaxInactiveInterval(30 * 60);
            }

            // 获取用户信息
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            User user = userDetails.getUser();

            return new AuthResponse(
                    user.getUuid(),
                    user.getUsername(),
                    user.getAdmin()
            );

        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("用户名或密码错误");
        }
    }

    /**
     * 用户登出
     */
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        // 清除安全上下文
        SecurityContextHolder.clearContext();

        // 使 session 失效
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }

    /**
     * 获取当前登录用户信息
     */
    public AuthResponse getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() ||
            authentication.getPrincipal().equals("anonymousUser")) {
            return null;
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userDetails.getUser();

        return new AuthResponse(
                user.getUuid(),
                user.getUsername(),
                user.getAdmin()
        );
    }
}

