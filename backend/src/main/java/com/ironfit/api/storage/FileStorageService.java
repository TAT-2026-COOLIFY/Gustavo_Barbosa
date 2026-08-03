package com.ironfit.api.storage;

import org.springframework.web.multipart.MultipartFile;

/**
 * Abstração de armazenamento de arquivos (Strategy). A implementação local serve o
 * desenvolvimento; um adapter de object storage (S3, GCS) pode substituí-la depois
 * sem alterar nenhum service que dependa desta interface.
 */
public interface FileStorageService {

    /** Salva o arquivo e retorna a URL pública para acessá-lo. */
    String salvar(MultipartFile arquivo, String subpasta);
}
