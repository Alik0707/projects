package com.example.uploader.service;

import com.example.uploader.dto.ResponseDto;
import com.example.uploader.entity.FileRecord;
import com.example.uploader.entity.Status;
import com.example.uploader.repo.FileRecordRepository;
import com.example.uploader.storage.StorageService;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static com.example.uploader.entity.Status.FAILED;

@Service
public class FileUploadService {

    private final FileRecordRepository repository;
    private final StorageService storage;

    public FileUploadService(FileRecordRepository repository, StorageService storage) {
        this.repository = repository;
        this.storage = storage;
    }

    //Основной метод загрузки файла
    public Mono<ResponseDto> uploadFile(FilePart filePart, String idempotencyKey) {
        // 1️⃣ Проверяем, не был ли файл уже загружен с этим ключом
        return repository.findByIdempotencyKey(idempotencyKey)
                // 2️⃣ Если запись найдена → файл уже загружен
                .flatMap(existing -> Mono.just(ResponseDto.builder()
                        .status(Status.FAILED)
                        .message("Файл уже загружен")
                        .build()))
                // 3️⃣ Если записи нет → сохраняем файл и создаём запись
                .switchIfEmpty(
                        storage.save(filePart, idempotencyKey)
                                .flatMap(path -> {
                                    FileRecord record = new FileRecord();
                                    record.setIdempotencyKey(idempotencyKey);
                                    record.setOriginalFilename(filePart.filename());
                                    record.setStoragePath(path);
                                    record.setStatus(Status.SUCCESS);
                                    record.setCreatedAt(LocalDateTime.now());
                                    return repository.save(record)
                                            .map(saved -> ResponseDto.builder()
                                                    .status(Status.SUCCESS)
                                                    .message("Файл успешно загружен")
                                                    .build());
                                })
                );
    }


}
