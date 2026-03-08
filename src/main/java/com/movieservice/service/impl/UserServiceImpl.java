package com.movieservice.service.impl;

import com.movieservice.dto.request.UserDtoRequest;
import com.movieservice.model.entity.RoleEnum;
import com.movieservice.model.entity.UserModel;
import com.movieservice.repository.UserRepository;
import com.movieservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserModel registerNewAccount(UserDtoRequest userDtoRequest) throws RuntimeException {
        if (isEmailExist(userDtoRequest.getEmail())) {
            throw new RuntimeException("There is an account with that email address: "
                    + userDtoRequest.getEmail());
        }

        UserModel newUser = UserModel.builder()
                .email(userDtoRequest.getEmail())
                .password(userDtoRequest.getPassword())
                .role(RoleEnum.USER)
                .build();

        return userRepository.save(newUser);
    }

    private boolean isEmailExist(String email) {
        return userRepository.findByEmail(email) != null;
    }

}
