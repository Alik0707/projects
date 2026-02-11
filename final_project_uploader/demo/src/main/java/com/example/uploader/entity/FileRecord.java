package com.example.uploader.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data // Lombok автоматически создаёт: геттеры, сеттеры, toString, equals, hashCode
@Table(name = "file_record", schema = "uploader_schema")
public class FileRecord {
    @Id
    private Long id;
    private String idempotencyKey;
    private String originalFilename;
    private String storagePath;
    private Status status;
    private String errorMessage;
    private LocalDateTime createdAt;
}

