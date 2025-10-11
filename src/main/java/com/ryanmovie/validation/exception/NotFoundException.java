package com.ryanmovie.validation.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String entity, Long id) {
        super(entity + " can't be found with id: " + id);
    }
}
