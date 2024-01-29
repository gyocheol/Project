package com.silcroad.api.service;

import com.silcroad.api.dto.UserLoginReqDto;
import com.silcroad.api.dto.UserSignUpReqDto;

public interface UserService {

    /**
     * 회원가입
     * @param dto
     */
    void signUp(UserSignUpReqDto dto);

    /**
     * 로그인
     * @param dto
     */
    String login(UserLoginReqDto dto);
}
