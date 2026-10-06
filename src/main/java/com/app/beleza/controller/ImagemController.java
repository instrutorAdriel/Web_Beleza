package com.app.beleza.controller;

import com.app.beleza.service.CloudinaryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/imagens")
public class ImagemController {

    private static final Set<String> PASTAS_PERMITIDAS = Set.of("produto", "depoimento");

    private final CloudinaryService cloudinaryService;

    public ImagemController(CloudinaryService cloudinaryService) {
        this.cloudinaryService = cloudinaryService;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file,
                                    @RequestParam("pasta") String pasta) {
        if (!PASTAS_PERMITIDAS.contains(pasta)) {
            return ResponseEntity.badRequest().body(Map.of("erro", "Pasta inválida"));
        }
        try {
            String url = cloudinaryService.upload(file, pasta);
            return ResponseEntity.ok(Map.of("url", url));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(Map.of("erro", "Falha ao enviar imagem"));
        }
    }
}