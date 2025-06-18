package com.finansys.backend.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record EntryRequestDTO(@NotBlank(message = "Nome da entrada é obrigatório") @Size(min = 2, max = 200, message = "Nome deve ter entre 2 e 200 caracteres") String name, @Size(max = 1000, message = "Descrição deve ter no máximo 1000 caracteres") String description, @NotBlank(message = "Tipo de lançamento é obrigatório") @Pattern(regexp = "^(Receita|Despesa)$", message = "Tipo deve ser Receita ou Despesa") String type, @NotNull(message = "Valor é obrigatório") @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero") @Digits(integer = 10, fraction = 2, message = "Valor deve ter no máximo 10 dígitos inteiros e 2 decimais") BigDecimal amount, @NotNull(message = "Data é obrigatória") LocalDate date, Boolean paid, @NotNull(message = "ID da categoria é obrigatório") Long categoryId) {

}
