package com.sku_sku.MatchPrediction.service;

import com.sku_sku.MatchPrediction.domain.Student;
import com.sku_sku.MatchPrediction.dto.ReqSignup;
import com.sku_sku.MatchPrediction.enums.FeeStatus;
import com.sku_sku.MatchPrediction.reposiroty.PaidStudentRepository;
import com.sku_sku.MatchPrediction.reposiroty.StudentReposiroty;
import com.sku_sku.MatchPrediction.security.JwtUtility;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentReposiroty studentReposiroty;
    private final PaidStudentRepository paidStudentRepository;
    private final JwtUtility jwtUtility;
    private final RedisTemplate<String, String> redisTemplate;
    private final SignupJwtService signupJwtService;

    @Value("${cookie.secure}")
    private boolean isSecure;

    @Value("${cookie.sameSite}")
    private String isSameSite;

    public ResponseCookie signup(ReqSignup reqSignup, HttpServletRequest request, HttpServletResponse response) {
        String email = extractEmailFromSignupCookie(request);
        System.out.println(email);
        if (studentReposiroty.findByEmail(email) != null) {
            throw new IllegalStateException("이미 가입된 사용자입니다.");
        }

        Student student = new Student(
                email,
                reqSignup.major(),
                reqSignup.studentId(),
                reqSignup.name(),
                paidStudentRepository.existsByStudentId(reqSignup.studentId()) ? FeeStatus.PAID : FeeStatus.UNPAID
        );

        studentReposiroty.save(student);

        ResponseCookie cookie = ResponseCookie.from("signup_token", "")
                .httpOnly(true)
                .secure(isSecure)
                .sameSite(isSameSite)
                .path("/")
                .maxAge(0)
                .build();

        response.addHeader("Set-Cookie", cookie.toString());

        return issueToken(student);
    }

    private ResponseCookie issueToken(Student student) {
        String jwt = jwtUtility.generateJwt(
                student.getEmail(),
                student.getMajor(),
                student.getStudentId(),
                student.getName(),
                student.getFeeStatus()
        );

        String refreshToken = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(
                "refresh:" + student.getEmail(),
                refreshToken,
                Duration.ofDays(30)
        );

        return ResponseCookie.from("access_token", jwt)
                .httpOnly(true)
                .secure(isSecure)
                .sameSite(isSameSite)
                .path("/")
                .maxAge(Duration.ofHours(1))
                .build();
    }

    private String extractEmailFromSignupCookie(HttpServletRequest request) {
        if (request.getCookies() == null) {
            throw new IllegalStateException("쿠키가 존재하지 않습니다.");
        }

        for (Cookie cookie : request.getCookies()) {
            if ("signup_token".equals(cookie.getName())) {
                return signupJwtService.getEmailFromSignupJwt(cookie.getValue());
            }
        }

        throw new IllegalStateException("signup_token 쿠키가 없습니다.");
    }
}
