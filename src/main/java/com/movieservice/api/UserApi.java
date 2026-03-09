package com.movieservice.api;

import com.movieservice.dto.request.UserDtoRequest;
import com.movieservice.dto.response.UserDtoResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static com.movieservice.common.constant.ApiConstant.USER_API_URL;

@RequestMapping(USER_API_URL)
public interface UserApi {

    @PostMapping(value = "/registration", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<UserDtoResponse> register(@RequestBody @Valid UserDtoRequest userDto);

    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<List<UserDtoResponse>> retrieveAll(
            @RequestParam(name = "page") Integer page,
            @RequestParam(name = "size") Integer size
    );

}
