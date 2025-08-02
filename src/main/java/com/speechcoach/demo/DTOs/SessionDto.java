package com.speechcoach.demo.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class SessionDto {
    private Long id;

    private final Instant time=Instant.now();

    private final String audioKey;

    private final double wpm;

    private final String summary;
}
