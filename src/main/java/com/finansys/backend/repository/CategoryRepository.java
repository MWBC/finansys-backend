package com.finansys.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.finansys.backend.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {

	Optional<Category> findByName(String name);
	
	List<Category> findByNameContainingIgnoreCase(String name);
	
	boolean existsByName(String name);
	
	boolean existsByNameAndIdNot(String name, Long id);
	
	@Query("SELECT c FROM Category c WHERE c.name LIKE %:searchTerm% OR c.description LIKE %:searchTerm%")
	List<Category> findByNameOrDescriptionContaining(@Param("searchTerm") String searchTerm);

	@Query("SELECT c FROM Category c LEFT JOIN FETCH c.entries WHERE c.id = :id")
	Optional<Category> findByIdWithEntries(@Param("id") Long id);
	
	@Query("SELECT c FROM Category c WHERE SIZE(c.entries) > :minEntries")
	List<Category> findCategoriesWithMinimumEntries(@Param("minEntries") int minEntries);
	
	// Consulta nativa para estatísticas
	@Query(value = """
	 SELECT c.*, COUNT(e.id) as entry_count
	 FROM categories c
	 LEFT JOIN entries e ON c.id = e.category_id
	 GROUP BY c.id
	 ORDER BY entry_count DESC
	 """, nativeQuery = true)
	List<Object[]> findCategoriesWithEntryCount();
	
	// Consulta para categorias mais utilizadas
	@Query("""
	 SELECT c FROM Category c
	 WHERE c.id IN (
	 SELECT e.categoryId FROM Entry e
	 GROUP BY e.categoryId
	 ORDER BY COUNT(e.id) DESC
	 )
	 """)
	List<Category> findMostUsedCategories(Pageable pageable);
}
