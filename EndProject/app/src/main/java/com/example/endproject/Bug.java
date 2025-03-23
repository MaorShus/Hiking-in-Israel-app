package com.example.endproject;

import java.io.Serializable;

public class Bug implements Serializable {
    private String user_name;
    private String description;
    private int fixed;
    private String date;

    // Getters and Setters
    public String getUserName() { return user_name; }
    public void setUserName(int user_id) { this.user_name = user_name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getFixed() { return fixed; }
    public void setFixed(int fixed) { this.fixed = fixed; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
}
