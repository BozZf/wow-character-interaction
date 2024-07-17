package me.dio.wow_character_interaction.adapter.controller.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class RequiredObjectIsNull extends RuntimeException {

    public RequiredObjectIsNull() {
        super();
    }
}
