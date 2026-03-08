package com.movieservice.service;

import com.movieservice.dto.request.UserDtoRequest;
import com.movieservice.model.entity.UserModel;

public interface UserService {

    UserModel registerNewAccount(UserDtoRequest userDtoRequest);

}
