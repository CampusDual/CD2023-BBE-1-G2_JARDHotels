package com.campusdual.jardhotelsontimize.api.core.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Check-out date must be greater than check-in date")
public class CheckinGreaterThanCheckout extends ResponseStatusException {
    public CheckinGreaterThanCheckout(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}

