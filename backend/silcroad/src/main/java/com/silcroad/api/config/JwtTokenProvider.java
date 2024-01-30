package com.silcroad.api.config;

import com.silcroad.api.dto.UserLoginReqDto;
import com.silcroad.api.entity.User;
import com.silcroad.api.repository.UserRepository;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenProvider {

    private final UserRepository userRepository;

    @Value("${spring.security.securityKey}")
    private String securityKey;

    // AccessToken 유효기간 1일
    private final long accessTokenValidTime = 1000 * 60 * 60 * 24;
    private HttpServletResponse response;

    @PostConstruct
    private void init() {
        securityKey = Base64.getEncoder().encodeToString(securityKey.getBytes());
    }

    public String accessToken(UserLoginReqDto member) {
        Date now = new Date();
        String token = Jwts.builder()
                .setSubject(member.getUsername())
                .setHeader(createHeader())
                .setClaims(createClaims(member)) // 클레임, 토큰에 포함될 정보
                .setExpiration(new Date(now.getTime() + accessTokenValidTime)) // 만료일
                .signWith(SignatureAlgorithm.HS256, securityKey)
                .compact();
        // 쿠키에 토큰 설정
        Cookie cookie = new Cookie("accessToken", token);
        cookie.setMaxAge((int) TimeUnit.MILLISECONDS.toSeconds(accessTokenValidTime));
        cookie.setHttpOnly(true); // javaScript 의 쿠키 접근 막기
        cookie.setPath("/"); // 쿠키의 유효 경로 설정
        response.addCookie(cookie);

        return token;
    }

    private Map<String, Object> createHeader() {
        Map<String, Object> header = new HashMap<>();
        header.put("typ", "JWT");
        header.put("alg", "HS256"); // 해시 256 사용하여 암호화
        header.put("regDate", System.currentTimeMillis());
        return header;
    }

    private Map<String, Object> createClaims(UserLoginReqDto member){
        Map<String, Object> claims = new HashMap<>();
        User user = userRepository.findByUsername(member.getUsername())
                .orElseThrow(() -> new IllegalStateException("없는 사용자 입니다."));
        claims.put("id", user.getId());
        claims.put("username", user.getUsername());

        return claims;
    }

    public Claims getClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(securityKey).build().parseClaimsJws(token).getBody();
    }

    public Long getId(String token) {
        return ((Number) getClaims(token).get("id")).longValue();
    }

    public String getUsername(String token) {
        return (String) getClaims(token).get("username");
    }

    public Boolean isValidate(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(securityKey).build().parseClaimsJws(token).getBody();
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token");
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token");
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token");
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.");
        }
        return false;
    }

    public String parseJwt(HttpServletRequest request){
        String headerAuth=null;     // 1. 변수 초기화

        // 2. 쿠키에서 JWT 추출
        Cookie[] cookies = request.getCookies();
        if(cookies!=null){
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("Authorization")) {
                    log.info("[parseJwt] 쿠키에서 Authorization 캐치");
                    log.info("[parseJwt] 쿠키에서 Authorization : {}", cookie.getValue());
                    headerAuth = cookie.getValue();
                    break;
                }
            }

            // 3. 쿠키에서 JWT를 추출할 수 있었다면 해당 값 반환
            if(headerAuth!=null){
                return headerAuth;
            }
        }

        // 4. 쿠키에서 JWT를 추출할 수 없으면 HTTP 헤더에서 추출
        headerAuth = request.getHeader("Authorization");
        return headerAuth;
    }
}
