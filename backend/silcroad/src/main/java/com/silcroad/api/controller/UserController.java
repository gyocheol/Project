package com.silcroad.api.controller;

import com.silcroad.api.dto.UserLoginReqDto;
import com.silcroad.api.dto.UserSignUpReqDto;
import com.silcroad.api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    /**
     * 회원가입
     * @param dto
     * @return
     */
    @PostMapping("/sign")
    public ResponseEntity<HttpStatus> signUp(@RequestBody UserSignUpReqDto dto) {
        userService.signUp(dto);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserLoginReqDto dto) {
        return new ResponseEntity<>(userService.login(dto), HttpStatus.OK);
    }
}
