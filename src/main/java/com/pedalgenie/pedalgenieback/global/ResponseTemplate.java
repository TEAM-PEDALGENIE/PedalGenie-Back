package com.pedalgenie.pedalgenieback.global;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@JsonInclude(JsonInclude.Include.NON_NULL) // null인 필드는 제외
@Builder
public class ResponseTemplate<T> {
    public int status;
    public boolean success;
    public String message;
    public T data;

    public static <T> ResponseEntity<ResponseTemplate<T>> createTemplate(HttpStatus status, boolean success, String message, T data) {
        ResponseTemplate<T> responseTemplate = ResponseTemplate.<T>builder()
                .status(status.value())
                .success(success)
                .message(message)
                .data(data)
                .build();

        return ResponseEntity
                .status(status)
                .body(responseTemplate);
    }
}

