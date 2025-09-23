package com.sku_sku.MatchPrediction.controller;

import com.sku_sku.MatchPrediction.domain.Student;
import com.sku_sku.MatchPrediction.dto.ReqSignup;
import com.sku_sku.MatchPrediction.dto.SubmissionInfoRes;
import com.sku_sku.MatchPrediction.service.StudentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/students")
public class StudentController {

    private final StudentService studentService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody ReqSignup reqSignup, HttpServletRequest request, HttpServletResponse response) {
        ResponseCookie cookie = studentService.signup(reqSignup, request, response);

        response.addHeader("Set-Cookie", cookie.toString());
        return ResponseEntity.status(HttpStatus.OK).body("정보 입력 완료");
    }

    @GetMapping("/submission/info")
    public ResponseEntity<SubmissionInfoRes> getSubmissionInfo(@AuthenticationPrincipal Student student) {
        return ResponseEntity.status(HttpStatus.OK).body(studentService.getSubmissionInfo(student));
    }
}
