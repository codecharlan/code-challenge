package com.codecharlan.codechallenge.exception;

import jakarta.validation.constraints.NotBlank;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(@NotBlank String s) {
        super(s);
    }
}
