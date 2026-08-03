package com.ironfit.api.storage;

import com.ironfit.api.exception.BusinessException;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class LocalFileStorageService implements FileStorageService {

    private static final Map<String, String> EXTENSOES_PERMITIDAS =
            Map.of("image/jpeg", ".jpg", "image/png", ".png", "image/webp", ".webp");

    private final StorageProperties storageProperties;

    public LocalFileStorageService(StorageProperties storageProperties) {
        this.storageProperties = storageProperties;
    }

    @Override
    public String salvar(MultipartFile arquivo, String subpasta) {
        String extensao = EXTENSOES_PERMITIDAS.get(arquivo.getContentType());
        if (extensao == null) {
            throw new BusinessException("Formato de imagem não suportado. Use JPEG, PNG ou WEBP.");
        }

        try {
            Path diretorio = Path.of(storageProperties.getUploadDir(), subpasta);
            Files.createDirectories(diretorio);

            String nomeArquivo = UUID.randomUUID() + extensao;
            Path destino = diretorio.resolve(nomeArquivo);
            arquivo.transferTo(destino);

            return "/uploads/" + subpasta + "/" + nomeArquivo;
        } catch (IOException ex) {
            throw new UncheckedIOException("Falha ao salvar arquivo", ex);
        }
    }
}
