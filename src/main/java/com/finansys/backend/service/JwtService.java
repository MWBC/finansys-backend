package com.finansys.backend.service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;

@Service
public class JwtService {

    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);
    
    @Value("${jwt.secret}")
    private String jwtSecret;
    
    @Value("${jwt.expiration}")
    private long jwtExpirationMs;
    
    public String generateToken(UserDetails userDetails) {
    	
        Map<String, Object> claims = new HashMap<>();
        
        return createToken(claims, userDetails.getUsername());
    }
    
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
    	
    	return createToken(extraClaims, userDetails.getUsername());
    }
    
    private String createToken(Map<String, Object> claims, String subject) {
    	
        Date now = new Date();
        
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);
        
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    
    public String extractUsername(String token) {
    	
        return extractClaim(token, Claims::getSubject);
    }
    
    public Date extractExpiration(String token) {
    	
        return extractClaim(token, Claims::getExpiration);
    }
    
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    	
        final Claims claims = extractAllClaims(token);
        
        return claimsResolver.apply(claims);
    }
    
    private Claims extractAllClaims(String token) {
    	
        try {
        	
            return Jwts.parserBuilder()
                    .setSigningKey(getSignInKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
        	
            logger.error("Token JWT expirado: {}", e.getMessage());
            
            throw e;
        } catch (UnsupportedJwtException e) {
        	
            logger.error("Token JWT não suportado: {}", e.getMessage());
            
            throw e;
        } catch (MalformedJwtException e) {
            
        	logger.error("Token JWT malformado: {}", e.getMessage());
            
        	throw e;
        } catch (SignatureException e) {
            
        	logger.error("Assinatura JWT inválida: {}", e.getMessage());
            
        	throw e;
        } catch (IllegalArgumentException e) {
            
        	logger.error("Token JWT vazio: {}", e.getMessage());
            
        	throw e;
        }
    }
    
    public Boolean isTokenExpired(String token) {
    	
        return extractExpiration(token).before(new Date());
    }
    
    public Boolean validateToken(String token, UserDetails userDetails) {
    	
        try {
        	
            final String username = extractUsername(token);
            
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (JwtException | IllegalArgumentException e) {
        	
            logger.error("Erro na validação do token JWT: {}", e.getMessage());
            
            return false;
        }
    }
    
    private Key getSignInKey() {
    	
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        
        return Keys.hmacShaKeyFor(keyBytes);
    }
    
    public long getExpirationTime() {
    	
        return jwtExpirationMs;
    }
    
    public boolean isTokenValid(String token) {
    	
        try {
        	
            Jwts.parserBuilder()
                    .setSigningKey(getSignInKey())
                    .build()
                    .parseClaimsJws(token);
            
            return true;
        } catch (JwtException | IllegalArgumentException e) {
        	
            return false;
        }
    }
    
    public Long extractUserId(String token) {
    	
        Claims claims = extractAllClaims(token);
        
        Object userIdClaim = claims.get("userId");
        
        if (userIdClaim != null) {
        	
            return Long.valueOf(userIdClaim.toString());
        }
        
        return null;
    }
    
    public String generateTokenWithUserInfo(UserDetails userDetails, Long userId, String email) {
    	
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("email", email);
        claims.put("authorities", userDetails.getAuthorities());
        
        return createToken(claims, userDetails.getUsername());
    }
}
