package com.movieservice.service;

import com.movieservice.dto.request.UserDtoRequest;
import com.movieservice.dto.response.UserDtoResponse;
import com.movieservice.model.entity.UserModel;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface UserService {

    UserModel registerNewAccount(UserDtoRequest userDtoRequest);

    List<UserDtoResponse> getAllUsersPageable(Pageable pageable);

}
