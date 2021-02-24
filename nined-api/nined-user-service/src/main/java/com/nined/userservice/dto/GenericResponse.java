package com.nined.userservice.dto;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import lombok.Setter;

/**
 * Generic response object for REST API
 * 
 * @author vijay
 *
 */
@Getter
@Setter
public class GenericResponse {
    private String message;
    private int status;

    @JsonInclude(Include.NON_NULL)
    private String error;

    @JsonInclude(Include.NON_NULL)
    private List<InvalidParam> invalidParams;

    public GenericResponse(final String message, final int status) {
        this.message = message;
        this.status = status;
    }

    public GenericResponse(final String message, final String error, final int status) {
        this.message = message;
        this.error = error;
        this.status = status;
    }

    public GenericResponse(List<ObjectError> allErrors, String error, String message,
            final int status) {
        this.error = error;
        this.message = message;
        this.status = status;
        invalidParams = allErrors.stream().map(e -> {
            if (e instanceof FieldError) {
                return new InvalidParam(((FieldError) e).getField(), e.getDefaultMessage());


            } else {
                return new InvalidParam(e.getObjectName(), e.getDefaultMessage());
            }
        }).collect(Collectors.toList());
    }

}
