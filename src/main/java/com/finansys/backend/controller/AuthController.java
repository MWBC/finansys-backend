package com.finansys.backend.controller;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.finansys.backend.dto.request.LoginRequestDTO;
import com.finansys.backend.dto.request.RegisterRequestDTO;
import com.finansys.backend.dto.response.JwtResponseDTO;
import com.finansys.backend.dto.response.MessageResponseDTO;
import com.finansys.backend.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("auth")
@Tag(name = "Autenticação", description = "Endpoints para autenticação e registro de usuário")
public class AuthController {

	@Autowired
	private AuthService authService;
	
	@PostMapping("/login")
    @Operation(summary = "Realizar login", description = "Autentica usuário e retorna token JWT")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login realizado com sucesso"),
        @ApiResponse(responseCode = "401", description = "Credenciais inválidas"),
        @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos")
    })
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequestDTO loginRequest) {
		
        try {
        	
            JwtResponseDTO jwtResponse = authService.authenticateUser(loginRequest);
            
            
            ResponseCookie cookie = ResponseCookie.from("token", jwtResponse.token())
            	    .httpOnly(true)
            	    .secure(true)
            	    .path("/")
            	    .maxAge(Duration.ofHours(1))
            	    .sameSite("None")
            	    .build();

            	return ResponseEntity.ok()
            	    .header(HttpHeaders.SET_COOKIE, cookie.toString())
            	    .body(jwtResponse);

//            return ResponseEntity.ok(jwtResponse);
        } catch (Exception e) {
        	
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponseDTO("Credenciais inválidas: " + e.getMessage(), false, LocalDateTime.now()));
        }
    }
	
	@PostMapping("/register")
    @Operation(summary = "Registrar usuário", description = "Cria uma nova conta de usuário")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Usuário registrado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos ou usuário já existe")
    })
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequestDTO registerRequestDTO) {
		
        try {
        	
            MessageResponseDTO messageResponseDTO = authService.registerUser(registerRequestDTO);
            
            if (messageResponseDTO.success()) {
            	
                return ResponseEntity.status(HttpStatus.CREATED).body(messageResponseDTO);
            } else {
            	
                return ResponseEntity.badRequest().body(messageResponseDTO);
            }
        } catch (Exception e) {
        	
            return ResponseEntity.badRequest().body(new MessageResponseDTO("Erro no registro: " + e.getMessage(), false, LocalDateTime.now()));
        }
    }
	
	@GetMapping("/check-email/{email}")
    @Operation(summary = "Verificar disponibilidade de email", 
               description = "Verifica se um email está disponível para uso")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Verificação realizada com sucesso")
    })
    public ResponseEntity<MessageResponseDTO> checkEmailAvailability(@PathVariable String email) {
		
        boolean isAvailable = authService.isEmailAvailable(email);
        
        String message = isAvailable ? "Email disponível" : "Email já está em uso";
        
        return ResponseEntity.ok(new MessageResponseDTO(message, isAvailable, LocalDateTime.now()));
    }
	
	@GetMapping("/me")
    @Operation(summary = "Obter informações do usuário atual", 
               description = "Retorna informações do usuário autenticado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Informações obtidas com sucesso"),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado")
    })
    public ResponseEntity<?> getCurrentUser() {
		
        try {
        	
            var currentUser = authService.getCurrentUser();
            
            return ResponseEntity.ok(currentUser);
        } catch (Exception e) {
        	
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponseDTO("Usuário não autenticado", false, LocalDateTime.now()));
        }
    }
	
	@PostMapping("/logout")
	@Operation(summary = "Realizar logout do usuário atual", 
    description = "Expira o cookie do token jwt do usuário")
	@ApiResponses(value = {
	@ApiResponse(responseCode = "200", description = "Logout realizado com sucesso"),
	@ApiResponse(responseCode = "401", description = "Usuário não autenticado")
})
	public ResponseEntity<?> logout() {
		
        ResponseCookie cookie = ResponseCookie.from("token", null)
        	    .httpOnly(true)
        	    .secure(true)
        	    .path("/")
        	    .maxAge(Duration.ofHours(0))
        	    .sameSite("None")
        	    .build();

        	return ResponseEntity.ok()
        	    .header(HttpHeaders.SET_COOKIE, cookie.toString())
        	    .body(new MessageResponseDTO("Logout realizado com sucesso.", true, LocalDateTime.now()));
	}
}
