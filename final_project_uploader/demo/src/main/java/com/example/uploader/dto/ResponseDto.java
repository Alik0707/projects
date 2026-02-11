package com.example.uploader.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import  com.example.uploader.entity.Status;

@Data
@AllArgsConstructor
@Builder
public class ResponseDto {
    private Status status;   // SUCCESS / FAILED
    private String message;  // текстовое сообщение
}
