package com.auction.WebAuction.dto;

import lombok.Data;


public class MemberLoginDto {
    private String username;
    private String userpass;

    public String getUserpass() {
        return userpass;
    }

    public void setUserpass(String userpass) {
        this.userpass = userpass;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
