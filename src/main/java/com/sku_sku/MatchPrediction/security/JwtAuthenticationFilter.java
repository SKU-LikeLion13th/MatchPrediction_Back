package com.sku_sku.MatchPrediction.security;



import com.sku_sku.MatchPrediction.domain.Student;
import com.sku_sku.MatchPrediction.enums.FeeStatus;
import com.sku_sku.MatchPrediction.exception.HandleJwtException;
import com.sku_sku.MatchPrediction.reposiroty.StudentReposiroty;
import com.sku_sku.MatchPrediction.service.OAuth2Service;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtility jwtUtility;
    private final OAuth2Service oAuth2Service; // 주입 추가
    private final RedisTemplate<String, String> redisTemplate; // 주입 추가
    private final StudentReposiroty studentReposiroty; // SecurityContext 재설정 위해 필요

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = jwtUtility.extractTokenFromCookies(request);

        if (token != null) {
            try {
                if (jwtUtility.validateJwt(token)) {
                    Authentication auth = getAuthentication(token);
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            } catch (ExpiredJwtException e) {
                Claims claims = e.getClaims();
                String email = claims.getSubject();
                String role = claims.get("role", String.class);


                if ("ADMIN_LION".equals(role)) {
                    String redisKey = "refresh:" + email;
                    String refreshToken = redisTemplate.opsForValue().get(redisKey);

                    if (refreshToken != null) {
                        // Access 토큰 재발급 및 쿠키 세팅
                        String newJwt = oAuth2Service.refreshAccessTokenInJwtAuthenticationFilter(email, response);

                        Authentication auth = getAuthentication(newJwt);
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    }
                } else {
                    throw new HandleJwtException("만료된 JWT");
                }
                // else: 다른 유저는 그냥 인증 없이 통과 → controller에서 401
            } catch (HandleJwtException e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write(e.getMessage());
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private Authentication getAuthentication(String jwt) {
        Claims claims = jwtUtility.getClaimsFromJwt(jwt);
        Student student = studentReposiroty.findByEmail(claims.getSubject());
        FeeStatus feeStatus = FeeStatus.valueOf(claims.get("feeStatus", String.class));

        return new UsernamePasswordAuthenticationToken(
                student,
                null,
                Collections.singletonList(new SimpleGrantedAuthority(feeStatus.name()))
        );
    }
}
