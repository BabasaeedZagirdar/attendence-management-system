package com.college.attendance.entity;

import jakarta.persistence.*;
import java.time.*;

@Entity @Table(name = "password_reset_token")

public class PasswordResetToken {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true) private String token;
    @ManyToOne(optional = false) @JoinColumn(name = "user_id") private User user;
    @Column(nullable = false) private LocalDateTime expiresAt;
    private boolean used;

    public PasswordResetToken() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
    public boolean isUsed() { return used; }
    public void setUsed(boolean used) { this.used = used; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String token;
        private User user;
        private LocalDateTime expiresAt;
        private boolean used;
        public Builder id(Long id) { this.id = id; return this; }
        public Builder token(String token) { this.token = token; return this; }
        public Builder user(User user) { this.user = user; return this; }
        public Builder expiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; return this; }
        public Builder used(boolean used) { this.used = used; return this; }
        public PasswordResetToken build() {
            PasswordResetToken o = new PasswordResetToken();
            o.setId(id);
            o.setToken(token);
            o.setUser(user);
            o.setExpiresAt(expiresAt);
            o.setUsed(used);
            return o;
        }
    }
}
