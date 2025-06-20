package com.finansys.backend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.finansys.backend.dto.request.CategoryRequestDTO;
import com.finansys.backend.dto.response.CategoryResponseDTO;
import com.finansys.backend.entity.Category;
import com.finansys.backend.repository.CategoryRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
@Transactional
public class CategoryService {

	@Autowired
	private CategoryRepository categoryRepository;
	
	public CategoryResponseDTO createCategory(CategoryRequestDTO categoryRequestDTO) {
		
		if(categoryRepository.existsByName(categoryRequestDTO.name())) {
			
			throw new RuntimeException("Já existe uma categoria com esse nome.");
		}
		
		Category category = Category.builder().name(categoryRequestDTO.name())
				.description(categoryRequestDTO.description())
				.build();
		
		return this.convertToResponse(category);
	}
	
	@Transactional(readOnly = true)
	public CategoryResponseDTO getCategoryById(Long id) {
		
		Category category = categoryRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada com ID: " + id));
		
		return convertToResponse(category);
	}
	
	@Transactional(readOnly = true)
	public List<CategoryResponseDTO> getAllCategories() {
		
		List<Category> categories = categoryRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
		
		return categories.stream().map(this::convertToResponse).collect(Collectors.toList());
	}
	
	@Transactional(readOnly = true)
	public Page<CategoryResponseDTO> getAllCategoriesPaginated(int page, int size, String sortBy, String sortDir) {
		
		Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
		
		Pageable pageable = PageRequest.of(page, size, sort);
		
		Page<Category> categories = categoryRepository.findAll(pageable);
		
		return categories.map(this::convertToResponse);
	}
	
	@Transactional(readOnly = true)
	public List<CategoryResponseDTO> searchCategories(String searchTerm) {
		
		List<Category> categories = categoryRepository.findByNameOrDescriptionContaining(searchTerm);
		
		return categories.stream().map(this::convertToResponse).collect(Collectors.toList());
	}
	
	public CategoryResponseDTO updateCategory(Long id, CategoryRequestDTO categoryRequest) {
		
		Category category = categoryRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada com ID: " + id));
		
		// Verificar se o novo nome já existe em outra categoria
		if (categoryRepository.existsByNameAndIdNot(categoryRequest.name(),id)) {
			
			throw new RuntimeException("Já existe outra categoria com este nome");
		}
		
		category.setName(categoryRequest.name());
		category.setDescription(categoryRequest.description());
		
		Category updatedCategory = categoryRepository.save(category);
		
		return convertToResponse(updatedCategory);
	}
	
	public void deleteCategory(Long id) {
		
		Category category = categoryRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada com ID: " + id));
		
		// Verificar se a categoria tem entradas associadas
		if (!category.getEntries().isEmpty()) {
			
			throw new RuntimeException("Não é possível excluir categoria que possui entradas associadas");
		}
		
		categoryRepository.delete(category);
	}
	
	@Transactional(readOnly = true)
	public boolean existsById(Long id) {
		
		return categoryRepository.existsById(id);
	}
	
	@Transactional(readOnly = true)
	public long getTotalCount() {
	
		return categoryRepository.count();
	}
	
	private CategoryResponseDTO convertToResponse(Category category) {
		
		return new CategoryResponseDTO(category.getId(), 
				category.getName(), 
				category.getDescription(), 
				category.getCreatedAt(), 
				category.getUpdatedAt(), 
				category.getEntries().size());
	}
}
