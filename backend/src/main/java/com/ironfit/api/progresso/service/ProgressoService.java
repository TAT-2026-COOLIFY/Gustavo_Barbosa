package com.ironfit.api.progresso.service;

import com.ironfit.api.auth.entity.Usuario;
import com.ironfit.api.auth.repository.UsuarioRepository;
import com.ironfit.api.exception.BusinessException;
import com.ironfit.api.exception.ResourceNotFoundException;
import com.ironfit.api.perfil.repository.PerfilRepository;
import com.ironfit.api.progresso.dto.RegistrarProgressoRequestDTO;
import com.ironfit.api.progresso.dto.RegistroProgressoResponseDTO;
import com.ironfit.api.progresso.entity.RegistroProgresso;
import com.ironfit.api.progresso.mapper.RegistroProgressoMapper;
import com.ironfit.api.progresso.repository.RegistroProgressoRepository;
import com.ironfit.api.storage.FileStorageService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/** Progresso físico: histórico de peso/medidas/fotos de evolução, com IMC e massa magra calculados a partir do Perfil. */
@Service
public class ProgressoService {

    private static final String SUBPASTA_FOTOS_PROGRESSO = "progresso";

    private final RegistroProgressoRepository registroProgressoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PerfilRepository perfilRepository;
    private final RegistroProgressoMapper registroProgressoMapper;
    private final FileStorageService fileStorageService;

    public ProgressoService(
            RegistroProgressoRepository registroProgressoRepository,
            UsuarioRepository usuarioRepository,
            PerfilRepository perfilRepository,
            RegistroProgressoMapper registroProgressoMapper,
            FileStorageService fileStorageService) {
        this.registroProgressoRepository = registroProgressoRepository;
        this.usuarioRepository = usuarioRepository;
        this.perfilRepository = perfilRepository;
        this.registroProgressoMapper = registroProgressoMapper;
        this.fileStorageService = fileStorageService;
    }

    @Transactional
    public RegistroProgressoResponseDTO registrar(Long usuarioId, RegistrarProgressoRequestDTO request) {
        LocalDate data = request.dataRegistro() != null ? request.dataRegistro() : LocalDate.now();

        RegistroProgresso registro = registroProgressoRepository
                .findByUsuarioIdAndDataRegistro(usuarioId, data)
                .orElseGet(() -> {
                    Usuario usuario = usuarioRepository
                            .findById(usuarioId)
                            .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
                    return RegistroProgresso.builder().usuario(usuario).dataRegistro(data).build();
                });

        preencherCampos(registro, request);
        registroProgressoRepository.save(registro);
        return mapear(usuarioId, registro);
    }

    @Transactional(readOnly = true)
    public List<RegistroProgressoResponseDTO> listar(Long usuarioId) {
        BigDecimal alturaCm = obterAlturaCm(usuarioId);
        return registroProgressoRepository.findByUsuarioIdOrderByDataRegistroDesc(usuarioId).stream()
                .map(registro -> registroProgressoMapper.toResponseDTO(registro, alturaCm))
                .toList();
    }

    @Transactional(readOnly = true)
    public RegistroProgressoResponseDTO obterUltimo(Long usuarioId) {
        RegistroProgresso registro = registroProgressoRepository
                .findFirstByUsuarioIdOrderByDataRegistroDesc(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Nenhum registro de progresso encontrado"));
        return mapear(usuarioId, registro);
    }

    @Transactional
    public RegistroProgressoResponseDTO atualizar(Long usuarioId, Long registroId, RegistrarProgressoRequestDTO request) {
        RegistroProgresso registro = buscarDoUsuario(usuarioId, registroId);

        LocalDate novaData = request.dataRegistro() != null ? request.dataRegistro() : registro.getDataRegistro();
        if (!novaData.equals(registro.getDataRegistro())
                && registroProgressoRepository.findByUsuarioIdAndDataRegistro(usuarioId, novaData).isPresent()) {
            throw new BusinessException("Já existe um registro de progresso nesta data");
        }
        registro.setDataRegistro(novaData);

        preencherCampos(registro, request);
        registroProgressoRepository.save(registro);
        return mapear(usuarioId, registro);
    }

    @Transactional
    public void removerRegistro(Long usuarioId, Long registroId) {
        registroProgressoRepository.delete(buscarDoUsuario(usuarioId, registroId));
    }

    @Transactional
    public String atualizarFoto(Long usuarioId, Long registroId, MultipartFile arquivo) {
        RegistroProgresso registro = buscarDoUsuario(usuarioId, registroId);
        String url = fileStorageService.salvar(arquivo, SUBPASTA_FOTOS_PROGRESSO);
        registro.setFotoUrl(url);
        registroProgressoRepository.save(registro);
        return url;
    }

    private void preencherCampos(RegistroProgresso registro, RegistrarProgressoRequestDTO request) {
        registro.setPesoKg(request.pesoKg());
        registro.setPercentualGordura(request.percentualGordura());
        registro.setCircunferenciaCinturaCm(request.circunferenciaCinturaCm());
        registro.setCircunferenciaQuadrilCm(request.circunferenciaQuadrilCm());
        registro.setCircunferenciaBracoCm(request.circunferenciaBracoCm());
        registro.setCircunferenciaCoxaCm(request.circunferenciaCoxaCm());
        registro.setCircunferenciaPeitoCm(request.circunferenciaPeitoCm());
        registro.setObservacoes(request.observacoes());
    }

    private RegistroProgresso buscarDoUsuario(Long usuarioId, Long registroId) {
        return registroProgressoRepository
                .findByIdAndUsuarioId(registroId, usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Registro de progresso não encontrado"));
    }

    private RegistroProgressoResponseDTO mapear(Long usuarioId, RegistroProgresso registro) {
        return registroProgressoMapper.toResponseDTO(registro, obterAlturaCm(usuarioId));
    }

    private BigDecimal obterAlturaCm(Long usuarioId) {
        return perfilRepository
                .findByUsuarioId(usuarioId)
                .map(perfil -> perfil.getAlturaCm())
                .orElse(null);
    }
}
