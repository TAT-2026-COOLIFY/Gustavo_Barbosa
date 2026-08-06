package com.ironfit.api.dashboard.service;

import com.ironfit.api.dashboard.dto.ConquistaDTO;
import com.ironfit.api.dashboard.dto.DashboardResponseDTO;
import com.ironfit.api.dashboard.dto.ResumoAguaDashboardDTO;
import com.ironfit.api.dashboard.dto.ResumoDietaDashboardDTO;
import com.ironfit.api.dashboard.dto.ResumoImcDTO;
import com.ironfit.api.dashboard.dto.ResumoMeditacaoDashboardDTO;
import com.ironfit.api.dashboard.dto.ResumoTreinoDashboardDTO;
import com.ironfit.api.dieta.dto.DiarioDiaResponseDTO;
import com.ironfit.api.dieta.service.DiarioService;
import com.ironfit.api.exception.ResourceNotFoundException;
import com.ironfit.api.hidratacao.dto.ResumoDiaHidratacaoResponseDTO;
import com.ironfit.api.hidratacao.dto.SequenciaHidratacaoResponseDTO;
import com.ironfit.api.hidratacao.service.HidratacaoService;
import com.ironfit.api.ia.entity.StatusRascunhoIa;
import com.ironfit.api.ia.repository.RascunhoIaRepository;
import com.ironfit.api.meditacao.service.ExecucaoMeditacaoService;
import com.ironfit.api.notificacao.service.NotificacaoService;
import com.ironfit.api.perfil.dto.ObjetivoResponseDTO;
import com.ironfit.api.perfil.service.ObjetivoService;
import com.ironfit.api.progresso.dto.RegistroProgressoResponseDTO;
import com.ironfit.api.progresso.service.ProgressoService;
import com.ironfit.api.treino.repository.SessaoTreinoRepository;
import com.ironfit.api.treino.service.ExecucaoTreinoService;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Agrega, em uma única chamada, o "resumo de hoje" de todos os módulos (IMC/peso, objetivo, água, treino,
 * dieta, meditação, notificações, rascunho de IA pendente) e computa conquistas sob demanda a partir desse
 * resumo. Não persiste nada — puramente um agregador de leitura sobre os services já existentes de cada módulo.
 */
@Service
public class DashboardService {

    private static final int CONSECUTIVOS_MINIMO_CONQUISTA = 3;
    private static final int PERCENTUAL_META_ATINGIDA = 100;
    private static final int DIAS_PROGRESSO_RECENTE = 7;

    private final ProgressoService progressoService;
    private final ObjetivoService objetivoService;
    private final HidratacaoService hidratacaoService;
    private final SessaoTreinoRepository sessaoTreinoRepository;
    private final ExecucaoTreinoService execucaoTreinoService;
    private final DiarioService diarioService;
    private final ExecucaoMeditacaoService execucaoMeditacaoService;
    private final NotificacaoService notificacaoService;
    private final RascunhoIaRepository rascunhoIaRepository;

    public DashboardService(
            ProgressoService progressoService,
            ObjetivoService objetivoService,
            HidratacaoService hidratacaoService,
            SessaoTreinoRepository sessaoTreinoRepository,
            ExecucaoTreinoService execucaoTreinoService,
            DiarioService diarioService,
            ExecucaoMeditacaoService execucaoMeditacaoService,
            NotificacaoService notificacaoService,
            RascunhoIaRepository rascunhoIaRepository) {
        this.progressoService = progressoService;
        this.objetivoService = objetivoService;
        this.hidratacaoService = hidratacaoService;
        this.sessaoTreinoRepository = sessaoTreinoRepository;
        this.execucaoTreinoService = execucaoTreinoService;
        this.diarioService = diarioService;
        this.execucaoMeditacaoService = execucaoMeditacaoService;
        this.notificacaoService = notificacaoService;
        this.rascunhoIaRepository = rascunhoIaRepository;
    }

    /**
     * Sem {@code @Transactional} de propósito: este método só orquestra chamadas a services que já
     * gerenciam sua própria transação (ex.: {@code HidratacaoService.obterResumoDia} cria a meta padrão
     * do usuário na primeira chamada, uma escrita real). Envolver tudo numa transação somente-leitura
     * aqui faria essa escrita falhar por herdar o modo read-only da transação externa.
     */
    public DashboardResponseDTO montar(Long usuarioId) {
        ResumoImcDTO imc = obterResumoImc(usuarioId);
        ObjetivoResponseDTO objetivoAtivo = obterObjetivoAtivo(usuarioId);
        ResumoAguaDashboardDTO agua = obterResumoAgua(usuarioId);
        ResumoTreinoDashboardDTO treino = obterResumoTreino(usuarioId);
        ResumoDietaDashboardDTO dieta = obterResumoDieta(usuarioId);
        ResumoMeditacaoDashboardDTO meditacao = obterResumoMeditacao(usuarioId);
        long notificacoesNaoLidas = notificacaoService.contarNaoLidas(usuarioId);
        boolean rascunhoIaPendente = rascunhoIaRepository.existsByUsuarioIdAndStatus(usuarioId, StatusRascunhoIa.PENDENTE);
        List<ConquistaDTO> conquistas = calcularConquistas(imc, agua, treino, meditacao);

        return new DashboardResponseDTO(
                imc, objetivoAtivo, agua, treino, dieta, meditacao, notificacoesNaoLidas, rascunhoIaPendente, conquistas);
    }

    private ResumoImcDTO obterResumoImc(Long usuarioId) {
        try {
            RegistroProgressoResponseDTO ultimo = progressoService.obterUltimo(usuarioId);
            return new ResumoImcDTO(ultimo.pesoKg(), ultimo.imc(), ultimo.massaMagraKg(), ultimo.dataRegistro());
        } catch (ResourceNotFoundException e) {
            return null;
        }
    }

    private ObjetivoResponseDTO obterObjetivoAtivo(Long usuarioId) {
        try {
            return objetivoService.obterAtivo(usuarioId);
        } catch (ResourceNotFoundException e) {
            return null;
        }
    }

    private ResumoAguaDashboardDTO obterResumoAgua(Long usuarioId) {
        ResumoDiaHidratacaoResponseDTO resumo = hidratacaoService.obterResumoDia(usuarioId, LocalDate.now());
        SequenciaHidratacaoResponseDTO sequencia = hidratacaoService.calcularSequencia(usuarioId);
        return new ResumoAguaDashboardDTO(resumo.metaMl(), resumo.totalMl(), resumo.percentual(), sequencia.diasConsecutivos());
    }

    private ResumoTreinoDashboardDTO obterResumoTreino(Long usuarioId) {
        ZoneId zona = ZoneId.systemDefault();
        Instant inicioDoDia = LocalDate.now().atStartOfDay(zona).toInstant();
        Instant fimDoDia = LocalDate.now().plusDays(1).atStartOfDay(zona).toInstant();
        boolean treinouHoje = sessaoTreinoRepository.existsByUsuarioIdAndConcluidoTrueAndDataExecucaoBetween(
                usuarioId, inicioDoDia, fimDoDia);
        return new ResumoTreinoDashboardDTO(treinouHoje, execucaoTreinoService.volumeSemanal(usuarioId).volumeKg());
    }

    private ResumoDietaDashboardDTO obterResumoDieta(Long usuarioId) {
        try {
            DiarioDiaResponseDTO dia = diarioService.obterDia(usuarioId, LocalDate.now());
            return new ResumoDietaDashboardDTO(
                    true,
                    dia.dietaNome(),
                    dia.metaCaloriasKcal(),
                    dia.caloriasConsumidas(),
                    dia.metaProteinaG(),
                    dia.proteinaConsumidaG(),
                    dia.metaCarboidratoG(),
                    dia.carboidratoConsumidoG(),
                    dia.metaGorduraG(),
                    dia.gorduraConsumidaG());
        } catch (ResourceNotFoundException e) {
            return new ResumoDietaDashboardDTO(false, null, null, null, null, null, null, null, null, null);
        }
    }

    private ResumoMeditacaoDashboardDTO obterResumoMeditacao(Long usuarioId) {
        return new ResumoMeditacaoDashboardDTO(
                execucaoMeditacaoService.sequencia(usuarioId).diasConsecutivos());
    }

    /** Regras avaliadas sob demanda a cada chamada, a partir do resumo já calculado — nada é persistido. */
    private List<ConquistaDTO> calcularConquistas(
            ResumoImcDTO imc, ResumoAguaDashboardDTO agua, ResumoTreinoDashboardDTO treino, ResumoMeditacaoDashboardDTO meditacao) {
        List<ConquistaDTO> conquistas = new ArrayList<>();

        if (agua.diasConsecutivos() != null && agua.diasConsecutivos() >= CONSECUTIVOS_MINIMO_CONQUISTA) {
            conquistas.add(new ConquistaDTO(
                    "Hidratação em dia", agua.diasConsecutivos() + " dias seguidos batendo a meta de água"));
        }
        if (meditacao.diasConsecutivos() != null && meditacao.diasConsecutivos() >= CONSECUTIVOS_MINIMO_CONQUISTA) {
            conquistas.add(new ConquistaDTO("Mente tranquila", meditacao.diasConsecutivos() + " dias seguidos de meditação"));
        }
        if (treino.treinouHoje()) {
            conquistas.add(new ConquistaDTO("Treino do dia", "Você já treinou hoje. Continue assim!"));
        }
        if (agua.percentual() != null && agua.percentual() >= PERCENTUAL_META_ATINGIDA) {
            conquistas.add(new ConquistaDTO("Meta de água batida", "Você já bateu sua meta de água hoje"));
        }
        if (imc != null
                && imc.dataRegistro() != null
                && !imc.dataRegistro().isBefore(LocalDate.now().minusDays(DIAS_PROGRESSO_RECENTE))) {
            conquistas.add(new ConquistaDTO("Progresso em dia", "Você registrou seu peso nos últimos 7 dias"));
        }

        return conquistas;
    }
}
