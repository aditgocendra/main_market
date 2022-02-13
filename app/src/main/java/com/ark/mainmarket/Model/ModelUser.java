package com.ark.mainmarket.Model;

public class ModelUser {

    private String username;
    private String email;
    private String phone_number;
    private String address;
    private String role;
    private String url_photo;
    private String key;

    public ModelUser(){

    }

    public ModelUser(String username, String email, String phone_number, String address, String role, String url_photo) {
        this.username = username;
        this.email = email;
        this.phone_number = phone_number;
        this.address = address;
        this.role = role;
        this.url_photo = url_photo;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUrl_photo() {
        return url_photo;
    }

    public void setUrl_photo(String url_photo) {
        this.url_photo = url_photo;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
