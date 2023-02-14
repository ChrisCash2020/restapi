package com.restaurant.restapi.controllers;

import com.restaurant.restapi.models.UserEntity;
import com.restaurant.restapi.models.requests.ExternalLoginReq;
import com.restaurant.restapi.models.requests.ExternalRegisterReq;
import com.restaurant.restapi.models.requests.LoginRequest;
import com.restaurant.restapi.models.requests.RegisterRequest;
import com.restaurant.restapi.models.responses.AuthResponse;
import com.restaurant.restapi.repositories.UserRepository;
import com.restaurant.restapi.security.AuthenticationService;
import com.restaurant.restapi.security.JwtService;
import com.restaurant.restapi.service.ImageUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;


@RestController
@RequestMapping("/api/auth")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    private final AuthenticationService service;
    private  final JwtService jwtService;
    private  final ImageUploadService uploadService;

    public UserController(UserRepository userRepository, AuthenticationService service, JwtService jwtService, ImageUploadService uploadService) {
        this.userRepository = userRepository;
        this.service = service;
        this.jwtService = jwtService;
        this.uploadService = uploadService;
    }
    @PostMapping(
            path="/register",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}
    )
    public ResponseEntity<?> internalRegister(@ModelAttribute RegisterRequest request ) {
        String res = service.internalRegister(request);
        if (res.length() < 40 ) {
            return ResponseEntity.internalServerError().body(res);
        }else{
            UserEntity user  = userRepository.findOneByEmail(request.getEmail());
            return ResponseEntity.ok().body(new AuthResponse(res, user.getName(), user.getImg(), user.getRole()));
        }
    }
    @PostMapping(
            path="/external-register",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}
    )
    public ResponseEntity<?> externalRegister(@ModelAttribute ExternalRegisterReq request ) {
        String res = service.externalRegister(request);
        if (res.length() < 40 ) {
            return ResponseEntity.internalServerError().body(res);
        }else{
            UserEntity user  = userRepository.findOneByEmail(request.getEmail());
            return ResponseEntity.ok().body(new AuthResponse(res, user.getName(), user.getImg(), user.getRole()));
        }
    }
    @PostMapping("/login")
    public ResponseEntity<?> internalLogin( @RequestBody LoginRequest loginRequest) {
        String res = service.internalLogin(loginRequest);
        if (res.length() < 40 ) {
            return ResponseEntity.internalServerError().body(res);
        }else{
            UserEntity user  = userRepository.findOneByEmail(loginRequest.getEmail());
            System.out.print(user.getName());
                return ResponseEntity.ok().body(new AuthResponse(res, user.getName(), user.getImg(), user.getRole() ));

        }
    }
    @PostMapping("/external-login")
    public ResponseEntity<?> externalLogin(@RequestBody ExternalLoginReq loginReq) {
        String email = loginReq.getEmail();
        String res = service.externalLogin(email);
        if (res.length() < 40 ) {
            return ResponseEntity.internalServerError().body(res);
        }else{
            System.out.print(res);
            UserEntity user  = userRepository.findOneByEmail(email);
            return ResponseEntity.ok().body(new AuthResponse(res, user.getName(), user.getImg(), user.getRole()));
        }
    }
    @GetMapping("/cred")
    public ResponseEntity<?> getUser(@RequestHeader("Authorization") String auth) {
        String email = jwtService.extractUsername(auth.substring(7));
        if(userRepository.existsByEmail(email)){
            UserEntity user  = userRepository.findOneByEmail(email);
            return new ResponseEntity<>(new AuthResponse("", user.getName(), user.getImg(), user.getRole()), HttpStatus.ACCEPTED);
        }else{
            return ResponseEntity.internalServerError().body("\"Session expired\"");

        }
    }
    @PutMapping(
            path="/edit",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}
    )
    public ResponseEntity<?> editUser(@RequestHeader("Authorization") String auth, @RequestParam("username") String username, @RequestParam("img") Optional<MultipartFile> img) {
        String email = jwtService.extractUsername(auth.substring(7));
        if(userRepository.existsByEmail(email)){
            UserEntity user  = userRepository.findOneByEmail(email);
            user.setName(username);
            if(img.isPresent()){
                user.setImg(uploadService.save(img.get()));
            }
            userRepository.save(user);
            return new ResponseEntity<>("\"User credentials updated\"", HttpStatus.ACCEPTED);
        }else{
            return ResponseEntity.internalServerError().body("\"Something Went Wrong\"");
        }
    }
}

