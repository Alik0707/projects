package com.example.demo;

import java.time.LocalDate;

public record Reserv(
        Long id,
        Long userId,
        Long roomId,
        LocalDate start,
        LocalDate end,
        ReservStatus status

){};
