package com.finansys.backend.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

//	@Value("${cors.allowed-origins}")
//    private List<String> allowedOrigins;
//    
//    @Value("${cors.allowed-methods}")
//    private List<String> allowedMethods;
//    
//    @Value("${cors.allowed-headers}")
//    private List<String> allowedHeaders;
//    
//    @Value("${cors.allow-credentials}")
//    private boolean allowCredentials;
    
	@Autowired
	CorsProperties corsProperties;
	
    @Override
    public void addCorsMappings(CorsRegistry registry) {
    	
        registry.addMapping("/api/**")
                .allowedOriginPatterns(corsProperties.getAllowedOrigins().toArray(new String[0]))
                .allowedMethods(corsProperties.getAllowedMethods().toArray(new String[0]))
                .allowedHeaders(corsProperties.getAllowedHeaders().toArray(new String[0]))
                .allowCredentials(corsProperties.isAllowCredentials())
                .maxAge(3600); // Cache preflight por 1 hora
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Configurar origens permitidas
        configuration.setAllowedOriginPatterns(corsProperties.getAllowedOrigins());
        
        // Configurar métodos HTTP permitidos
        configuration.setAllowedMethods(corsProperties.getAllowedMethods());
        
        // Configurar cabeçalhos permitidos
        configuration.setAllowedHeaders(corsProperties.getAllowedHeaders());
        
        // Permitir credenciais (cookies, headers de autorização)
        configuration.setAllowCredentials(corsProperties.isAllowCredentials());
        
        // Cabeçalhos expostos para o cliente
        configuration.setExposedHeaders(Arrays.asList(
            "Authorization",
            "Content-Type",
            "X-Total-Count",
            "X-Total-Pages"
        ));
        
        // Cache de preflight requests
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}
