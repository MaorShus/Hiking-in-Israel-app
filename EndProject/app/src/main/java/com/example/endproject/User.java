package com.example.endproject;


// User.java
public class User {

    private String username;

    private static User user;

    private String password;

    private User() {
        this.username = "";
        this.password = "";
    }

    public static User getInstance(){
        if(user==null)
            user = new User();
        return user;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }




    // Getters and setters
}