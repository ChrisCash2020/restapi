package com.restaurant.restapi.models.responses;

import com.restaurant.restapi.models.Role;
import lombok.Data;

@Data
public class AuthResponse {
    public String token;
    public String username;
    public String img;
    public Role role;

    public AuthResponse(String token, String username, String img, Role role) {
        this.token = token;
        this.username = username;
        this.img = img;
        this.role = role;
    }
}
