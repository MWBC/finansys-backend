package com.finansys.backend.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.finansys.backend.dto.request.EntryRequestDTO;
import com.finansys.backend.dto.response.EntryResponseDTO;
import com.finansys.backend.dto.response.MessageResponseDTO;
import com.finansys.backend.service.EntryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("entries")
@Tag(name = "Lançamentos", description = "Endpoints para gerenciamento de Lançamentos financeiros")
@SecurityRequirement(name = "bearerAuth")
public class EntryController {
	
	@Autowired
    private EntryService entryService;
    
    @PostMapping
    @Operation(summary = "Criar lançamento", description = "Cria um novo lançamento financeiro")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Lançamento criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados de lançamento inválidos"),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
        @ApiResponse(responseCode = "404", description = "Categoria não encontrada")
    })
//    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> createEntry(@Valid @RequestBody EntryRequestDTO entryRequest) {
    	
        try {
        	
            EntryResponseDTO entryResponse = entryService.createEntry(entryRequest);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(entryResponse);
        } catch (RuntimeException e) {
        	
            if (e.getMessage().contains("não encontrada")) {
            	
                return ResponseEntity.notFound()
                    .build();
            } else {
            	
                return ResponseEntity.badRequest()
                    .body(new MessageResponseDTO(e.getMessage(), false, LocalDateTime.now()));
            }
        } catch (Exception e) {
        	
            return ResponseEntity.badRequest()
                .body(new MessageResponseDTO("Erro ao criar lançamento: " + e.getMessage(), false, LocalDateTime.now()));
        }
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Obter lançamento por ID", description = "Retorna um lançamento específica pelo ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lançamento encontrado"),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
        @ApiResponse(responseCode = "404", description = "Lançamento não encontrado")
    })
    
//    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> getEntryById(@PathVariable Long id) {
    	
        try {
        	
            EntryResponseDTO entryResponse = entryService.getEntryById(id);
            
            return ResponseEntity.ok(entryResponse);
        } catch (RuntimeException e) {
        	
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
        	
            return ResponseEntity.badRequest()
                .body(new MessageResponseDTO("Erro ao buscar lançamento: " + e.getMessage(), false, LocalDateTime.now()));
        }
    }
    
    @GetMapping
    @Operation(summary = "Listar todas os lançamentos", description = "Retorna lista de todos os lançamentos")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de lançamentos retornada com sucesso"),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado")
    })
//    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<EntryResponseDTO>> getAllEntries() {
    	
        List<EntryResponseDTO> entries = entryService.getAllEntries();
        
        return ResponseEntity.ok(entries);
    }

    @GetMapping("/paginated")
    @Operation(summary = "Listar lançamentos com paginação", 
               description = "Retorna lista paginada de lançamentos")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista paginada retornada com sucesso"),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado")
    })
//    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Page<EntryResponseDTO>> getAllEntriesPaginated(
            @Parameter(description = "Número da página (começando em 0)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo para ordenação")
            @RequestParam(defaultValue = "date") String sortBy,
            @Parameter(description = "Direção da ordenação (asc ou desc)")
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Page<EntryResponseDTO> entries = entryService.getAllEntriesPaginated(page, size, sortBy, sortDir);
        
        return ResponseEntity.ok(entries);
    }
    
    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Listar lançamentos por categoria", 
               description = "Retorna lançamentos de uma categoria específica")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de lançamentoss retornada com sucesso"),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado")
    })
//    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<EntryResponseDTO>> getEntriesByCategory(@PathVariable Long categoryId) {
    	
        List<EntryResponseDTO> entries = entryService.getEntriesByCategory(categoryId);
        
        return ResponseEntity.ok(entries);
    }
    
    @GetMapping("/type/{type}")
    @Operation(summary = "Listar lançamentos por tipo", 
               description = "Retorna lançamentos de um tipo específico (RECEITA ou DESPESA)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de lançamentos retornada com sucesso"),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado")
    })
//    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<EntryResponseDTO>> getEntriesByType(@PathVariable String type) {
    	
        List<EntryResponseDTO> entries = entryService.getEntriesByType(type);
        
        return ResponseEntity.ok(entries);
    }
    
    @GetMapping("/paid/{paid}")
    @Operation(summary = "Listar lançamentos por status de pagamento", 
               description = "Retorna lançamentos filtrados por status de pagamento")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de lançamentos retornada com sucesso"),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado")
    })
//    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<EntryResponseDTO>> getEntriesByPaidStatus(@PathVariable Boolean paid) {
    	
        List<EntryResponseDTO> entries = entryService.getEntriesByPaidStatus(paid);
        
        return ResponseEntity.ok(entries);
    }
    
    @GetMapping("/date-range")
    @Operation(summary = "Listar lançamentos por período", 
               description = "Retorna lançamentos dentro de um período específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de lançamentos retornada com sucesso"),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
        @ApiResponse(responseCode = "400", description = "Datas inválidas")
    })
//    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<EntryResponseDTO>> getEntriesByDateRange(
            @Parameter(description = "Data de início (formato: yyyy-MM-dd)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "Data de fim (formato: yyyy-MM-dd)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        List<EntryResponseDTO> entries = entryService.getEntriesByDateRange(startDate, endDate);
        
        return ResponseEntity.ok(entries);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar lançamento", description = "Atualiza um lançamento existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lançamento atualizado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados de lançamento inválidos"),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
        @ApiResponse(responseCode = "404", description = "Entrada ou categoria não encontrada")
    })
//    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> updateEntry(@PathVariable Long id, 
                                       @Valid @RequestBody EntryRequestDTO entryRequest) {
        try {
        	
            EntryResponseDTO entryResponse = entryService.updateEntry(id, entryRequest);
            
            return ResponseEntity.ok(entryResponse);
        } catch (RuntimeException e) {
        	
            if (e.getMessage().contains("não encontrada")) {
            	
            	return ResponseEntity.notFound()
                    .build();
            } else {
            	
                return ResponseEntity.badRequest()
                    .body(new MessageResponseDTO(e.getMessage(), false, LocalDateTime.now()));
            }
        } catch (Exception e) {
        	
            return ResponseEntity.badRequest()
                .body(new MessageResponseDTO("Erro ao atualizar entrada: " + e.getMessage(), false, LocalDateTime.now()));
        }
    }
    
    @PatchMapping("/{id}/paid")
    @Operation(summary = "Atualizar status de pagamento", 
               description = "Atualiza apenas o status de pagamento de um lançamento")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Status atualizado com sucesso"),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
        @ApiResponse(responseCode = "404", description = "Lançamento não encontrado")
    })
//    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> updatePaidStatus(@PathVariable Long id, 
                                            @RequestParam Boolean paid) {
        try {
            EntryResponseDTO entryResponse = entryService.updatePaidStatus(id, paid);
            
            return ResponseEntity.ok(entryResponse);
        } catch (RuntimeException e) {
        	
            return ResponseEntity.notFound()
                .build();
        } catch (Exception e) {
        	
            return ResponseEntity.badRequest()
                .body(new MessageResponseDTO("Erro ao atualizar status: " + e.getMessage(), false, LocalDateTime.now()));
        }
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir lançamento", description = "Exclui um lançamento")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Lançamento excluída com sucesso"),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
        @ApiResponse(responseCode = "404", description = "Lançamento não encontrado")
    })
//    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> deleteEntry(@PathVariable Long id) {
    	
        try {
        	
            entryService.deleteEntry(id);
            
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
        	
            return ResponseEntity.notFound()
                .build();
        } catch (Exception e) {
        	
            return ResponseEntity.badRequest()
                .body(new MessageResponseDTO("Erro ao excluir entrada: " + e.getMessage(), false, LocalDateTime.now()));
        }
    }
    
    @GetMapping("/total/type/{type}")
    @Operation(summary = "Obter total por tipo", 
               description = "Retorna o valor total de lançamentos pagos por tipo")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Total calculado com sucesso"),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado")
    })
//    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<BigDecimal> getTotalByType(@PathVariable String type) {
    	
        BigDecimal total = entryService.getTotalByType(type);
        
        return ResponseEntity.ok(total);
    }
    
    @GetMapping("/total/category/{categoryId}")
    @Operation(summary = "Obter total por categoria", 
               description = "Retorna o valor total de lançamentos pagos por categoria")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Total calculado com sucesso"),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado")
    })
//    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<BigDecimal> getTotalByCategory(@PathVariable Long categoryId) {
    	
        BigDecimal total = entryService.getTotalByCategory(categoryId);
        
        return ResponseEntity.ok(total);
    }
    
    @GetMapping("/count")
    @Operation(summary = "Contar entradas", description = "Retorna o total de lançamentos")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Contagem retornada com sucesso"),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado")
    })
//    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Long> getTotalCount() {
    	
        long count = entryService.getTotalCount();
        
        return ResponseEntity.ok(count);
    }
}
