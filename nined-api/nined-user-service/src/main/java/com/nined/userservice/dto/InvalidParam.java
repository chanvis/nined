package com.nined.userservice.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * Object to represent API error response in case of invalid params
 * 
 * @author vijay
 *
 */
@Getter
@Setter
@RequiredArgsConstructor
public class InvalidParam {
    private final String name;

    private final String reason;
}
