package com.example.childfinarapp;

public class SignUpInformation {
    private String authDBUserID;
    private String parentName;
    private String childName;
    private String parentEmail;

    //empty constructor because I am using Firebase (NoSQL)
    public SignUpInformation(){

}

//getter and setter methods for the five variables

    public String getAuthDBUserID() {

        return  authDBUserID;
    }

    public void setAuthDBUserID(String authDBUserID) {

        this.authDBUserID =  authDBUserID;
    }


    public String getParentName() {

        return  parentName;
    }

    public void setParentName(String parentName) {

        this.parentName =  parentName;
    }

    public String getChildName() {

        return childName;
    }

    public void setChildName(String childName) {

        this.childName = childName;
    }

    public String getParentEmail() {

        return parentEmail;
    }

    public void setParentEmail(String parentEmail) {

        this.parentEmail = parentEmail;
    }

}
