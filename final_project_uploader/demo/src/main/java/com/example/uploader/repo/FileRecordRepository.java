package com.example.uploader.repo;

import com.example.uploader.entity.FileRecord;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface FileRecordRepository extends ReactiveCrudRepository<FileRecord, Long> {
    Mono<FileRecord> findByIdempotencyKey(String idempotencyKey);
}
