package com.github.huymaster.campusexpensemanager.database.realm.type;

import androidx.annotation.NonNull;

import io.realm.RealmObject;

public class UserInfo extends RealmObject {
    private String name = "";
    private String email = "";
    private String phone = "";
    private String address = "";
    private byte[] profileImage = new byte[0];

    public UserInfo() {
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public byte[] getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(byte[] profileImage) {
        this.profileImage = profileImage;
    }

    @NonNull
    @Override
    public String toString() {
        return "UserInfo[" + name + "]";
    }
}
