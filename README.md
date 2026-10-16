# 🍕 Sistema PediuPatrão - Atividade Prática M1

Este repositório contém a evolução individual do projeto "PediuPatrão", desenvolvido como requisito para a Atividade Prática M1 do curso de Engenharia de Software. O foco principal desta iteração foi a implementação de um controlo rigoroso de acessos, gestão de fluxo de pedidos e um sistema robusto de auditoria.

## 🛠 Tecnologias Utilizadas
* **Linguagem:** Java 21
* **Framework:** Spring Boot 3.4.4
* **Segurança:** Spring Security
* **Base de Dados:** MongoDB
* **Frontend:** Thymeleaf, HTML5, CSS3
* **Gestão de Dependências:** Maven

## 🚀 Requisitos Implementados 

* **[RF01] Controlo de Utilizadores e Permissões:** Implementação dos perfis `ADMIN`, `GERENTE` e `ATENDENTE`. A proteção foi aplicada ao nível do *backend* (Spring Security), bloqueando tentativas de autoelevação e acessos indevidos via rotas (HTTP 403).
* **[RF02] Evolução do Cadastro de Pedidos:** Registo detalhado de itens e quantidades, com o cálculo do valor total executado estritamente no servidor, impedindo manipulações via requisições HTTP.
* **[RF03] Controlo de Transições de Pedidos:** Máquina de estados validada no domínio (`RECEBIDO` → `EM_PREPARACAO` → `PRONTO` → `SAIU_PARA_ENTREGA` → `FINALIZADO`), registando o responsável e a data/hora nas transições críticas.
* **[RF04] Cancelamentos e Descontos (Exclusivo Gerente):** Regras de negócio estritas que permitem a aplicação de descontos (limitados a 20%) e cancelamentos (com justificação obrigatória) apenas ao perfil `GERENTE`, bloqueando inclusive o `ADMIN`.
* **[RF05] Auditoria:** Registo persistente e imutável no MongoDB para rastreabilidade de alterações em pedidos, mudanças de status, descontos, cancelamentos e atualizações cadastrais de clientes (registando valor anterior e novo). Acesso restrito aos perfis de gestão.

## ⚙️ Como Executar o Projeto Localmente

**Pré-requisitos:**
* JDK 21 instalado e configurado nas variáveis de ambiente.
* MongoDB Community Server a correr localmente na porta padrão (`27017`).

**Passos para execução:**
1. Clone este repositório para a sua máquina local.
2. Abra o terminal na raiz do projeto (pasta onde se encontra o `pom.xml`).
3. Como medida de segurança, o sistema não possui credenciais padrão codificadas. Configure o administrador inicial definindo as variáveis de ambiente e inicie a aplicação executando o script PowerShell:
   ```powershell
   $env:PEDIUPATRAO_ADMIN_USERNAME = "admin"
   $env:PEDIUPATRAO_ADMIN_PASSWORD = "123"
   .\run-local.ps1