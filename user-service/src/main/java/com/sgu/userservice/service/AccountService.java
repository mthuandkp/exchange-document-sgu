package com.sgu.userservice.service;

import com.sgu.userservice.dto.request.*;
import com.sgu.userservice.dto.response.HttpResponseEntity;
import com.sgu.userservice.dto.request.ActiveAccountRequest;
import org.springframework.web.multipart.MultipartFile;

public interface AccountService {


    public HttpResponseEntity getAllAccountWithPagination(int page, int size);

    public HttpResponseEntity getById(String id);

    public HttpResponseEntity getAccoutByUsername(String username);

    public HttpResponseEntity sendOtpCode(String sendOTPRequest);

    public HttpResponseEntity activeAccount(String username, ActiveAccountRequest activeAccountRequest);

    public HttpResponseEntity blockAccount(String username, BlockAccountRequest blockAccountRequest);

    public HttpResponseEntity unBlockAccount(String username);

    public HttpResponseEntity changePassword(ChangePasswordRequest changePasswordRequest);


    public HttpResponseEntity updateVnpay(String username, MultipartFile file);

    public HttpResponseEntity getAll();
}
