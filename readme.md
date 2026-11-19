# Serviço de Agendamento Salão SENAC
Desenvolvido para o SENAC DF, o sistema web de agendamento tem como objetivo tornar mais simples e eficiente o agendamento de horários para os serviços de beleza gratuitos oferecidos pela instituição.

A plataforma permite que a comunidade local agende serviços de imagem pessoal de forma mais prática, organizada e eficiente, garantindo melhor gestão dos atendimentos e maior facilidade de acesso aos serviços.

## Funcionalidades
- Cadastro de clientes
- Login no sistema
- Agendamento de serviços
- Visualização de horários disponíveis
- Corfirmação de agendamentos
- Cancelamento de agendamentos


## Tecnologias Utilizadas

- HTML
- CSS
- Java
- Java Script
- Spring Boot
- Thymeleaf
- MySQL

## Estrutura do Sistema

O sistema foi dividido em módulos para facilitar a manutenção e escalabilidade:

- Autenticação de usuários
- Cadastro de clientes
- Gerenciamento de serviços
- Sistema de agendamento
- Painel administrativo

## Moderação de depoimentos

Usuários autenticados com e-mail terminado exatamente em `@df.senac.br` podem acessar a rota `/admin`, usando o mesmo domínio institucional exigido pelo sistema desktop. E-mails de domínios parecidos não recebem acesso.

Na primeira inicialização, o Flyway aplica automaticamente a migração de `src/main/resources/db/migration`, adicionando a coluna de moderação e mantendo os depoimentos existentes publicados. O usuário configurado em `USUARIO` precisa ter permissão `ALTER` no banco; novos depoimentos aguardam aprovação e podem ser aprovados ou rejeitados na área administrativa.
## Benefícios

Com a implementação deste sistema, o Salão Escola SENAC passa a contar com uma solução digital que melhora a experiência dos usuários e torna a administração mais eficiente.

## Desenvolvido por
Turma **2025.08.133** do curso Técnico em Desenvolvimento de Sistemas do SENAC CEP José Rufino e Carlos Aguiar 
