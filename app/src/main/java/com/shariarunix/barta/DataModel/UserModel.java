package com.shariarunix.barta.DataModel;

public class UserModel {
    String userId, userName, userEmail, userPassword;
    long createdAt;
    boolean isLoggedIn;

    public UserModel() {
        // Default Empty Constructor;
    }

    public UserModel(String userId,
                     String userName,
                     String userEmail,
                     String userPassword,
                     long createdAt,
                     boolean isLoggedIn) {

        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPassword = userPassword;
        this.createdAt = createdAt;
        this.isLoggedIn = isLoggedIn;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        isLoggedIn = loggedIn;
    }
}
