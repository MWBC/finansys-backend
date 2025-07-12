package com.finansys.backend.entity;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users", 
uniqueConstraints = {
		
		@UniqueConstraint(columnNames = "name"), 
		@UniqueConstraint(columnNames = "email")
}, 
indexes = {
		
		@Index(name = "idx_user_name", columnList = "name"),
        @Index(name = "idx_user_email", columnList = "email"),
        @Index(name = "idx_user_enabled", columnList = "enabled")
})
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class User implements UserDetails {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@NotBlank(message = "Nome de usuário é obrigatório")
    @Size(min = 3, max = 50, message = "Nome de usuário deve ter entre 3 e 50 caracteres")
    @Column(name = "name", nullable = false, unique = true, length = 50)
    private String name;
	
	@NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ter formato válido")
    @Size(max = 100, message = "Email deve ter no máximo 100 caracteres")
    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;
    
	@NotBlank(message = "Senha é obrigatória")
    @Size(min = 60, max = 60, message = "Hash da senha deve ter 60 caracteres")
    @Column(name = "password", nullable = false, length = 60)
    private String password;
	
	@Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private Role role = Role.USER;

	@Column(name = "enabled", nullable = false)
    private Boolean enabled = true;
	
	@Column(name = "account_non_expired", nullable = false)
    private Boolean accountNonExpired = true;
    
    @Column(name = "account_non_locked", nullable = false)
    private Boolean accountNonLocked = true;

    @Column(name = "credentials_non_expired", nullable = false)
    private Boolean credentialsNonExpired = true;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;
    
    public User(String name, String email, String password) {
    	
    	this.name = name;
    	this.email = email;
    	this.password = password;
    }
    
    @PrePersist
    protected void onCreate() {
    	
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
    	
        this.updatedAt = LocalDateTime.now();
    }
    
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		
        return List.of(new SimpleGrantedAuthority(role.getAuthority()));

	}

	@Override
	public String getPassword() {
		
		return password;
	}

	@Override
	public String getUsername() {
		
		return email;
	}
	
	@Override
    public boolean isAccountNonExpired() {
		
        return accountNonExpired;
    }
    
    @Override
    public boolean isAccountNonLocked() {
    	
        return accountNonLocked;
    }
    
    @Override
    public boolean isCredentialsNonExpired() {
    	
        return credentialsNonExpired;
    }
    
    @Override
    public boolean isEnabled() {
    	
        return enabled;
    }
    
	// Enum para roles
    public enum Role {
        USER("ROLE_USER"),
        ADMIN("ROLE_ADMIN");
        
        private final String authority;
        
        Role(String authority) {
            this.authority = authority;
        }
        
        public String getAuthority() {
            return authority;
        }
    }
    
    public void updateLastLogin() {
    	
        this.lastLogin = LocalDateTime.now();
    }

}
