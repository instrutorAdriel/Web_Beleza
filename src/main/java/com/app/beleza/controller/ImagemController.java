package com.app.beleza.controller;

import com.app.beleza.model.Depoimento;
import com.app.beleza.model.Produto;
import com.app.beleza.respository.DepoimentoRepository;
import com.app.beleza.respository.ProdutoRepository;
import com.app.beleza.service.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/imagens")
public class ImagemController {

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private DepoimentoRepository depoimentoRepository;

    // PRODUTO -> imagemAnexo
    @PostMapping(value = "/produtos/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> imagemProduto(@PathVariable Integer id,
                                           @RequestParam("file") MultipartFile file) {
        Produto produto = produtoRepository.findById(id).orElse(null);
        if (produto == null) {
            return ResponseEntity.status(404).body(Map.of("erro", "Produto não encontrado"));
        }
        try {
            String url = cloudinaryService.upload(file, "produtos");
            produto.setImagemAnexo(url);
            produtoRepository.save(produto);
            return ResponseEntity.ok(Map.of("url", url));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("erro", "Falha ao enviar imagem"));
        }
    }

    // DEPOIMENTO -> imagemAnexo1 ou imagemAnexo2 (posicao = 1 ou 2)
    @PostMapping(value = "/depoimentos/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> imagemDepoimento(@PathVariable Integer id,
                                              @RequestParam("posicao") int posicao,
                                              @RequestParam("file") MultipartFile file) {
        if (posicao != 1 && posicao != 2) {
            return ResponseEntity.badRequest().body(Map.of("erro", "posicao deve ser 1 ou 2"));
        }
        Depoimento depoimento = depoimentoRepository.findById(id).orElse(null);
        if (depoimento == null) {
            return ResponseEntity.status(404).body(Map.of("erro", "Depoimento não encontrado"));
        }
        try {
            String url = cloudinaryService.upload(file, "depoimentos");
            if (posicao == 1) {
                depoimento.setImagemAnexo1(url);
            } else {
                depoimento.setImagemAnexo2(url);
            }
            depoimentoRepository.save(depoimento);
            return ResponseEntity.ok(Map.of("url", url, "posicao", posicao));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("erro", "Falha ao enviar imagem"));
        }
    }
}