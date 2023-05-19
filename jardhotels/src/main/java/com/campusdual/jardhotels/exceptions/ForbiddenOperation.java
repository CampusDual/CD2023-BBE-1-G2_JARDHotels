package com.campusdual.jardhotels.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

@ResponseStatus(value = HttpStatus.FORBIDDEN,reason = "Forbidden operation")
public class ForbiddenOperation extends ResponseStatusException {
    public ForbiddenOperation(String message) {
        super(HttpStatus.FORBIDDEN,message);
    }
}
