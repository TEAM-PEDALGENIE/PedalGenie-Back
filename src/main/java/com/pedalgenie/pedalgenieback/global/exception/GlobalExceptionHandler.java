package com.pedalgenie.pedalgenieback.global.exception;

import com.pedalgenie.pedalgenieback.global.ResponseTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ResponseTemplate<Object>> handleCustomException(CustomException exception) {
        ErrorCode errorCode = exception.getErrorCode();

        return ResponseTemplate.createTemplate(
                errorCode.getHttpStatus(),
                false,
                errorCode.getMessage(),
                null
        );
    }

    // 정의한 예외 외에는 500 서버 에러처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseTemplate<Object>> handleGeneralException(Exception ex) {
        // 로그 출력
        ex.printStackTrace();

        return ResponseTemplate.createTemplate(
                HttpStatus.INTERNAL_SERVER_ERROR,
                false,
                "서버 오류가 발생했습니다..",
                null
        );
    }
}
