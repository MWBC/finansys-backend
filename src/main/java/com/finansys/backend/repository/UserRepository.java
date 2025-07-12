package com.finansys.backend.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.finansys.backend.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByName(String name);
	    
    Optional<User> findByEmail(String email);
    
    Optional<User> findByNameOrEmail(String name, String email);
    
    boolean existsByName(String name);
    
    boolean existsByEmail(String email);
    
    boolean existsByNameAndIdNot(String name, Long id);
    
    boolean existsByEmailAndIdNot(String email, Long id);
    
    @Modifying
    @Query("UPDATE User u SET u.lastLogin = :lastLogin WHERE u.id = :userId")
    void updateLastLogin(@Param("userId") Long userId, @Param("lastLogin") LocalDateTime lastLogin);
    
    @Modifying
    @Query("UPDATE User u SET u.enabled = :enabled WHERE u.id = :userId")
    void updateUserStatus(@Param("userId") Long userId, @Param("enabled") Boolean enabled);
    
    @Query("SELECT u FROM User u WHERE u.enabled = true")
    List<User> findAllActiveUsers();
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.enabled = true")
    long countActiveUsers();
}
