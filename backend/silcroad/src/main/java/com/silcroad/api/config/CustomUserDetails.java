package com.silcroad.api.config;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class CustomUserDetails implements UserDetails {

    private final Object user;

    public CustomUserDetails(Object user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    // 비밀번호 반환
    @Override
    public String getPassword() {
        return ((User) user).getPassword();
    }
    // 이름 또는 아이디 반환
    @Override
    public String getUsername() {
        return ((User) user).getUsername();
    }
    // 계정 만료 여부
    @Override
    public boolean isAccountNonExpired() {
        return true;    // 만료되지 않음
    }
    // 계정이 잠겼는지 여부
    @Override
    public boolean isAccountNonLocked() {
        return true;    // 잠겨있지 않음
    }
    // 사용자 자격 증명 만료 여부
    @Override
    public boolean isCredentialsNonExpired() {
        return true;    // 만료되지 않음
    }
    // 계정 활성화
    @Override
    public boolean isEnabled() {
        return true;    // 활성화 되어있음
    }
}
