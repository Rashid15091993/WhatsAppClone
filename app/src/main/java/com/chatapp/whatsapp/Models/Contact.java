package com.chatapp.whatsapp.Models;

public class Contact
{
    private String uid;
    private String name;
    private String phone;

    public Contact() {

    }
    public Contact(String phone, String uid) {
        this.uid = uid;
        this.phone = phone;


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

    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }

}
