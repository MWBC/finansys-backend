package com.finansys.backend.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.finansys.backend.entity.Entry;

public interface EntryRepository extends JpaRepository<Entry, Long>{

	List<Entry> findByCategoryId(Long categoryId);
	
	Page<Entry> findByCategoryId(Long categoryId, Pageable pageable);
	
	List<Entry> findByType(String type);
	
	Page<Entry> findByType(String type, Pageable pageable);
	
	List<Entry> findByPaid(Boolean paid);
	
	Page<Entry> findByPaid(Boolean paid, Pageable pageable);
	
	List<Entry> findByDateBetween(LocalDate startDate, LocalDate endDate);
	
	Page<Entry> findByDateBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);
	
	List<Entry> findByTypeAndPaid(String type, Boolean paid);
	
	List<Entry> findByCategoryIdAndDateBetween(Long categoryId, LocalDate startDate, LocalDate endDate);
	
	List<Entry> findByTypeAndDateBetween(String type, LocalDate startDate, LocalDate endDate);
	
	List<Entry> findByOrderByDateDesc();
	
	List<Entry> findByCategoryIdOrderByDateDesc(Long categoryId);
	
	@Query("SELECT SUM(e.amount) FROM Entry e WHERE e.type = :type AND e.paid = true")
	Optional<BigDecimal> sumAmountByTypeAndPaid(@Param("type") String type);
	
	@Query("SELECT SUM(e.amount) FROM Entry e WHERE e.categoryId = :categoryId AND e.paid = true")
	Optional<BigDecimal> sumAmountByCategoryAndPaid(@Param("categoryId") Long categoryId);
	
	@Query("""
			 SELECT SUM(e.amount) FROM Entry e
			 WHERE e.type = :type
			 AND e.date BETWEEN :startDate AND :endDate
			 AND e.paid = true
			 """)
	Optional<BigDecimal> sumAmountByTypeAndDateRangeAndPaid(
	@Param("type") String type, @Param("startDate") LocalDate startDate,
	@Param("endDate") LocalDate endDate);
	
	// Consultas para dashboard
	@Query("""
	 SELECT e.type, SUM(e.amount)
	 FROM Entry e
	 WHERE e.date BETWEEN :startDate AND :endDate
	 AND e.paid = true
	 GROUP BY e.type
	 """)
	List<Object[]> sumAmountByTypeInPeriod(
	@Param("startDate") LocalDate startDate,
	@Param("endDate") LocalDate endDate);
	
	@Query("""
			 SELECT c.name, SUM(e.amount)
			 FROM Entry e
			 JOIN e.category c
			 WHERE e.date BETWEEN :startDate AND :endDate
			 AND e.paid = true
			 GROUP BY c.name
			 ORDER BY SUM(e.amount) DESC
			 """)
	List<Object[]> sumAmountByCategoryInPeriod(
	@Param("startDate") LocalDate startDate,
	@Param("endDate") LocalDate endDate);
	
	// Consultas para análise financeira
	@Query("""
	 SELECT DATE(e.date), SUM(CASE WHEN e.type = 'RECEITA' THEN e.amount ELSE
	0 END) as receitas,
	 SUM(CASE WHEN e.type = 'DESPESA' THEN e.amount ELSE 0 END) as
	despesas
	 FROM Entry e
	 WHERE e.date BETWEEN :startDate AND :endDate
	 AND e.paid = true
	 GROUP BY DATE(e.date)
	 ORDER BY DATE(e.date)
	 """)
	List<Object[]> getDailyFinancialSummary(
	@Param("startDate") LocalDate startDate,
	@Param("endDate") LocalDate endDate);
	
	// Consulta para entradas em atraso
	@Query("SELECT e FROM Entry e WHERE e.paid = false AND e.date < :currentDate")
	List<Entry> findOverdueEntries(@Param("currentDate") LocalDate currentDate);
	
	// Consulta para próximos vencimentos
	@Query("""
	 SELECT e FROM Entry e
	 WHERE e.paid = false
	 AND e.date BETWEEN :startDate AND :endDate
	 ORDER BY e.date ASC
	 """)
	List<Entry> findUpcomingEntries(
	@Param("startDate") LocalDate startDate,
	@Param("endDate") LocalDate endDate);
}
