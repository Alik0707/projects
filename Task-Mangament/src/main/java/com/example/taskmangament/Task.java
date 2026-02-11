package com.example.taskmangament;

import java.time.LocalDateTime;


public record Task(
        Long id,
        Long creatoId,
        Long assignedUserId,
        Status st,
        LocalDateTime start_time,
        LocalDateTime end_time,
        Priority pr
) {
}
