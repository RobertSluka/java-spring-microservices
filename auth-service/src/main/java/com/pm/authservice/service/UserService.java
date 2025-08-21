package com.pm.authservice.service;

import com.pm.authservice.dto.LoginRequestDTO;
import com.pm.authservice.dto.LoginResponseDTO;
import com.pm.authservice.model.Role;
import com.pm.authservice.model.User;
import com.pm.authservice.repository.UserRepository;
import java.util.Optional;

import com.pm.authservice.util.JwtUtil;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtUtil jwtUtil;

  public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtUtil = jwtUtil;
  }
  
  public Optional<User> findByEmail(String email) {
    return userRepository.findByEmail(email);
  }


  @Transactional
  public LoginResponseDTO register(LoginRequestDTO dto) {

    if (userRepository.existsByEmailIgnoreCase(dto.getEmail())) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
    }

    User u = new User();
    u.setEmail(dto.getEmail().trim().toLowerCase());
    u.setPassword(passwordEncoder.encode(dto.getPassword()));
    u.setRole(Role.USER);
    userRepository.save(u);


    String token = jwtUtil.generateToken(u.getEmail(), u.getRole());

    return new LoginResponseDTO(token);
  }
}
