package com.example.cli.taskmanager.domain;

import java.time.LocalDate;
import java.util.Optional;

public record Task(
    String id,
    String title,
     Optional<LocalDate> dueDate,
    Priority priority,
    Status status
) {}