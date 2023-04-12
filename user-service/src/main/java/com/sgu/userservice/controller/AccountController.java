package com.sgu.userservice.controller;

import com.sgu.userservice.dto.request.*;
import com.sgu.userservice.dto.response.HttpResponseEntity;
import com.sgu.userservice.dto.request.ActiveAccountRequest;
import com.sgu.userservice.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {
    @Autowired
    private AccountService accountService;

    @GetMapping("")
    public ResponseEntity<HttpResponseEntity> getAll(
            @RequestParam(name = "page", required = false,defaultValue = "0") int pageRequest,
            @RequestParam(name = "size", required = false,defaultValue="0") int sizeRequest
    ){
        HttpResponseEntity httpResponseEntity = null;
        if(pageRequest == 0 && sizeRequest == 0){
            httpResponseEntity = accountService.getAll();
        }
        else{
            int page = pageRequest == 0 ? 1 : pageRequest;
            int size = sizeRequest==0 ? 20 : sizeRequest;

            httpResponseEntity = accountService.getAllAccountWithPagination(page,size);
        }

        return ResponseEntity.status(HttpStatus.OK).body(httpResponseEntity);
    }

    @GetMapping("/{id}")
    public ResponseEntity<HttpResponseEntity> getAccountByPersonId(
            @PathVariable(name = "id") String id
    ){
        HttpResponseEntity httpResponseEntity = accountService.getById(id);

        return ResponseEntity.status(HttpStatus.OK).body(httpResponseEntity);
    }

    @PostMapping("/otp/{username}")
    public ResponseEntity<HttpResponseEntity> sendOtpCode(
            @PathVariable(name = "username") String username
    ){
        HttpResponseEntity httpResponseEntity = accountService.sendOtpCode(username);

        return ResponseEntity.status(HttpStatus.OK).body(httpResponseEntity);
    }

    @PutMapping("/active/{username}")
    public ResponseEntity<HttpResponseEntity> activeAccount(
            @PathVariable(name="username") String username,
            @RequestBody @Valid ActiveAccountRequest activeAccountRequest
    ){
        HttpResponseEntity httpResponseEntity = accountService.activeAccount(username,activeAccountRequest);

        return ResponseEntity.status(HttpStatus.OK).body(httpResponseEntity);
    }

    @PutMapping("/block-account/{username}")
    public ResponseEntity<HttpResponseEntity> blockAccount(
            @PathVariable(name="username") String username,
            @RequestBody @Valid BlockAccountRequest blockAccountRequest
    ){
        HttpResponseEntity httpResponseEntity = accountService.blockAccount(username, blockAccountRequest);

        return ResponseEntity.status(HttpStatus.OK).body(httpResponseEntity);
    }

    @PutMapping("/unblock-account/{username}")
    public ResponseEntity<HttpResponseEntity> unBlockAccount(
            @PathVariable(name="username") String username
    ){
        HttpResponseEntity httpResponseEntity = accountService.unBlockAccount(username);

        return ResponseEntity.status(HttpStatus.OK).body(httpResponseEntity);
    }

    @PutMapping("/forget-password")
    public ResponseEntity<HttpResponseEntity> changePassword(
            @RequestBody @Valid ChangePasswordRequest changePasswordRequest
    ){
        HttpResponseEntity httpResponseEntity = accountService.changePassword(changePasswordRequest);

        return ResponseEntity.status(HttpStatus.OK).body(httpResponseEntity);
    }










    @GetMapping("/get-by-username/{username}")
    public ResponseEntity<HttpResponseEntity> getAccountByUsername(
            @PathVariable(name = "username") String username
    ){
        HttpResponseEntity httpResponseEntity = accountService.getAccoutByUsername(username);

        return ResponseEntity.status(HttpStatus.OK).body(httpResponseEntity);
    }




    @PostMapping("/update-vnpay/{username}")
    public ResponseEntity<HttpResponseEntity> updateVnpay(
            @RequestParam("file") MultipartFile file,
            @PathVariable(name = "username") String username
    ){
        HttpResponseEntity httpResponseEntity = accountService.updateVnpay(username,file);

        return ResponseEntity.status(HttpStatus.OK).body(httpResponseEntity);
    }
}
