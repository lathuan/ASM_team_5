package com.example.asm_ad;

public class UserModel {
    private int id;
    private String username;
    private String fullName;
    private String email;
    private String phone;
    private int roleId;

    public UserModel(int id, String username, String fullName, String email, String phone, int roleId) {
        this.id = id;
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.roleId = roleId;
    }

    // getters / setters
    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public int getRoleId() { return roleId; }

    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setRoleId(int roleId) { this.roleId = roleId; }
}
