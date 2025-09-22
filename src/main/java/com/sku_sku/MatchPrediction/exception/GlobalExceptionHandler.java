package com.sku_sku.MatchPrediction.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Jwt
    @ExceptionHandler(HandleJwtException.class)
    public ResponseEntity<String> handleJwt(HandleJwtException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    }

    @ExceptionHandler(InvalidLoginlException.class)
    public ResponseEntity<String> invalidLogin(InvalidLoginlException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    }

    @ExceptionHandler(InvalidJwtlException.class)
    public ResponseEntity<String> invalidJwt(InvalidJwtlException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage() + " token 없음");
    }

    @ExceptionHandler(InvalidStudentPKException.class)
    public ResponseEntity<String> invalidStudentPK(InvalidStudentPKException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("그런 학생 없음");
    }

    @ExceptionHandler(InvalidSportTypeException.class)
    public ResponseEntity<String> invaliddSportType(InvalidSportTypeException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 경기 예측이 없음");
    }

    @ExceptionHandler(LimitSubmissionException.class)
    public ResponseEntity<String> limitSubmission(LimitSubmissionException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("응모 횟수 초과");
    }
}
