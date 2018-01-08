package com.example.onpus.weddingpanda.constant;

/**
 * Created by onpus on 2017/11/21.
 */

public class User {


    private String name;
    private String email;
//    private String password;
    private String userType;

    public User(){

    }

    public User(String name, String email, String userType) {
        this.name = name;
        this.email = email;
        this.userType = userType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

//    public String getPassword() {
//        return password;
//    }
//
//    public void setPassword(String password) {
//        this.password = password;
//    }

    public void setUserType(String userType) { this.userType = userType;}
    public String getUserType(){ return userType;}


}