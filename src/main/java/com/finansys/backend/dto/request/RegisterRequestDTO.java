package com.finansys.backend.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequestDTO(@NotBlank(message = "Nome é obrigatório") @Size(max = 50, message = "Nome deve ter até 50 caracteres") String name, @NotBlank(message = "Email é obrigatório") @Email(message = "Email deve ter formato válido") @Size(max = 100, message = "Email deve ter no máximo 100 caracteres") String email, @NotBlank(message = "Senha é obrigatória") @Size(min = 6, max = 100, message = "Senha deve ter entre 6 e 100 caracteres") String password) {

}
