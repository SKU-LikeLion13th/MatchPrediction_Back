package com.sku_sku.MatchPrediction.controller;

import com.sku_sku.MatchPrediction.domain.Student;
import com.sku_sku.MatchPrediction.dto.LoginInfoRes;
import com.sku_sku.MatchPrediction.service.OAuth2Service;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/log")
public class OAuth2Controller {

    private final OAuth2Service oAuth2Service;

    @GetMapping("/status")
    public ResponseEntity<LoginInfoRes> loginStatus(Authentication authentication) {
        return ResponseEntity.status(HttpStatus.OK).body(oAuth2Service.getLoginStatus(authentication));
    }

    @PostMapping("/out")
    public ResponseEntity<String> logout(HttpServletResponse response, Authentication auth) {
        oAuth2Service.logout(response, auth.getName());
        return ResponseEntity.status(HttpStatus.OK).body("로그아웃 성공");
    }

    @PostMapping("/refresh")
    public ResponseEntity<String> refreshAccessToken(HttpServletRequest request, HttpServletResponse response) {
        oAuth2Service.refreshAccessToken(request, response);
        return ResponseEntity.status(HttpStatus.OK).body("Access token 재발급 완료");
    }

}
