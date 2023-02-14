package com.restaurant.restapi.models.responses;


public class AuthResponse {

    public String token;
    public String username;
    public String img;
    public Enum role;

    public AuthResponse(String token, String username, String img, Enum role) {

        this.token = token;
        this.username = username;
        this.img = img;
        this.role = role;
    }
}
