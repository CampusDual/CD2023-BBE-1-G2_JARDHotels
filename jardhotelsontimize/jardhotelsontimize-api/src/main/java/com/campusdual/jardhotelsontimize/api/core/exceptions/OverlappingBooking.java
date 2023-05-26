package com.campusdual.jardhotelsontimize.api.core.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

@ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "The date range overlaps with the dates of an existing booking")
public class OverlappingBooking extends ResponseStatusException {
    public OverlappingBooking(String message) {
        super(HttpStatus.FORBIDDEN, message);
    }
}

