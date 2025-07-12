package com.finansys.backend.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.finansys.backend.entity.User;
import com.finansys.backend.repository.UserRepository;

@Service
public class UserDetailServiceImpl implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;
	
	@Override
	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

		User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                    "Usuário não encontrado com username ou email: " + email));
        
        return user;
	}

	@Transactional(readOnly = true)
    public UserDetails loadUserById(Long id) {
		
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException(
                    "Usuário não encontrado com ID: " + id));
        
        return user;
    }
}
