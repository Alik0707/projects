package com.example.uploader.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class LocalStorageService implements StorageService{
    private final Path baseDir;

    public LocalStorageService(@Value("${file-storage.base-dir}") String baseDir) {
        this.baseDir = Path.of(baseDir);
    }

    @Override
    public Mono<String> save(FilePart filePart, String idempotencyKey) {
        return Mono.fromCallable(() -> {
                    Files.createDirectories(baseDir); // создаём папку,  если нет ничего страшного это просто void
                    return baseDir;  // if don t use callable ошибки как runtime ioException не избежать. Вдруг ответ на вопрос почему не runnable
                })
                .then(Mono.defer(() -> {
                    String filename = idempotencyKey + "_" + filePart.filename();
                    Path target = baseDir.resolve(filename);
                    return filePart.transferTo(target).thenReturn(target.toString());
                }));

    }
    @Override
    public Mono<Void> delete(String storagePath) {
        return Mono.fromRunnable(() -> {
            try {
                Files.deleteIfExists(Path.of(storagePath));
            } catch (Exception ignor) {
            }
        });
    }
}
