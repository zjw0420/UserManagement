package model;

import java.time.LocalDateTime;

/**
 * 用户实体类
 */
public class User {
    private int id;
    private String username;
    private String password;   // SHA-256 哈希
    private String role;       // ADMIN / USER
    private String email;
    private String phone;
    private int status;        // 1=正常, 0=禁用
    private LocalDateTime createdAt;

    public User() {}

    public User(int id, String username, String password, String role,
                String email, String phone, int status, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.email = email;
        this.phone = phone;
        this.status = status;
        this.createdAt = createdAt;
    }

    // Getters / Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getStatusText() {
        return status == 1 ? "正常" : "禁用";
    }

    public String getRoleText() {
        return "ADMIN".equals(role) ? "管理员" : "普通用户";
    }
}
