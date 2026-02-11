package com.example.uploader.dto;

import lombok.Data;

@Data
public class RequestDto {
    private String idempotencyKey;   // ключ для идемпотентности
    private String originalFilename;  // имя файла от клиента
}