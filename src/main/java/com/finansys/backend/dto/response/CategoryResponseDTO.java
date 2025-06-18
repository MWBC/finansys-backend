package com.finansys.backend.dto.response;

import java.time.LocalDateTime;

public record CategoryResponseDTO(Long id, String name, String description, LocalDateTime createdAt, LocalDateTime updatedAt, int entryCount) {

}
