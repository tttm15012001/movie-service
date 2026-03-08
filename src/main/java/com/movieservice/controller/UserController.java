package com.movieservice.controller;

import com.movieservice.api.UserApi;
import com.movieservice.dto.request.UserDtoRequest;
import com.movieservice.dto.response.UserDtoResponse;
import com.movieservice.model.entity.RoleEnum;
import com.movieservice.model.entity.UserModel;
import com.movieservice.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController implements UserApi {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Override
    public ResponseEntity<UserDtoResponse> register(@RequestBody @Valid UserDtoRequest user) {
        UserModel newUser = userService.registerNewAccount(user);

        UserDtoResponse userDtoResponse = UserDtoResponse.builder()
                .email(newUser.getEmail())
                .role(RoleEnum.getRoleByCode(newUser.getRole().toString()))
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(userDtoResponse);
    }

}
