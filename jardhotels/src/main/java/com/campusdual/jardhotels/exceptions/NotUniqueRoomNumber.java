package com.campusdual.jardhotels.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

@ResponseStatus(value = HttpStatus.CONFLICT,reason = "A room with this number already exists in the same hotel")

public class NotUniqueRoomNumber extends ResponseStatusException {
    public NotUniqueRoomNumber(String message) {
        super(HttpStatus.CONFLICT,message);
    }
}
