package com.sgu.userservice.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.sgu.userservice.constant.Constant;
import com.sgu.userservice.dto.request.*;
import com.sgu.userservice.dto.response.AccountResponse;
import com.sgu.userservice.dto.response.HttpResponseEntity;
import com.sgu.userservice.dto.response.PersonResponse;
import com.sgu.userservice.dto.response.UserResponse;
import com.sgu.userservice.exception.*;

import com.sgu.userservice.model.Account;
import com.sgu.userservice.model.Person;
import com.sgu.userservice.repository.AccountRepository;
import com.sgu.userservice.repository.PersonRepository;
import com.sgu.userservice.service.CloudinaryService;
import com.sgu.userservice.service.OTPSmsService;
import com.sgu.userservice.service.UserService;
import com.sgu.userservice.utils.DateUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
public class UserServiceImp implements UserService {
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private OTPSmsService otpSmsService;
    @Autowired
    private CloudinaryService cloudinaryService;

    @Override
    public HttpResponseEntity register(UserRequest userRequest) {
        if(accountRepository.findByUsername(userRequest.getUsername()).isPresent()){
            throw new ConflictException(
                    String.format("Mssv '%s' đã tồn tại trong hệ thống",userRequest.getUsername()));
        }

        if(personRepository.getByPhone(userRequest.getPhone()).isPresent()){
            throw new ConflictException(
                    String.format("Số điện thoại '%s' đã được đăng ký tài khoản",userRequest.getPhone()));
        }

        Person personEntity = (Person) this.convertToEntity(userRequest,"PERSON");
        Account accountEntity = (Account)this.convertToEntity(userRequest,"ACCOUNT");

        Person savePerson = personRepository.save(personEntity);
        accountEntity.setId(savePerson.getId());
        Account saveAccount = accountRepository.save(accountEntity);

        HttpResponseEntity httpResponseEntity = HttpResponseEntity.convertToResponeEntity(
                HttpStatus.CREATED.value(),
                Constant.REGISTER_SUCCESS,
                null,
                null
        );
        return httpResponseEntity;
    }

    @Override
    public HttpResponseEntity delete(DeleteRequest deleteRequest) {
        String id = deleteRequest.getId();
        Optional<Person> personOptional = personRepository.findById(new ObjectId(id));
        Optional<Account> accountOptional = accountRepository.findById(new ObjectId(id));

        if(personOptional.isEmpty() || accountOptional.isEmpty()){
            throw new UserNotFoundException("Can't find account and person with id = " + id);
        }


        personRepository.delete(personOptional.get());
        accountRepository.delete(accountOptional.get());

        HttpResponseEntity httpResponseEntity = HttpResponseEntity.builder()
                .code(HttpStatus.OK.value())
                .message(Constant.SUCCESS)
                .data(Arrays.asList(personOptional.get(),accountOptional.get()))
                .build();
        return httpResponseEntity;

    }

    @Override
    public HttpResponseEntity profile(String token) {
        if(!token.startsWith("Bearer ")){
            throw new BadRequestException("Token không hợp lệ");
        }

        DecodedJWT jwt = JWT.decode(token.substring("Bearer ".length()));

        if(jwt.getClaims().get("role") == null){
            throw new UnauthorizedException("Access token không chính xác");
        }

        if (jwt.getExpiresAt().before(new Date())) {
            throw new UnauthorizedException("Access token đã hết hạn");
        }

        Account accountEntity = accountRepository.findByUsername(jwt.getSubject()).orElseThrow(
                () -> new NotFoundException(String.format("Không thể tìm tài khoản bởi token"))
        );

        Person personEntity = personRepository.findById(accountEntity.getId()).orElseThrow(
                ()-> new NotFoundException(String.format(
                        "Người dùng có id=%s không tồn tại",accountEntity.getId().toString()
                ))
        );


        PersonResponse personResponse = this.convertToPersonResponse(personEntity);
        AccountResponse accountResponse = this.convertToAccountResponse(accountEntity);

        UserResponse userResponse = this.convertToUserResponse(personResponse,accountResponse);
        List<UserResponse> userResponsesList = Arrays.asList(userResponse);

        HttpResponseEntity httpResponseEntity = HttpResponseEntity.convertToResponeEntity(
                HttpStatus.OK.value(),
                Constant.SUCCESS,
                userResponsesList,
                null
        );
        return httpResponseEntity;
    }

    @Override
    public HttpResponseEntity updatePerson(String token, PersonRequest personRequest){
        if(!token.startsWith("Bearer ")){
            throw new BadRequestException("Token không hợp lệ");
        }

        DecodedJWT jwt = JWT.decode(token.substring("Bearer ".length()));
        String username = jwt.getSubject();

        Account account = accountRepository.findByUsername(username).orElseThrow(
                ()-> new NotFoundException("Tài khoản không tồn tại")
        );

        Person person = personRepository.findById(account.getId()).orElseThrow(
                ()-> new NotFoundException("Người dùng không tồn tại")
        );

        if(!DateUtils.isValidDate(personRequest.getBirthday())){
            throw new BadRequestException("Ngày sinh không hợp lệ");
        }

        Person savePeson = Person.builder()
                .id(person.getId())
                .name(personRequest.getName())
                .address(personRequest.getAddress())
                .birthday(personRequest.getBirthday())
                .gender(personRequest.getGender())
                .createdAt(person.getCreatedAt())
                .updatedAt(DateUtils.getNow())
                .phone(personRequest.getPhone())
                .build();

       account.setAvatar(personRequest.getAvatar());


        savePeson = personRepository.save(savePeson);
        Account saveAccount = accountRepository.save(account);
        PersonResponse personResponse = this.convertToPersonResponse(savePeson);
        AccountResponse accountResponse = this.convertToAccountResponse(saveAccount);
        UserResponse userResponse = this.convertToUserResponse(personResponse,accountResponse);
        List<UserResponse> responseList = Arrays.asList(userResponse);

        HttpResponseEntity httpResponseEntity = HttpResponseEntity.builder()
                .code(HttpStatus.OK.value())
                .message(Constant.SUCCESS)
                .data(responseList)
                .build();


        return httpResponseEntity;
    }

    @Override
    public HttpResponseEntity personProfile(String id) {
        Account accountEntity = accountRepository.findById(new ObjectId(id)).orElseThrow(
                () -> new NotFoundException(String.format("Không tìm thấy profile"))
        );

        Person personEntity = personRepository.findById(accountEntity.getId()).orElseThrow(
                () -> new NotFoundException(String.format("Không tìm thấy profile"))

        );


        PersonResponse personResponse = this.convertToPersonResponse(personEntity);
        AccountResponse accountResponse = this.convertToAccountResponse(accountEntity);
        UserResponse userResponse = this.convertToUserResponse(personResponse,accountResponse);

        List<UserResponse> userResponsesList = Arrays.asList(userResponse);

        HttpResponseEntity httpResponseEntity = HttpResponseEntity.convertToResponeEntity(
                HttpStatus.OK.value(),
                Constant.SUCCESS,
                userResponsesList,
                null
        );
        return httpResponseEntity;
    }


    private UserResponse convertToUserResponse(PersonResponse personResponse, AccountResponse accountResponse) {
        return UserResponse.builder()
                .id(personResponse.getId())
                .username(accountResponse.getUsername())
                .role(accountResponse.getRole())
                .avatar(accountResponse.getAvatar())
                .isBlock(accountResponse.getIsBlock())
                .isActive(accountResponse.getIsActive())
                .vnpayURL(accountResponse.getVnpayURL())
                .name(personResponse.getName())
                .address(personResponse.getAddress())
                .phone(personResponse.getPhone())
                .birthday(personResponse.getBirthday())
                .gender(personResponse.getGender())
                .build();
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

    private PersonResponse convertToPersonResponse(Person savePerson) {
        return PersonResponse.builder()
                .id(savePerson.getId())
                .name(savePerson.getName())
                .address(savePerson.getAddress())
                .phone(savePerson.getPhone())
                .birthday(savePerson.getBirthday())
                .gender(savePerson.getGender())
                .createdAt(savePerson.getCreatedAt())
                .updatedAt(savePerson.getUpdatedAt())
                .build();
    }

    private Object convertToEntity(UserRequest userRequest, String classname) {
        if(classname.equals("ACCOUNT")){
            return Account.builder()
                    .username(userRequest.getUsername())
                    .password(new BCryptPasswordEncoder().encode(userRequest.getPassword()))
                    .build();
        }
        return Person.builder()
                .name(userRequest.getName())
                .phone(userRequest.getPhone())
                .address(userRequest.getAddress())
                .birthday(userRequest.getBirthday())
                .gender(userRequest.getGender())
                .build();
    }

    @Override
    public HttpResponseEntity uploadImage(MultipartFile file) {


        String contentType = file.getContentType();
        if(!contentType.equals("image/jpeg") && !contentType.equals("image/png")){
            throw new BadRequestException("Image only support 'jpg','jpeg' and 'png'");
        }

        //Upload
        try{
            Map<?,?> map = cloudinaryService.upload(file,"user/");
            String url = (String) map.get("url");

            HttpResponseEntity httpResponseEntity = HttpResponseEntity.builder()
                    .code(HttpStatus.OK.value())
                    .message(Constant.SUCCESS)
                    .data(Arrays.asList(url))
                    .build();
            return httpResponseEntity;

        }catch (InternalServerException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public HttpResponseEntity getByUsername(String username) {
        Account accountEntity = accountRepository.findByUsername(username).orElseThrow(
                () -> new NotFoundException(String.format("Không tìm thấy profile"))
        );

        Person personEntity = personRepository.findById(accountEntity.getId()).orElseThrow(
                () -> new NotFoundException(String.format("Không tìm thấy profile"))

        );


        PersonResponse personResponse = this.convertToPersonResponse(personEntity);
        AccountResponse accountResponse = this.convertToAccountResponse(accountEntity);
        UserResponse userResponse = this.convertToUserResponse(personResponse,accountResponse);

        List<UserResponse> userResponsesList = Arrays.asList(userResponse);

        HttpResponseEntity httpResponseEntity = HttpResponseEntity.convertToResponeEntity(
                HttpStatus.OK.value(),
                Constant.SUCCESS,
                userResponsesList,
                null
        );
        return httpResponseEntity;
    }
}
