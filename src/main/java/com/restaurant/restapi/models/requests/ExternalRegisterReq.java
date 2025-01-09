package com.restaurant.restapi.models.requests;

import lombok.Data;
@Data
public class ExternalRegisterReq {
    private String username;
    private String password;
    private String img;
    private String email;
}
