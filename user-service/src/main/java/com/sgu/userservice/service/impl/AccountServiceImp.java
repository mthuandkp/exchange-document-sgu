package com.sgu.userservice.service.impl;

import com.sgu.userservice.constant.Constant;
import com.sgu.userservice.constant.Role;
import com.sgu.userservice.dto.request.*;
import com.sgu.userservice.dto.response.AccountResponse;
import com.sgu.userservice.dto.response.HttpResponseEntity;
import com.sgu.userservice.dto.response.Pagination;
import com.sgu.userservice.exception.*;
import com.sgu.userservice.model.*;
import com.sgu.userservice.repository.AccountRepository;
import com.sgu.userservice.repository.PersonRepository;
import com.sgu.userservice.service.CloudinaryService;
import com.sgu.userservice.service.AccountService;
import com.sgu.userservice.service.OTPSmsService;
import com.sgu.userservice.utils.DateUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
public class AccountServiceImp implements AccountService {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private CloudinaryService cloudinaryService;
    @Autowired
    private OTPSmsService otpSmsService;
    @Autowired
    private PersonRepository personRepository;

    @Override
    public HttpResponseEntity getAllAccountWithPagination(int page, int size) {
        Pageable pageable = PageRequest.of(page-1,size);
        Page<Account> accountPage = accountRepository.findAllUserAccount(pageable);
        List<AccountResponse> accountList = accountPage.getContent()
                .stream()
                .map(this::convertToAccountResponse)
                .collect(Collectors.toList());
        Pagination pagination = Pagination.builder()
                .page(page)
                .size(size)
                .total_page(accountPage.getTotalPages())
                .total_size(accountPage.getTotalElements())
                .build();

        HttpResponseEntity httpResponseEntity = HttpResponseEntity.convertToResponeEntity(
                HttpStatus.OK.value(),
                Constant.SUCCESS,
                accountList,
                pagination
        );


        return httpResponseEntity;
    }

    @Override
    public HttpResponseEntity getAll() {
        List<AccountResponse> accountList = accountRepository.findAll()
                .stream()
                .map(this::convertToAccountResponse)
                .filter(account->account.getRole().equals(Role.USER))
                .collect(Collectors.toList());

        HttpResponseEntity httpResponseEntity = HttpResponseEntity.convertToResponeEntity(
                HttpStatus.OK.value(),
                Constant.SUCCESS,
                accountList,
                null
        );


        return httpResponseEntity;
    }

    @Override
    public HttpResponseEntity getById(String id) {
        Account account = accountRepository.findById(new ObjectId(id)).orElseThrow(
                ()->new NotFoundException(String.format("Không thể tìm tài khoản có id=%s",id))
        );

        AccountResponse accountResponse = this.convertToAccountResponse(account);
        List<AccountResponse> accountResponseList = Arrays.asList(accountResponse);
        HttpResponseEntity httpResponseEntity = HttpResponseEntity.convertToResponeEntity(
                HttpStatus.OK.value(),
                Constant.SUCCESS,
                accountResponseList,
                null
        );
        return httpResponseEntity;
    }

    @Override
    public HttpResponseEntity getAccoutByUsername(String username) {
        Account account = accountRepository.findByUsername(username).orElseThrow(
                ()->new NotFoundException(String.format("Không thể tìm tài khoản có username=%s",username))
        );

        AccountResponse accountResponse = this.convertToAccountResponse(account);
        List<AccountResponse> accountResponseList = Arrays.asList(accountResponse);

        HttpResponseEntity httpResponseEntity = HttpResponseEntity.convertToResponeEntity(
                HttpStatus.OK.value(),
                Constant.SUCCESS,
                accountResponseList,
                null
        );
        return httpResponseEntity;
    }

    @Override
    public HttpResponseEntity sendOtpCode(String username) {
        Account account = accountRepository.findByUsername(username).orElseThrow(
                ()->new NotFoundException(
                        String.format("Không thể tìm tài khoản có username=%s", username))
        );
        Person person = personRepository.findById(account.getId()).orElseThrow(
                ()->new NotFoundException(
                        String.format("Không thể tìm người dùng có id=%s"
                                , account.getId().toString()))
        );

        if(account.getIsBlock()){
            throw new UnauthorizedException("Account has block: " + account.getReasonForBlock());
        }

        String otpRandomCode = this.createOTP();

        Boolean isSend = otpSmsService.sendSMS(otpRandomCode,person.getPhone());
        if(!isSend){
            throw new BadGateWayException("Send mail fail");
        }

        //update otp code
        String updatedAt = String.valueOf(new Timestamp(System.currentTimeMillis()).getTime());

        account.setOtpCode(otpRandomCode);
        account.setOtpCreatedAt(updatedAt);

        accountRepository.save(account);


        HttpResponseEntity httpResponseEntity = HttpResponseEntity.builder()
                .code(HttpStatus.OK.value())
                .message(Constant.SUCCESS)
                .data(Arrays.asList(otpRandomCode))
                .build();
        return httpResponseEntity;
    }

    private String createOTP() {
        Long min = 100000L,max = 999999L;
        Long otpCode = ThreadLocalRandom.current().nextLong(min, max + 1);

        return String.valueOf(otpCode);
    }

    @Override
    public HttpResponseEntity activeAccount(String username, ActiveAccountRequest activeAccountRequest) {
        Account account = accountRepository.findByUsername(username).orElseThrow(
                ()->new NotFoundException(
                        String.format("Không thể tìm tài khoản có username=%s",username))
        );
        if(account.getIsBlock()){
            throw new UnauthorizedException("Account has block: " + account.getReasonForBlock());
        }

        if(account.getIsActive()){
            throw new UnauthorizedException("Account already active: ");
        }

        if(activeAccountRequest.getCode() != Integer.valueOf(account.getOtpCode())){
            throw new UnauthorizedException("Otp code not correct");
        }

        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
        long otpCreatedTime = Long.valueOf(account.getOtpCreatedAt());
        long diff = currentTimestamp.getTime() - otpCreatedTime;

        //Overcome 15p
        if(diff/60000 > 15){
            throw new UnauthorizedException("Otp has expired");
        }

        account.setIsActive(true);
        account.setUpdatedAt(DateUtils.getNow());
        accountRepository.save(account);

        HttpResponseEntity httpResponseEntity = HttpResponseEntity.builder()
                .code(HttpStatus.OK.value())
                .message(Constant.SUCCESS)
                .build();
        return httpResponseEntity;
    }

    @Override
    public HttpResponseEntity blockAccount(String username, BlockAccountRequest blockAccountRequest) {


        Account account = accountRepository.findByUsername(username).orElseThrow(
                ()->new NotFoundException(String.format("Không thể tìm tài khoản có username=%s",username))
        );

        if(account.getIsBlock()){
            throw new UnauthorizedException("Không thể khoá, tài khoản đang bị khoá trước đó");
        }


        account = account.toBuilder()
                .isBlock(true)
                .reasonForBlock(blockAccountRequest.getReasonForBlocking())
                .updatedAt(DateUtils.getNow())
                .build();

        accountRepository.save(account);


        HttpResponseEntity httpResponseEntity = HttpResponseEntity.builder()
                .code(HttpStatus.OK.value())
                .message("Khoá thành công")
                .build();

        return httpResponseEntity;
    }

    @Override
    public HttpResponseEntity unBlockAccount(String username) {
        Account account = accountRepository.findByUsername(username).orElseThrow(
                ()->new NotFoundException(String.format("Không thể tìm tài khoản có username=%s",username))
        );

        if(!account.getIsBlock()){
            throw new UnauthorizedException("Không thể mở khoá, tài khoản đang hoạt động");
        }

        account = account.toBuilder()
                .isBlock(false)
                .reasonForBlock("")
                .updatedAt(DateUtils.getNow())
                .build();


        accountRepository.save(account);

        HttpResponseEntity httpResponseEntity = HttpResponseEntity.builder()
                .code(HttpStatus.OK.value())
                .message("Mở khoá thành công")
                .build();

        return httpResponseEntity;
    }

    @Override
    public HttpResponseEntity changePassword(ChangePasswordRequest changePasswordRequest) {
        String username = changePasswordRequest.getUsername();
        Account account = accountRepository.findByUsername(username).orElseThrow(
                ()->new NotFoundException(String.format("Không thể tìm tài khoản có username=%s",username))
        );

        if(account.getIsBlock()){
            throw new UnauthorizedException("Account has block before : " + account.getReasonForBlock());
        }

        if(!account.getOtpCode().equals(String.valueOf(changePasswordRequest.getOtpCode()))){
            throw new UnauthorizedException("OTP code isn't correct");
        }
        String encodePassword = new BCryptPasswordEncoder().encode(changePasswordRequest.getNewPassword());

        account.setPassword(encodePassword);
        account.setUpdatedAt(DateUtils.getNow());

        accountRepository.save(account);

        HttpResponseEntity httpResponseEntity = HttpResponseEntity.builder()
                .code(HttpStatus.OK.value())
                .message(Constant.SUCCESS)
                .build();
        return httpResponseEntity;

    }


    @Override
    public HttpResponseEntity updateVnpay(String username, MultipartFile file) {
        Account account = accountRepository.findByUsername(username).orElseThrow(
                ()->new NotFoundException(String.format("Không thể tìm tài khoản có username=%s",username))
        );



        if(account.getIsBlock()){
            throw new UnauthorizedException("Account has block: " + account.getReasonForBlock());
        }

        if(!account.getIsActive()){
            throw new UnauthorizedException("Account hasn't active: ");
        }

        String contentType = file.getContentType();
        if(!contentType.equals("image/jpeg") && !contentType.equals("image/png")){
            throw new BadRequestException("Image only support 'jpg','jpeg' and 'png'");
        }

        //Upload
        try{
            Map<?,?> map = cloudinaryService.upload(file,"user/");
            String url = (String) map.get("url");

            account.setVnpayURL(url);

            accountRepository.save(account);
        }catch (InternalServerException e) {
            throw new RuntimeException(e);
        }


        HttpResponseEntity httpResponseEntity = HttpResponseEntity.builder()
                .code(HttpStatus.OK.value())
                .message(Constant.SUCCESS)
                .build();
        return httpResponseEntity;
    }

    private AccountResponse convertToAccountResponse(Account saveAccount) {
        return AccountResponse.builder()
                .id(saveAccount.getId())
                .username(saveAccount.getUsername())
                .role(saveAccount.getRole())
                .avatar(saveAccount.getAvatar())
                .isBlock(saveAccount.getIsBlock())
                .isActive(saveAccount.getIsActive())
                .otpCode(saveAccount.getOtpCode())
                .refreshToken(saveAccount.getRefreshToken())
                .vnpayURL(saveAccount.getVnpayURL())
                .createdAt(saveAccount.getCreatedAt())
                .updatedAt(saveAccount.getUpdatedAt())
                .build();
    }
}
