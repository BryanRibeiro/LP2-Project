package com.projectlp2.dto;

import lombok.Data;

@Data
public class ErrorResponse {
    private String errorCode;
    private String errorMsg;

    public ErrorResponse(String errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }
}
