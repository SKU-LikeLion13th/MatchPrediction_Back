package com.sku_sku.MatchPrediction.controller;

import com.sku_sku.MatchPrediction.dto.ReqSignup;
import com.sku_sku.MatchPrediction.service.StudentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
