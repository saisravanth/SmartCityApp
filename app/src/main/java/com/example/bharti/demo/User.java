package com.example.bharti.demo;

public class User {
    private String fullName;
    private String emailId;
    private String password;

//    public User(String name, String email, String password) {
//        this.emailId = email;
//        this.fullName = name;
//        this.password = password;
//    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
