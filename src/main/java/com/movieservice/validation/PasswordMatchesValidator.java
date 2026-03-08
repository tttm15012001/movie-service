package com.movieservice.validation;

import com.movieservice.dto.request.UserDtoRequest;
import com.movieservice.validation.annotation.PasswordMatches;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {

    @Override
    public void initialize(PasswordMatches constraintAnnotation) {}

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {
        UserDtoRequest userDtoRequest = (UserDtoRequest) obj;
        return userDtoRequest.getPassword().equals(userDtoRequest.getPasswordConfirmation());
    }
}
