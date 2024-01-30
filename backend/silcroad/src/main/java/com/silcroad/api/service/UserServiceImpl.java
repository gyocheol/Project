package com.silcroad.api.service;

//import com.silcroad.api.config.JwtTokenProvider;
import com.silcroad.api.dto.UserLoginReqDto;
import com.silcroad.api.dto.UserSignUpReqDto;
import com.silcroad.api.entity.User;
import com.silcroad.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
//    private final JwtTokenProvider jwtTokenProvider;
        
    @Override
    public void signUp(UserSignUpReqDto dto) {
        // 아이디 중복 체크
        if (userRepository.findByUsername(dto.getUsername()).isPresent()){
            throw new IllegalStateException("중복된 아이디가 존재합니다.");
        }
        // 비밀번호 유효성 검사
        if (!dto.getPassword().equals(dto.getCheckedPassword())) {
            throw new IllegalStateException("비밀번호를 다시 입력해주세요.");
        }
        // DB에 저장
        User user = User.builder()
                .username(dto.getUsername())
                .password(dto.getPassword())
                .build();
        user.encoder(passwordEncoder);
        userRepository.save(user);
    }

    @Override
    public String login(UserLoginReqDto dto) {
        // 아이디 유효성 검사
        User user = userRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new IllegalStateException("존재하는 회원이 없습니다."));
        // 비밀번호 유효성 검사
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new IllegalStateException("비밀번호가 틀렸습니다.");
        }
//        return jwtTokenProvider.accessToken(dto);
        return "안녕";
    }
}
