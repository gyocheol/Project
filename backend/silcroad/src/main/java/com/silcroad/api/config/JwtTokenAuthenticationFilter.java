package com.silcroad.api.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailService customUserDetailService;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = jwtTokenProvider.parseJwt(request);
        if (token == null || token.trim().isEmpty()) {
            // 토큰이 없더라도 요청가능한 api uri
            if (request.getRequestURI().equals("/api/login") || request.getRequestURI().equals("/api/sign")
                || request.getRequestURI().equals("/login")) {
                log.info("[doFilterInternal] : 토큰이 없는 uri : {}", request.getRequestURI());
                response.addHeader("Access-Control-Allow-Origin", "*");
                filterChain.doFilter(request, response);
                return;
            }
            // Error 던지기
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            objectMapper.writeValue(response.getWriter(), new IllegalStateException("토큰이 유효하지 않습니다."));
            return;
        }
        try {
            // 유효성 검사
            if (!jwtTokenProvider.isValidate(token)) {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.setCharacterEncoding("UTF-8");
                objectMapper.writeValue(response.getWriter(), new IllegalStateException("토큰이 유효하지 않습니다."));
                response.addHeader("Access-Control-Allow-Origin", "*");
                return;
            }
            log.info("request: {}", request.getHeader("Authorization"));
            log.info("token: {}", token);

            // 추출된 JWT 토큰이 null이 아닌 경우, 해당 토큰에서 identity 값을 가져오기
            String username = jwtTokenProvider.getUsername(token);
            log.info("username: {}", username);
            CustomUserDetails userDetails = customUserDetailService.loadUserByUsername(username);
            log.info("userDetail.getAuthorities: {}", userDetails.getAuthorities());

            // 가져온 사용자 정보를 사용하여 UsernamePasswordAuthenticationToken 객체를 생성하고, SecurityContext에 이를 설정
            Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContext context = SecurityContextHolder.getContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);
            log.info("SecurityContextHolder 저장 완료");

        } catch (ExpiredJwtException e){
            log.info("[doFilterInternal]에서 발생 : {}", e.getMessage());
            e.printStackTrace();
        }
        // HTTP 요청을 필터링한 후 다음 필터로 체인을 전달
        response.addHeader("Access-Control-Allow-Origin", "*");
        filterChain.doFilter(request, response);
    }
}
