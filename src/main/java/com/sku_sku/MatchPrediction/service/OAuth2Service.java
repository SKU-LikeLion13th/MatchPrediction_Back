package com.sku_sku.MatchPrediction.service;


import com.sku_sku.MatchPrediction.domain.Student;
import com.sku_sku.MatchPrediction.dto.LoginInfoRes;
import com.sku_sku.MatchPrediction.exception.InvalidJwtlException;
import com.sku_sku.MatchPrediction.exception.InvalidLoginlException;
import com.sku_sku.MatchPrediction.reposiroty.StudentReposiroty;
import com.sku_sku.MatchPrediction.security.JwtUtility;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class OAuth2Service {
    private final JwtUtility jwtUtility;
    private final RedisTemplate<String, String> redisTemplate;
    private final StudentReposiroty studentReposiroty;

    @Value("${cookie.secure}")
    private boolean isSecure;

    @Value("${cookie.sameSite}")
    private String isSameSite;

    public LoginInfoRes getLoginStatus(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new InvalidLoginlException("로그인이 안 되어 있음");
        }
//        if (student == null) {
//            throw new InvalidLoginlException("로그인이 안 되어 있음");
//        }

        String email = authentication.getName();
        Student student = studentReposiroty.findByEmail(email);

        return new LoginInfoRes(
                student.getEmail(),
                student.getMajor(),
                student.getStudentId(),
                student.getName(),
                student.getPhoneNum(),
                student.getFeeStatus(),
                student.getRoleType()
        );
    }

    public void logout(HttpServletRequest request, HttpServletResponse response, String email) {
        String redisKey = "refresh:" + email;
        redisTemplate.delete(redisKey);

        ResponseCookie deleteToken = ResponseCookie.from("access_token", "")
                .httpOnly(true)
                .secure(isSecure)
                .sameSite(isSameSite)
                .path("/")
                .maxAge(0)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, deleteToken.toString());

        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        ResponseCookie deleteJSession = ResponseCookie.from("JSESSIONID", "")
                .path("/")
                .maxAge(0)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, deleteJSession.toString());

        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
    }


    public void refreshAccessToken(HttpServletRequest request, HttpServletResponse response) {
        String token = jwtUtility.extractTokenFromCookies(request);
        if (token == null) throw new InvalidJwtlException("Access");

        String email = jwtUtility.getClaimsFromJwt(token).getSubject();

        String redisKey = "refresh:" + email;
        if (!redisTemplate.hasKey(redisKey)) {
            throw new InvalidJwtlException("Refresh");
        }

        Student student = studentReposiroty.findByEmail(email);
        String newAccessToken = jwtUtility.generateJwt(email, student.getMajor(), student.getStudentId(), student.getName(), student.getFeeStatus(), student.getRoleType());

        ResponseCookie cookie = ResponseCookie.from("access_token", newAccessToken)
                .httpOnly(true)
                .secure(isSecure)
                .sameSite(isSameSite)
                .path("/")
                .maxAge(Duration.ofHours(1))
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }

    public String refreshAccessTokenInJwtAuthenticationFilter(String email, HttpServletResponse response) {
        String redisKey = "refresh:" + email;
        if (!redisTemplate.hasKey(redisKey)) {
            throw new InvalidJwtlException("Refresh");
        }

        Student student = studentReposiroty.findByEmail(email);
        String newAccessToken = jwtUtility.generateJwt(email, student.getMajor(), student.getStudentId(), student.getName(), student.getFeeStatus(), student.getRoleType());

        ResponseCookie cookie = ResponseCookie.from("access_token", newAccessToken)
                .httpOnly(true)
                .secure(isSecure)
                .sameSite(isSameSite)
                .path("/")
                .maxAge(Duration.ofHours(1))
                .build();

        response.addHeader("Set-Cookie", cookie.toString());

        return newAccessToken;
    }

}

