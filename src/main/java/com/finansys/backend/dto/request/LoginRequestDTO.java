package com.finansys.backend.dto.request;

import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(@NotBlank(message = "Username ou email é obrigatório") String email, @NotBlank(message = "Senha é obrigatória") String password) {

}
