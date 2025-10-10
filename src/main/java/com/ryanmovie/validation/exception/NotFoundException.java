package com.ryanmovie.validation.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String entity, Long id) {
        super(entity + " can not be found with id: " + id);
    }
}
