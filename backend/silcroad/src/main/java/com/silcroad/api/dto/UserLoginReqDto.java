package com.silcroad.api.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserLoginReqDto {

    private String username;
    private String password;
}
