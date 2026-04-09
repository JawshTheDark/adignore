package com.jawshthedark.adignore.data;

public class IgnoredUser {
    private String username;
    private String reason;

    public IgnoredUser() {
    }

    public IgnoredUser(String username, String reason) {
        this.username = username;
        this.reason = reason;
    }

    public String getUsername() {
        return username;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}