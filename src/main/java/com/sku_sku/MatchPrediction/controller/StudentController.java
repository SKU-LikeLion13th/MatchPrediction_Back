package com.sku_sku.MatchPrediction.controller;

import com.sku_sku.MatchPrediction.domain.PredictionSubmission;
import com.sku_sku.MatchPrediction.domain.Student;
import com.sku_sku.MatchPrediction.dto.ReqSignup;
import com.sku_sku.MatchPrediction.dto.SubmissionInfoRes;
import com.sku_sku.MatchPrediction.reposiroty.StudentReposiroty;
import com.sku_sku.MatchPrediction.security.CustomOAuth2User;
import com.sku_sku.MatchPrediction.service.StudentService;
import jakarta.servlet.ServletRegistration;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/students")
public class StudentController {

    private final StudentService studentService;
    private final StudentReposiroty studentReposiroty;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody ReqSignup reqSignup, HttpServletRequest request, HttpServletResponse response) {
        ResponseCookie cookie = studentService.signup(reqSignup, request, response);

        response.addHeader("Set-Cookie", cookie.toString());
        return ResponseEntity.status(HttpStatus.OK).body("정보 입력 완료");
    }

    @GetMapping("/submission/info")
    public ResponseEntity<SubmissionInfoRes> getSubmissionInfo(@AuthenticationPrincipal Student student) {
        System.out.println("student: " + student);
        return ResponseEntity.status(HttpStatus.OK).body(studentService.getSubmissionInfo(student));
    }
}
