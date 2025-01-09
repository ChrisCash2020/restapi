package com.restaurant.restapi.models.requests;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class RegisterRequest {
    private String username;
    private String password;
    private MultipartFile img;
    private String email;
}

