package com.example.uploader.controller;

import com.example.uploader.dto.ResponseDto;
import com.example.uploader.service.FileUploadService;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
public class FileUploadController {

    private final FileUploadService service;

    public FileUploadController(FileUploadService service) {
        this.service = service;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseDto> upload(@RequestPart("file") FilePart file,
                                           @RequestPart("idempotencyKey") String idempotencyKey) {
        return service.uploadFile(file, idempotencyKey);
    }


}
