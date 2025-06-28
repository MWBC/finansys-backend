package com.finansys.backend.dto.response;

import java.time.LocalDateTime;

public record MessageResponseDTO(String message, boolean success, LocalDateTime timestamp) {

	
}
