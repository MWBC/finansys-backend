package com.finansys.backend.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

public record EntryResponseDTO(Long id, String name, String description, String type, BigDecimal amount, @JsonFormat(pattern = "dd/MM/yyyy") LocalDate date, Boolean paid, Long categoryId, String categoryName, LocalDateTime createdAt, LocalDateTime updateddAt) {

}
