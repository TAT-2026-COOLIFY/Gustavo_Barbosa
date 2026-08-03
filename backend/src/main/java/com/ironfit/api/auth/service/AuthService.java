package com.ironfit.api.auth.service;

import com.ironfit.api.auth.dto.AlterarSenhaRequestDTO;
import com.ironfit.api.auth.dto.CadastroRequestDTO;
import com.ironfit.api.auth.dto.LoginRequestDTO;
import com.ironfit.api.auth.dto.LoginResponseDTO;
import com.ironfit.api.auth.entity.Usuario;
import com.ironfit.api.auth.mapper.UsuarioMapper;
import com.ironfit.api.auth.repository.UsuarioRepository;
import com.ironfit.api.exception.BusinessException;
import com.ironfit.api.exception.ResourceNotFoundException;
import com.ironfit.api.exception.UnauthorizedException;
import com.ironfit.api.security.JwtProperties;
import com.ironfit.api.security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtProperties jwtProperties;
    private final RefreshTokenService refreshTokenService;
    private final UsuarioMapper usuarioMapper;

    public AuthService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtTokenProvider jwtTokenProvider,
            JwtProperties jwtProperties,
            RefreshTokenService refreshTokenService,
            UsuarioMapper usuarioMapper) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.jwtProperties = jwtProperties;
        this.refreshTokenService = refreshTokenService;
        this.usuarioMapper = usuarioMapper;
    }

    @Transactional
    public LoginResponseDTO cadastrar(CadastroRequestDTO request) {
        if (usuarioRepository.existsByEmailIgnoreCase(request.email())) {
            throw new BusinessException("E-mail já cadastrado");
        }

        Usuario usuario = Usuario.builder()
                .nome(request.nome())
                .email(request.email())
                .senhaHash(passwordEncoder.encode(request.senha()))
                .build();

        usuarioRepository.save(usuario);
        return gerarLoginResponse(usuario);
    }

    public LoginResponseDTO login(LoginRequestDTO request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.senha()));
        } catch (org.springframework.security.core.AuthenticationException ex) {
            throw new BadCredentialsException("E-mail ou senha inválidos");
        }

        Usuario usuario = usuarioRepository
                .findByEmailIgnoreCase(request.email())
                .orElseThrow(() -> new BadCredentialsException("E-mail ou senha inválidos"));

        return gerarLoginResponse(usuario);
    }

    @Transactional
    public LoginResponseDTO refresh(String refreshTokenBruto) {
        RefreshTokenService.RotacaoResultado resultado = refreshTokenService.validarERotacionar(refreshTokenBruto);
        return montarResponse(resultado.usuario(), resultado.novoRefreshToken());
    }

    public void logout(String refreshTokenBruto) {
        refreshTokenService.revogar(refreshTokenBruto);
    }

    @Transactional
    public void alterarSenha(Long usuarioId, AlterarSenhaRequestDTO request) {
        Usuario usuario = usuarioRepository
                .findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        if (!passwordEncoder.matches(request.senhaAtual(), usuario.getSenhaHash())) {
            throw new UnauthorizedException("Senha atual incorreta");
        }

        usuario.setSenhaHash(passwordEncoder.encode(request.novaSenha()));
        usuarioRepository.save(usuario);
        refreshTokenService.revogarTodosDoUsuario(usuarioId);
    }

    private LoginResponseDTO gerarLoginResponse(Usuario usuario) {
        String refreshToken = refreshTokenService.gerar(usuario);
        return montarResponse(usuario, refreshToken);
    }

    private LoginResponseDTO montarResponse(Usuario usuario, String refreshToken) {
        String accessToken = jwtTokenProvider.gerarAccessToken(usuario.getId(), usuario.getEmail());
        long expiresInSeconds = jwtProperties.getAccessTokenTtlMinutes() * 60;
        return new LoginResponseDTO(accessToken, refreshToken, expiresInSeconds, usuarioMapper.toResponseDTO(usuario));
    }
}
