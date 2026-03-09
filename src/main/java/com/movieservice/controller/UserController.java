package com.movieservice.controller;

import static com.movieservice.common.constant.ApiConstant.DEFAULT_SIZE_PER_PAGE;

import com.movieservice.api.UserApi;
import com.movieservice.dto.request.UserDtoRequest;
import com.movieservice.dto.response.UserDtoResponse;
import com.movieservice.model.entity.UserModel;
import com.movieservice.service.UserService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
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

        return ResponseEntity.status(HttpStatus.CREATED).body(newUser.toUserDtoResponse());
    }

    @Override
    public ResponseEntity<List<UserDtoResponse>> retrieveAll(
            @RequestParam(name = "page") Integer page,
            @RequestParam(name = "size") Integer size
    ) {
        if (page == null) page = 0;
        if (size == null) size = DEFAULT_SIZE_PER_PAGE;

        Pageable pageable = PageRequest.of(page - 1, size);

        List<UserDtoResponse> users = userService.getAllUsersPageable(pageable);

        return ResponseEntity.status(HttpStatus.OK).body(users);
    }

}
