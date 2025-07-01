package com.finansys.backend.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.finansys.backend.dto.request.CategoryRequestDTO;
import com.finansys.backend.dto.response.CategoryResponseDTO;
import com.finansys.backend.dto.response.MessageResponseDTO;
import com.finansys.backend.service.CategoryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("/categories")
@Tag(name = "Categorias", description = "Endpoints para gerenciamento de Categorias")

//annotation springdoc-openai para indicar que o endpoint exige autenticação
//@SecurityRequirement(name = "bearerAuth")

@CrossOrigin(origins = "*", maxAge = 3600)
public class CategoryController {

	@Autowired
	private CategoryService categoryService;
	
	@PostMapping()
    @Operation(summary = "Criar categoria", description = "Cria uma nova categoria")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Categoria criada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos"),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
        @ApiResponse(responseCode = "409", description = "Categoria com este nome já existe")
    })
//    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
	public ResponseEntity<?> createCategory(@Valid @RequestBody CategoryRequestDTO categoryRequest) {
		
        try {
        	
            CategoryResponseDTO categoryResponse = categoryService.createCategory(categoryRequest);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(categoryResponse);
        } catch (RuntimeException e) {
        	
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new MessageResponseDTO(e.getMessage(), false, LocalDateTime.now()));
        } catch (Exception e) {
        	
            return ResponseEntity.badRequest().body(new MessageResponseDTO("Erro ao criar categoria: " + e.getMessage(), false, LocalDateTime.now()));
        }
    }
	
	@GetMapping("/{id}")
    @Operation(summary = "Obter categoria por ID", description = "Retorna uma categoria específica pelo ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Categoria encontrada"),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
        @ApiResponse(responseCode = "404", description = "Categoria não encontrada")
    })
//    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> getCategoryById(@PathVariable Long id) {
        
		try {
			
            CategoryResponseDTO categoryResponse = categoryService.getCategoryById(id);
            
            return ResponseEntity.ok(categoryResponse);
        } catch (RuntimeException e) {
        	
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
        	
            return ResponseEntity.badRequest().body(new MessageResponseDTO("Erro ao buscar categoria: " + e.getMessage(), false, LocalDateTime.now()));
        }
    }
	
	@GetMapping("")
    @Operation(summary = "Listar todas as categorias", description = "Retorna lista de todas as categorias")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de categorias retornada com sucesso"),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado")
    })
//    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<CategoryResponseDTO>> getAllCategories() {
		
        List<CategoryResponseDTO> categories = categoryService.getAllCategories();
        
        return ResponseEntity.ok(categories);
    }
	
	@GetMapping("/paginated")
    @Operation(summary = "Listar categorias com paginação", 
               description = "Retorna lista paginada de categorias")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista paginada retornada com sucesso"),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado")
    })
//    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Page<CategoryResponseDTO>> getAllCategoriesPaginated(
            @Parameter(description = "Número da página (começando em 0)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo para ordenação")
            @RequestParam(defaultValue = "name") String sortBy,
            @Parameter(description = "Direção da ordenação (asc ou desc)")
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Page<CategoryResponseDTO> categories = categoryService.getAllCategoriesPaginated(page, size, sortBy, sortDir);
        
        return ResponseEntity.ok(categories);
    }
	
	@GetMapping("/search")
    @Operation(summary = "Buscar categorias", 
               description = "Busca categorias por nome ou descrição")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso"),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado")
    })
//    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<CategoryResponseDTO>> searchCategories(
            @Parameter(description = "Termo de busca")
            @RequestParam String searchTerm) {
        
        List<CategoryResponseDTO> categories = categoryService.searchCategories(searchTerm);
        
        return ResponseEntity.ok(categories);
    }
	
	@PutMapping("/{id}")
    @Operation(summary = "Atualizar categoria", description = "Atualiza uma categoria existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Categoria atualizada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos"),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
        @ApiResponse(responseCode = "404", description = "Categoria não encontrada"),
        @ApiResponse(responseCode = "409", description = "Nome da categoria já existe")
    })
//	    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryRequestDTO categoryRequest) {
        try {
        	
            CategoryResponseDTO categoryResponse = categoryService.updateCategory(id, categoryRequest);
            
            return ResponseEntity.ok(categoryResponse);
        } catch (RuntimeException e) {
            
        		if (e.getMessage().contains("não encontrada")) {
            	
                return ResponseEntity.notFound().build();
            } else {
            
            	return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new MessageResponseDTO(e.getMessage(), false, LocalDateTime.now()));
            }
        } catch (Exception e) {
        	
            return ResponseEntity.badRequest().body(new MessageResponseDTO("Erro ao atualizar categoria: " + e.getMessage(), false, LocalDateTime.now()));
        }
    }
	 
	@DeleteMapping("/{id}")
    @Operation(summary = "Excluir categoria", description = "Exclui uma categoria")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Categoria excluída com sucesso"),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
        @ApiResponse(responseCode = "404", description = "Categoria não encontrada"),
        @ApiResponse(responseCode = "409", description = "Categoria possui entradas associadas")
    })
//	    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
	 
        try {
        	
            categoryService.deleteCategory(id);
            
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
        	
            if (e.getMessage().contains("não encontrada")) {
            	
                return ResponseEntity.notFound().build();
            } else {
            	
                return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new MessageResponseDTO(e.getMessage(), false, LocalDateTime.now()));
            }
        } catch (Exception e) {
        	
            return ResponseEntity.badRequest()
                .body(new MessageResponseDTO("Erro ao excluir categoria: " + e.getMessage(), false, LocalDateTime.now()));
        }
    }
	 
	@GetMapping("/count")
    @Operation(summary = "Contar categorias", description = "Retorna o total de categorias")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Contagem retornada com sucesso"),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado")
    })
//    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Long> getTotalCount() {
		
        long count = categoryService.getTotalCount();
        
        return ResponseEntity.ok(count);
    }
}
