package com.padelzgz.api.controller;

import com.padelzgz.api.dto.LoginRequestDTO;
import com.padelzgz.api.dto.LoginResponseDTO;
import com.padelzgz.api.exception.ErrorResponse;
import com.padelzgz.api.model.Usuario;
import com.padelzgz.api.repository.UsuarioRepository;
import com.padelzgz.api.security.JwtUtils;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        logger.info("POST /auth/login email={}", loginRequest.getEmail());
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()));

            UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getEmail());
            String token = jwtUtils.generateToken(userDetails.getUsername());

            logger.info("Login exitoso: {}", loginRequest.getEmail());
            return ResponseEntity.ok(new LoginResponseDTO(token, loginRequest.getEmail()));

        } catch (AuthenticationException e) {
            logger.error("Login fallido para {}: {}", loginRequest.getEmail(), e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ErrorResponse.generalError(401, "Email o contraseña incorrectos"));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication authentication) {
        logger.info("GET /auth/me email={}", authentication.getName());
        return usuarioRepository.findByEmail(authentication.getName())
                .map(u -> ResponseEntity.ok((Object) u))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
}