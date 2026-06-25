package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.UUID;

@Controller
public class AvaliacoesController {

    // Diretório onde as imagens de avaliação serão salvas.
    private static final String DIRETORIO_UPLOAD = "uploads/avaliacoes";

    /**
     * Exibe a página/home onde está o popup de avaliação.
     * Use esse GET se quiser acessar /avaliacoes direto pelo navegador.
     */
    @GetMapping("/avaliacoes")
    public String exibirFormulario(Model model) {
        // Se quiser listar avaliações já cadastradas, adicione aqui:
        // model.addAttribute("avaliacoes", avaliacaoService.listarTodas());
        return "home"; // nome do template home.html (ajuste se for diferente)
    }

    /**
     * Recebe os dados do formulário de avaliação enviados pelo popup.
     */
    @PostMapping("/avaliacoes")
    public String salvarAvaliacao(
            @RequestParam("imagem") MultipartFile imagem,
            @RequestParam("descricao") String descricao,
            @RequestParam(value = "nomeModelo", required = false) String nomeModelo,
            Model model) {

        try {
            String caminhoImagem = salvarImagem(imagem);

            // Substitua pela lógica real de persistência (ex: JPA Repository)
            System.out.println("Nova avaliação recebida:");
            System.out.println("Modelo: " + (nomeModelo != null && !nomeModelo.isBlank() ? nomeModelo : "Não informado"));
            System.out.println("Descrição: " + descricao);
            System.out.println("Imagem salva em: " + caminhoImagem);
            System.out.println("Data: " + LocalDateTime.now());

            model.addAttribute("mensagemSucesso", "Avaliação enviada com sucesso!");

        } catch (IOException e) {
            model.addAttribute("mensagemErro", "Erro ao salvar a imagem da avaliação.");
            e.printStackTrace();
        }

        return "redirect:/avaliacoes";
    }

    private String salvarImagem(MultipartFile imagem) throws IOException {
        if (imagem == null || imagem.isEmpty()) {
            throw new IOException("Nenhuma imagem foi enviada.");
        }

        Path diretorio = Paths.get(DIRETORIO_UPLOAD);
        if (!Files.exists(diretorio)) {
            Files.createDirectories(diretorio);
        }

        String extensao = "";
        String nomeOriginal = imagem.getOriginalFilename();
        if (nomeOriginal != null && nomeOriginal.contains(".")) {
            extensao = nomeOriginal.substring(nomeOriginal.lastIndexOf("."));
        }

        String nomeArquivo = UUID.randomUUID() + extensao;
        Path caminhoDestino = diretorio.resolve(nomeArquivo);

        Files.copy(imagem.getInputStream(), caminhoDestino, StandardCopyOption.REPLACE_EXISTING);

        return caminhoDestino.toString();
    }
}