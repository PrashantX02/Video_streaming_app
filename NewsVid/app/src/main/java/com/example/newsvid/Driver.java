package com.example.newsvid;

public class Driver {
    String ImageAddress,uid,name,passcode,gender,address,married_status,mail;

    public Driver(){}

    public Driver(String uid,String name,String imageAddress,String passcode,String gender,String address,String married_status,String mail){
        this.uid = uid;
        this.name = name;
        this.ImageAddress = imageAddress;
        this.passcode = passcode;
        this.gender = gender;
        this.married_status = married_status;
        this.address = address;
        this.mail = mail;
    }

    public String getImageAddress() {
        return ImageAddress;
    }

    public void setImageAddress(String imageAddress) {
        ImageAddress = imageAddress;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPasscode() {
        return passcode;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMarried_status() {
        return married_status;
    }

    public void setMarried_status(String married_status) {
        this.married_status = married_status;
    }

    public void setPasscode(String passcode) {
        this.passcode = passcode;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }
}
