package com.sgu.userservice.controller;


import com.sgu.userservice.dto.request.*;
import com.sgu.userservice.dto.response.HttpResponseEntity;
import com.sgu.userservice.exception.BadRequestException;
import com.sgu.userservice.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class UserController {
    @Autowired
    private UserService userService;
    @PostMapping("/register")
    public ResponseEntity<HttpResponseEntity> register(@RequestBody @Valid UserRequest userRequest){
        HttpResponseEntity httpResponseEntity = userService.register(userRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(httpResponseEntity);
    }

    @GetMapping("/profile")
    public ResponseEntity<HttpResponseEntity> profile(
            @RequestHeader("Authorization") Optional<String> token){
        if(token.isEmpty()){
           throw new BadRequestException("Không tìm thấy token");
        }
        HttpResponseEntity httpResponseEntity = userService.profile(token.get());

        return ResponseEntity.status(HttpStatus.OK).body(httpResponseEntity);
    }

    @GetMapping("/profile/{id}")
    public ResponseEntity<HttpResponseEntity> profileById(
            @PathVariable("id") String id
    ){
        HttpResponseEntity httpResponseEntity = userService.personProfile(id);

        return ResponseEntity.status(HttpStatus.OK).body(httpResponseEntity);
    }

    @GetMapping("/profile/username/{username}")
    public ResponseEntity<HttpResponseEntity> profileByUserName(
            @PathVariable("username") String username
    ){
        HttpResponseEntity httpResponseEntity = userService.getByUsername(username);

        return ResponseEntity.status(HttpStatus.OK).body(httpResponseEntity);
    }


    @PutMapping("/user/profile/update/{username}")
    public ResponseEntity<HttpResponseEntity> updatePerson(
            @RequestBody @Valid PersonRequest personRequest,
            @RequestHeader("Authorization") String token
    ){
        HttpResponseEntity httpResponseEntity = userService.updatePerson(token,personRequest);

        return ResponseEntity.status(HttpStatus.OK).body(httpResponseEntity);
    }

    @PostMapping("/user/upload-image")
    public ResponseEntity<?> updateAvatar(
            @RequestParam("file") MultipartFile file){

        HttpResponseEntity httpResponseEntity = userService.uploadImage(file);
        return ResponseEntity.status(HttpStatus.OK).body(httpResponseEntity);
    }


}
