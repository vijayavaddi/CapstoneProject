package com.hcltech.doctorpatient.dto.authentication;

import lombok.Data;

@Data
public class AuthenticationRequestDto {

    private String mobile;
    private String password;
    private String roles;

}
