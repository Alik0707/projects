package com.example.uploader.storage;

import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;

public interface StorageService {

    //Сохраняет файл и возвращает путь где он лежит
    Mono<String> save(FilePart filePart, String idempotencyKey);

}
