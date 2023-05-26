package com.campusdual.jardhotelsontimize.api.core.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

@ResponseStatus(value = HttpStatus.NOT_FOUND,reason = "Guest not found")
public class GuestNotFound extends ResponseStatusException {
    public GuestNotFound(String message) {
        super(HttpStatus.NOT_FOUND,message);

    }
}
