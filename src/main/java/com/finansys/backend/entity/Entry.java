package com.finansys.backend.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "entries", indexes = {

		@Index(name = "idx_entry_date", columnList = "date"),
		@Index(name = "idx_entry_type", columnList = "type"),
		@Index(name = "idx_entry_paid", columnList = "paid"),
		@Index(name = "idx_entry_category", columnList = "category_id")
})
public class Entry {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@NotBlank(message = "Nome do Lançamento é obrigatório")
	@Size(min = 2, max = 200, message = "Nome deve ter entre 2 e 200 caracteres")
	@Column(name = "name", nullable = false, length = 200)
	private String name;
	
	@Size(max = 1000, message = "Descrição deve ter no máximo 1000 caracteres")
	@Column(name = "description", length = 1000)
	private String description;
	
	@NotBlank(message = "Tipo do Lançamento é obrigatório")
	@Pattern(regexp = "^(Receita|Despesa)$",
	message = "Tipo deve ser Receita ou Despesa")
	@Column(name = "type", nullable = false, length = 20)
	private String type;
	
	@NotNull(message = "Valor é obrigatório")
	@DecimalMin(value = "0.01", message = "Valor deve ser maior que zero")
	@Digits(integer = 10, fraction = 2,
	message = "Valor deve ter no máximo 10 dígitos inteiros e 2 decimais")
	@Column(name = "amount", nullable = false, precision = 12, scale = 2)
	private BigDecimal amount;
	
	@NotNull(message = "Data é obrigatória")
	@Column(name = "date", nullable = false)
	private LocalDate date;
	
	@Default
	@Column(name = "paid", nullable = false)
	private Boolean paid = false;
	
	@NotNull(message = "Categoria é obrigatória")
	@Column(name = "category_id", nullable = false)
	private Long categoryId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "category_id", insertable = false, updatable = false)
	private Category category;
	
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;
	
	@Column(name = "updated_at")
	private LocalDateTime updatedAt;
	
	@PrePersist
	protected void onCreate() {
		
		this.createdAt = LocalDateTime.now();
		this.updatedAt = LocalDateTime.now();
		
		if (this.paid == null) {
			
			this.paid = false;
		}
	}
	
	@PreUpdate
	protected void onUpdate() {
		
		this.updatedAt = LocalDateTime.now();
	}
	
	public boolean isReceita() {
		
		return "Receita".equals(this.type);
	}
	
	public boolean isDespesa() {
	
		return "Despesa".equals(this.type);
	}
}
