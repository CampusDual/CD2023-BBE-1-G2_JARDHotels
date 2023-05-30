package com.campusdual.jardhotelsontimize.api.core.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

@ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY, reason = "Check-in date must be greater than or equal to current date")
public class CheckinLowerThanCurrentDate extends ResponseStatusException {
    public CheckinLowerThanCurrentDate(String message) {
        super(HttpStatus.UNPROCESSABLE_ENTITY, message);
    }
}

