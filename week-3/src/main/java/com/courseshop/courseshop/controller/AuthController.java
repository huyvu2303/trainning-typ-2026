package com.courseshop.courseshop.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.courseshop.courseshop.model.LoginRequest;
import com.courseshop.courseshop.model.TokenResponse;
import com.courseshop.courseshop.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final String SESSION_USERNAME = "authenticatedUsername";

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // SESSION: server stores the login state; the browser sends JSESSIONID cookie on later requests.
    @PostMapping("/session/login")
    public ResponseEntity<?> loginWithSession(
            @RequestBody LoginRequest loginRequest,
            HttpServletRequest request) {
        if (!authService.authenticate(loginRequest)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Sai username hoặc password"));
        }

        HttpSession session = request.getSession(true);
        request.changeSessionId();
        session.setAttribute(SESSION_USERNAME, loginRequest.getUsername());
        return ResponseEntity.ok(Map.of("message", "Đăng nhập session thành công"));
    }

    @GetMapping("/session/me")
    public ResponseEntity<?> getSessionUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute(SESSION_USERNAME) == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Chưa đăng nhập bằng session"));
        }
        return ResponseEntity.ok(Map.of("username", session.getAttribute(SESSION_USERNAME)));
    }

    @PostMapping("/session/logout")
    public ResponseEntity<Void> logoutSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return ResponseEntity.noContent().build();
    }

    // TOKEN: server returns a signed JWT; client sends it in Authorization: Bearer <token>.
    @PostMapping("/token/login")
    public ResponseEntity<?> loginWithToken(@RequestBody LoginRequest loginRequest) {
        if (!authService.authenticate(loginRequest)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Sai username hoặc password"));
        }

        String token = authService.createAccessToken(loginRequest.getUsername());
        return ResponseEntity.ok(new TokenResponse(
                token, "Bearer", AuthService.TOKEN_LIFETIME_SECONDS));
    }

    @GetMapping("/token/me")
    public ResponseEntity<?> getTokenUser(
            @RequestHeader(name = "Authorization", required = false) String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Thiếu Bearer token"));
        }

        String token = authorization.substring("Bearer ".length());
        if (!authService.isValidToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Token không hợp lệ hoặc đã hết hạn"));
        }

        return ResponseEntity.ok(Map.of(
                "username", authService.getUsernameFromValidToken(token)));
    }
}
