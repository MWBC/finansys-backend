package com.finansys.backend.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.finansys.backend.dto.request.EntryRequestDTO;
import com.finansys.backend.dto.response.EntryResponseDTO;
import com.finansys.backend.entity.Category;
import com.finansys.backend.entity.Entry;
import com.finansys.backend.repository.CategoryRepository;
import com.finansys.backend.repository.EntryRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
@Transactional
public class EntryService {

	@Autowired
	private EntryRepository entryRepository;
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	public EntryResponseDTO createEntry(EntryRequestDTO entryRequest) {
		
		// Verificar se a categoria existe
		Category category = categoryRepository.findById(entryRequest.categoryId()).orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada com ID: " + entryRequest.categoryId()));
		
		Entry entry = new Entry();
		entry.setName(entryRequest.name());
		entry.setDescription(entryRequest.description());
		entry.setType(entryRequest.type());
		entry.setAmount(entryRequest.amount());
		entry.setDate(entryRequest.date());
		entry.setCategoryId(entryRequest.categoryId());
		
		
		if (entryRequest.paid() != null) {
			
			entry.setPaid(entryRequest.paid());
		}
		
		Entry savedEntry = entryRepository.save(entry);
		
		return convertToResponse(savedEntry, category.getName());
	}
	
	@Transactional(readOnly = true)
	public EntryResponseDTO getEntryById(Long id) {
		
		Entry entry = entryRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Lançamento não encontrado com ID: " + id));
		
		Category category = categoryRepository.findById(entry.getCategoryId()).orElse(null);
		
		String categoryName = category != null ? category.getName() : "Categoria não encontrada";
		
		return convertToResponse(entry, categoryName);
	}
	
	@Transactional(readOnly = true)
	public List<EntryResponseDTO> getAllEntries() {
		
		List<Entry> entries = entryRepository.findAll(Sort.by(Sort.Direction.DESC,"date"));
		
		return entries.stream().map(entry -> {
			
			Category category = categoryRepository.findById(entry.getCategoryId()).orElse(null);
			
			String categoryName = category != null ? category.getName() : "Categoria não encontrada";
			
			return convertToResponse(entry, categoryName);
		})
		.collect(Collectors.toList());
	}
	
	@Transactional(readOnly = true)
	public Page<EntryResponseDTO> getAllEntriesPaginated(int page, int size, String sortBy, String sortDir) {
		
		Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
		
		Pageable pageable = PageRequest.of(page, size, sort);
		
		Page<Entry> entries = entryRepository.findAll(pageable);
		
		return entries.map(entry -> {
			
			Category category = categoryRepository.findById(entry.getCategoryId()).orElse(null);
			
			String categoryName = category != null ? category.getName() : "Categoria não encontrada";
			
			return convertToResponse(entry, categoryName);
		});
	}
	
	@Transactional(readOnly = true)
	public List<EntryResponseDTO> getEntriesByCategory(Long categoryId) {
		
		List<Entry> entries = entryRepository.findByCategoryIdOrderByDateDesc(categoryId);
		
		Category category = categoryRepository.findById(categoryId).orElse(null);
		
		String categoryName = category != null ? category.getName() : "Categoria não encontrada";
		
		return entries.stream().map(entry -> convertToResponse(entry, categoryName)).collect(Collectors.toList());
	}
	
	@Transactional(readOnly = true)
	public List<EntryResponseDTO> getEntriesByType(String type) {
			
		List<Entry> entries = entryRepository.findByType(type);
		
		return entries.stream().map(entry -> {
			
			Category category = categoryRepository.findById(entry.getCategoryId()).orElse(null);
			
			String categoryName = category != null ? category.getName() : "Categoria não encontrada";
			
			return convertToResponse(entry, categoryName);
		})
		.collect(Collectors.toList());
	}
	
	@Transactional(readOnly = true)
	public List<EntryResponseDTO> getEntriesByPaidStatus(Boolean paid) {
			
		List<Entry> entries = entryRepository.findByPaid(paid);
		
		return entries.stream().map(entry -> {
			
			Category category = categoryRepository.findById(entry.getCategoryId()).orElse(null);
			
			String categoryName = category != null ? category.getName() : "Categoria não encontrada";
			
			return convertToResponse(entry, categoryName);
		})
		.collect(Collectors.toList());
	}
	
	@Transactional(readOnly = true)
	public List<EntryResponseDTO> getEntriesByDateRange(LocalDate startDate, LocalDate endDate) {
			
		List<Entry> entries = entryRepository.findByDateBetween(startDate, endDate);
		
		return entries.stream().map(entry -> {
			
			Category category = categoryRepository.findById(entry.getCategoryId()).orElse(null);
			
			String categoryName = category != null ? category.getName() : "Categoria não encontrada";
			
			return convertToResponse(entry, categoryName);
		})
		.collect(Collectors.toList());
	}
	
	public EntryResponseDTO updateEntry(Long id, EntryRequestDTO entryRequest) {
		
		Entry entry = entryRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Lançamento não encontrado com ID: " + id));
		
		// Verificar se a nova categoria existe
		Category category = categoryRepository.findById(entryRequest.categoryId()).orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada com ID: " + entryRequest.categoryId()));
		
		entry.setName(entryRequest.name());
		entry.setDescription(entryRequest.description());
		entry.setType(entryRequest.type());
		entry.setAmount(entryRequest.amount());
		entry.setDate(entryRequest.date());
		entry.setCategoryId(entryRequest.categoryId());
		
		if (entryRequest.paid() != null) {
			
			entry.setPaid(entryRequest.paid());
		}
		
		Entry updatedEntry = entryRepository.save(entry);
		
		return convertToResponse(updatedEntry, category.getName());
	}
	
	public EntryResponseDTO updatePaidStatus(Long id, Boolean paid) {
		
		Entry entry = entryRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Lançamento não encontrado com ID: " + id));
		entry.setPaid(paid);
		
		Entry updatedEntry = entryRepository.save(entry);
		
		Category category = categoryRepository.findById(entry.getCategoryId()).orElse(null);
		
		String categoryName = category != null ? category.getName() : "Categoria não encontrada";
		
		return convertToResponse(updatedEntry, categoryName);
	}
	
	public void deleteEntry(Long id) {
		
		Entry entry = entryRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Lançamento não encontrado com ID: " + id));
		
		entryRepository.delete(entry);
	}
	
	@Transactional(readOnly = true)
	public BigDecimal getTotalByType(String type) {
			
		Optional<BigDecimal> total = entryRepository.sumAmountByTypeAndPaid(type, true);
		
		return total.orElse(BigDecimal.ZERO);
	}
	
	@Transactional(readOnly = true)
	public BigDecimal getTotalByCategory(Long categoryId) {
		
		Optional<BigDecimal> total = entryRepository.sumAmountByCategoryAndPaid(categoryId, true);
		
		return total.orElse(BigDecimal.ZERO);
	}
	
	@Transactional(readOnly = true)
	public long getTotalCount() {
		
		return entryRepository.count();
	}
	
	private EntryResponseDTO convertToResponse(Entry entry, String categoryName) {
		return new EntryResponseDTO(
			entry.getId(),
			entry.getName(),
			entry.getDescription(),
			entry.getType(),
			entry.getAmount(),
			entry.getDate(),
			entry.getPaid(),
			entry.getCategoryId(),
			categoryName,
			entry.getCreatedAt(),
			entry.getUpdatedAt()
		);
	}
}
