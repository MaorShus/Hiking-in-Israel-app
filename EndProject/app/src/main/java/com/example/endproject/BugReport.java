package com.example.endproject;

public class BugReport {
    private String user_name;
    private String description;
    private boolean fixed;
    private String date;

    public BugReport(String user_name, String description, boolean fixed, String date) {
        this.user_name = user_name;
        this.description = description;
        this.fixed = fixed;
        this.date = date;
    }
    public String getUserName() { return user_name; }
    public String getDescription() { return description; }
    public boolean isFixed() { return fixed; }
    public String getDate() { return date; }
}
