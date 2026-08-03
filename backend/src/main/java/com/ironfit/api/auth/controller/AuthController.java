package com.ironfit.api.auth.controller;

import com.ironfit.api.auth.dto.AlterarSenhaRequestDTO;
import com.ironfit.api.auth.dto.CadastroRequestDTO;
import com.ironfit.api.auth.dto.EsqueciSenhaRequestDTO;
import com.ironfit.api.auth.dto.LoginRequestDTO;
import com.ironfit.api.auth.dto.LoginResponseDTO;
import com.ironfit.api.auth.dto.LogoutRequestDTO;
import com.ironfit.api.auth.dto.RedefinirSenhaRequestDTO;
import com.ironfit.api.auth.dto.RefreshRequestDTO;
import com.ironfit.api.auth.service.AuthService;
import com.ironfit.api.auth.service.PasswordResetService;
import com.ironfit.api.security.UsuarioUserDetails;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Autenticação")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final PasswordResetService passwordResetService;

    public AuthController(AuthService authService, PasswordResetService passwordResetService) {
        this.authService = authService;
        this.passwordResetService = passwordResetService;
    }

    @PostMapping("/cadastro")
    @ResponseStatus(HttpStatus.CREATED)
    public LoginResponseDTO cadastrar(@Valid @RequestBody CadastroRequestDTO request) {
        return authService.cadastrar(request);
    }

    @PostMapping("/login")
    public LoginResponseDTO login(@Valid @RequestBody LoginRequestDTO request) {
        return authService.login(request);
    }

    @PostMapping("/refresh")
    public LoginResponseDTO refresh(@Valid @RequestBody RefreshRequestDTO request) {
        return authService.refresh(request.refreshToken());
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@Valid @RequestBody LogoutRequestDTO request) {
        authService.logout(request.refreshToken());
    }

    @PostMapping("/esqueci-senha")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void esqueciSenha(@Valid @RequestBody EsqueciSenhaRequestDTO request) {
        passwordResetService.solicitarRedefinicao(request.email());
    }

    @PostMapping("/redefinir-senha")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void redefinirSenha(@Valid @RequestBody RedefinirSenhaRequestDTO request) {
        passwordResetService.redefinir(request.token(), request.novaSenha());
    }

    @PutMapping("/alterar-senha")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @SecurityRequirement(name = "bearerAuth")
    public void alterarSenha(
            @AuthenticationPrincipal UsuarioUserDetails userDetails, @Valid @RequestBody AlterarSenhaRequestDTO request) {
        authService.alterarSenha(userDetails.getUsuarioId(), request);
    }
}
