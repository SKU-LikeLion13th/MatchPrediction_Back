package com.sku_sku.MatchPrediction.security;


import com.sku_sku.MatchPrediction.domain.Student;
import com.sku_sku.MatchPrediction.reposiroty.StudentReposiroty;
import com.sku_sku.MatchPrediction.service.SignupJwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtility jwtUtility;
    private final StudentReposiroty studentReposiroty;
    private final RedisTemplate<String, String> redisTemplate;
    private final SignupJwtService signupJwtService;

    @Value("${cookie.secure}")
    private boolean isSecure;

    @Value("${cookie.sameSite}")
    private String isSameSite;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");

        Student student = studentReposiroty.findByEmail(email);
        if (student == null) {
            String signupJwt = signupJwtService.createSignupJwt(email);
            ResponseCookie cookie = ResponseCookie.from("signup_token", signupJwt)
                    .httpOnly(true)
                    .secure(isSecure)
                    .sameSite(isSameSite)
                    .path("/")
                    .maxAge(Duration.ofMinutes(10))
                    .build();

            response.addHeader("Set-Cookie", cookie.toString());

            response.sendRedirect("http://localhost:5173/member");
            return;
        }

        String jwt = jwtUtility.generateJwt(email, student.getMajor(), student.getStudentId(), student.getName(), student.getFeeStatus(), student.getRoleType());

        ResponseCookie cookie = ResponseCookie.from("access_token", jwt)
                .httpOnly(true)
                .secure(isSecure)
                .sameSite(isSameSite)
                .path("/")
                .maxAge(Duration.ofHours(1))
                .build();

        response.addHeader("Set-Cookie", cookie.toString());

        String refreshToken = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set("refresh:" + email, refreshToken, Duration.ofDays(30));

        String redirectUrl = request.getParameter("state");
        System.out.println("redirectUrl: " + redirectUrl);

//        response.sendRedirect(redirectUrl);
        if (redirectUrl.startsWith("http://localhost")) {
            response.sendRedirect("http://localhost:5173/matchinfo");
        } else {
            response.sendRedirect("https://solvit-final/matchinfo");
        }
    }
}