package com.app.beleza.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    // Usado quando o arquivo chega direto do formulário
    public String upload(MultipartFile file, String pasta) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Arquivo vazio");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("O arquivo precisa ser uma imagem");
        }
        return upload(file.getBytes(), pasta);
    }

    // Usado quando a imagem estava guardada na sessão (preview)
    public String upload(byte[] bytes, String pasta) throws IOException {
        if (bytes == null || bytes.length == 0) {
            throw new IllegalArgumentException("Arquivo vazio");
        }
        Map<?, ?> resultado = cloudinary.uploader().upload(
                bytes,
                ObjectUtils.asMap(
                        "folder", pasta,
                        "resource_type", "image"
                )
        );
        return (String) resultado.get("secure_url");
    }

    public void deletar(String publicId) throws IOException {
        cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    }
}