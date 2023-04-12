package com.sgu.authservice.controller;

import com.sgu.authservice.dto.request.LogoutRequest;
import com.sgu.authservice.dto.request.RefreshTokenRequest;
import com.sgu.authservice.dto.response.HttpResponseObject;
import com.sgu.authservice.dto.request.LoginRequest;
import com.sgu.authservice.exception.BadRequest;
import com.sgu.authservice.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class AuthController {
    @Autowired
    private AuthService authService;
    @PostMapping("/login")
    public ResponseEntity<HttpResponseObject> login(
        @RequestBody @Valid LoginRequest loginRequest
    ){
        HttpResponseObject httpResponse = authService.login(loginRequest);

        return ResponseEntity.status(HttpStatus.OK).body(httpResponse);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<HttpResponseObject> refreshToken(
            @RequestBody @Valid RefreshTokenRequest refreshTokenRequest
    ){
        HttpResponseObject httpResponse = authService.refreshToken(refreshTokenRequest);

        return ResponseEntity.status(HttpStatus.OK).body(httpResponse);
    }


    @PostMapping("/logout")
    public ResponseEntity<HttpResponseObject> logout(
            @RequestBody @Valid LogoutRequest logoutRequest,
            Errors errors
    ){
        if (errors.hasErrors()) {
            throw new BadRequest(errors.getFieldError().getDefaultMessage());
        }
        HttpResponseObject httpResponse = authService.logout(logoutRequest);

        return ResponseEntity.status(HttpStatus.OK).body(httpResponse);
    }


}
