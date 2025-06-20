package com.finansys.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryRequestDTO(@NotBlank(message = "Nome da categoria é obrigatório") @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres") String name, @Size(max = 500, message = "Descrição deve ter no máximo 500 caracteres") String description) {

}
