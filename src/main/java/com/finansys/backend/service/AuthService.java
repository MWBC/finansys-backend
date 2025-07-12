package com.finansys.backend.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.finansys.backend.dto.request.LoginRequestDTO;
import com.finansys.backend.dto.request.RegisterRequestDTO;
import com.finansys.backend.dto.response.JwtResponseDTO;
import com.finansys.backend.dto.response.MessageResponseDTO;
import com.finansys.backend.entity.User;
import com.finansys.backend.repository.UserRepository;


@Service
@Transactional
public class AuthService {

	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private JwtService jwtService;
	
	public JwtResponseDTO authenticateUser(LoginRequestDTO loginRequestDTO) {
		
        Authentication authentication = authenticationManager.authenticate(
        		
            new UsernamePasswordAuthenticationToken(
                loginRequestDTO.email(),
                loginRequestDTO.password()
            )
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        User user = (User) authentication.getPrincipal();
        
        String jwt = jwtService.generateTokenWithUserInfo(user, user.getId(), user.getEmail());
        
        user.updateLastLogin();
        
        userRepository.save(user);
        
        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(jwtService.getExpirationTime() / 1000);
        
        return new JwtResponseDTO(
            jwt,
            "Bearer", 
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getRole().name(),
            expiresAt
        );
    }
	
	public MessageResponseDTO registerUser(RegisterRequestDTO registerRequestDTO) {
        
        if (userRepository.existsByEmail(registerRequestDTO.email())) {
        	
            return new MessageResponseDTO("Erro: Email já está em uso!", false, LocalDateTime.now());
        }
        
        User user = new User(
            registerRequestDTO.name(),
            registerRequestDTO.email(),
            passwordEncoder.encode(registerRequestDTO.password())
        );
        
        userRepository.save(user);
        
        return new MessageResponseDTO("Usuário registrado com sucesso!", true, LocalDateTime.now());
    }
	
	@Transactional(readOnly = true)
    public User getCurrentUser() {
		
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.getPrincipal() instanceof User) {
        	
            return (User) authentication.getPrincipal();
        }
        
        throw new RuntimeException("Usuário não autenticado");
    }
    
    @Transactional(readOnly = true)
    public boolean isEmailAvailable(String email) {
    	
        return !userRepository.existsByEmail(email);
    }
}
