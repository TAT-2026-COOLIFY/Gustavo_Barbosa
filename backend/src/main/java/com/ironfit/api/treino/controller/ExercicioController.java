package com.ironfit.api.treino.controller;

import com.ironfit.api.treino.dto.ExercicioResponseDTO;
import com.ironfit.api.treino.entity.GrupoMuscular;
import com.ironfit.api.treino.service.ExercicioService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Exercícios")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/exercicios")
public class ExercicioController {

    private final ExercicioService exercicioService;

    public ExercicioController(ExercicioService exercicioService) {
        this.exercicioService = exercicioService;
    }

    @GetMapping
    public List<ExercicioResponseDTO> listar(@RequestParam(required = false) GrupoMuscular grupoMuscular) {
        return exercicioService.listar(grupoMuscular);
    }
}
