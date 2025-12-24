package com.hust.soict.aims.entities;

import java.sql.Timestamp;

public class UserRole {
    private int userId;
    private int roleId;
    private Timestamp assignedAt;

    public UserRole() {}

    public UserRole(int userId, int roleId) {
        this.userId = userId;
        this.roleId = roleId;
    }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public int getRoleId() { return roleId; }
    public void setRoleId(int roleId) { this.roleId = roleId; }
    public Timestamp getAssignedAt() { return assignedAt; }
    public void setAssignedAt(Timestamp assignedAt) { this.assignedAt = assignedAt; }
}
