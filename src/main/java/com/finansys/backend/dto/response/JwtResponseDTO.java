package com.finansys.backend.dto.response;

import java.time.LocalDateTime;

public record JwtResponseDTO(String token, String type, Long id, String name, String email, String role, LocalDateTime expiresAt) {

}
