package com.silcroad.api.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserSignUpReqDto {

    private String username;
    private String password;
    private String checkedPassword;
}
