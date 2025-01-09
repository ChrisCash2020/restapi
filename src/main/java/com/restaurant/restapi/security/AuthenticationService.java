package com.restaurant.restapi.security;

import com.restaurant.restapi.models.Provider;
import com.restaurant.restapi.models.Role;
import com.restaurant.restapi.models.UserEntity;
import com.restaurant.restapi.models.requests.ExternalRegisterReq;
import com.restaurant.restapi.models.requests.LoginRequest;
import com.restaurant.restapi.models.requests.RegisterRequest;
import com.restaurant.restapi.repositories.UserRepository;
import com.restaurant.restapi.service.ImageUploadService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.annotation.ModelAttribute;

@Service
public class AuthenticationService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final ImageUploadService uploadService;

    public AuthenticationService(UserRepository repository, PasswordEncoder passwordEncoder, JwtService jwtService,
            AuthenticationManager authenticationManager, ImageUploadService uploadService) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.uploadService = uploadService;
    }

    public String externalRegister(@ModelAttribute ExternalRegisterReq request) {
        if (repository.existsByEmail(request.getEmail())) {
            return "\"Account with email exists\"";
        } else {
            if (request.getEmail().isEmpty() || request.getImg().isEmpty() || request.getUsername().isEmpty()) {
                return "\"Permitted Credentials\"";
            }
            final UserEntity user = new UserEntity();
            user.setName(request.getUsername());
            user.setPassword(passwordEncoder.encode("googlepw"));
            user.setImg(request.getImg());
            user.setEmail(request.getEmail());
            user.setRole(Role.USER);
            user.setProvider(Provider.GOOGLE);
            repository.save(user);
            return jwtService.generateToken(user);
        }
    }

    public String internalRegister(@ModelAttribute RegisterRequest request) {
        if (repository.existsByEmail(request.getEmail())) {
            return "\"Account with email exists\"";
        } else {
            if (request.getEmail().isEmpty() || request.getImg().isEmpty() || request.getPassword().isEmpty()
                    || request.getUsername().isEmpty()) {
                return "\"Permitted Credentials\"";
            }
            final UserEntity user = new UserEntity();
            user.setName(request.getUsername());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            String imgUrl = uploadService.save(request.getImg());
            user.setImg(imgUrl);
            user.setEmail(request.getEmail());
            user.setRole(Role.USER);
            user.setProvider(Provider.INTERNAL);
            repository.save(user);
            return jwtService.generateToken(user);
        }
    }

    public String externalLogin(String email) {
        try {
            var user = repository.findByEmail(email)
                    .orElseThrow(() -> new ErrorResponseException(HttpStatus.NOT_FOUND));
            String provider = user.getProvider().toString();
            if (provider.equals("GOOGLE")) {
                return jwtService.generateToken(user);

            } else {
                return "\"User not logged in with google\"";
            }
        } catch (Exception error) {
            return "\"Account does not exist\"";
        }
    }

    public String internalLogin(LoginRequest request) {
        try {
            final UserEntity user = repository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new ErrorResponseException(HttpStatus.NOT_FOUND));
            String provider = user.getProvider().toString();
            if (provider.equals("GOOGLE")) {
                return "\"Login in with Google\"";
            } else {
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.getEmail(),
                                request.getPassword()));
                return jwtService.generateToken(user);
            }

        } catch (Exception error) {
            return "\"Invalid Credentials\"";
        }

    }
}
