package com.alessandro.napoletano.springbootoauth2demov2.payload;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;

@Getter
@Setter
public class SignUpRequest {
    private String name;

    @Email
    private String email;

    private String password;

    private String role;

    private String line;
}
