package com.example.demo.controller;

import com.example.demo.model.Financeiro;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
@RequestMapping("/api")
public class FinanceiroController {

    /**
     * Rota da Tela (View)
     * Endereço: http://localhost:8080/api/tela
     * Abre o arquivo index.html que contém os dropdowns.
     */
    @GetMapping("/tela")
    public String exibirTelaFinanceiro() {
        return "forward:/index.html";
    }

    /**
     * Rota dos Dados (API)
     * Endereço: http://localhost:8080/api/financeiro?moedas=USD-BRL
     * Chamada pelo fetch() do JavaScript baseado nas escolhas dos selects.
     */
    @GetMapping("/financeiro")
    @ResponseBody // Faz o Spring devolver um objeto JSON limpo para o JavaScript
    public Financeiro buscarDadosFinanceiros(
            @RequestParam(value = "moedas", defaultValue = "USD-BRL") String moedas) {

        // Se o JS mandar "USD-BRL", a chave vira "USDBRL" (padrão de leitura do JSON da API externa)
        String chaveJson = moedas.replace("-", "").toUpperCase().trim();

        // Monta o endereço da API externa com as moedas escolhidas
        String url = "https://economia.awesomeapi.com.br/last/" + moedas;

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "application/json")
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                String json = response.body();

                // Puxa as informações dinâmicas de dentro do JSON usando a Expressão Regular
                String nome  = extrairCampoDinamico(json, chaveJson, "name");
                String valor = extrairCampoDinamico(json, chaveJson, "bid"); // Pega o preço atual
                String data  = extrairCampoDinamico(json, chaveJson, "create_date");

                // Monta o modelo final e envia de volta para o JavaScript do HTML
                return new Financeiro(nome, valor, data);
            }
        } catch (Exception e) {
            System.out.println("Erro ao conectar com API externa: " + e.getMessage());
        }

        // Retorno de segurança caso dê falha no par de moedas escolhido
        return new Financeiro("Moeda Inválida (" + moedas + ")", "0.00", "Nao disponivel");
    }

    /**
     * Método Auxiliar (Expressão Regular)
     * Encontra e recorta o valor do campo correto do JSON com base na moeda selecionada
     */
    private String extrairCampoDinamico(String json, String moeda, String chave) {
        Pattern pattern = Pattern.compile("\"" + moeda + "\":\\{.*?\"" + chave + "\":\"([^\"]+)\"");
        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "Nao encontrado";
    }
}