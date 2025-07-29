package pet.itotion.authService.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import pet.itotion.authService.dto.AuthResponseDTO;
import pet.itotion.authService.dto.AuthRequestDTO;
import pet.itotion.authService.dto.RegisterRequestDTO;
import pet.itotion.authService.service.AuthService;


@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> registerUser(
            @Valid @RequestBody RegisterRequestDTO registerRequest) {
        return ResponseEntity.ok(authService.register(registerRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> loginUser(
            @Valid @RequestBody AuthRequestDTO loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }
}