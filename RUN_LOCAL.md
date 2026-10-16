# Executar localmente

1. Abra um terminal PowerShell nesta pasta.
2. Execute `.\run-local.ps1`.
3. Acesse <http://localhost:3001/login> e mantenha o terminal aberto.

O script usa o JDK 21 instalado em `C:\Program Files\Eclipse Adoptium\jdk-21.0.12.101-hotspot` e inicia o Spring Boot na porta 3001. O processo fica ligado ao terminal; fechá-lo encerra o servidor.

## MongoDB e login

A página de login pode abrir sem MongoDB, mas autenticação e telas com dados precisam de uma conexão MongoDB. Sem configuração, a aplicação tenta `mongodb://localhost:27017/pizzaria`. Para usar o Atlas, configure `MONGODB_URI` no PowerShell antes de iniciar, por exemplo:

```powershell
$env:MONGODB_URI = 'mongodb+srv://USUARIO:SENHA@SEU-CLUSTER/...?retryWrites=true&w=majority'
.\run-local.ps1
```

O Atlas também precisa permitir o IP desta máquina na lista de acesso de rede. Para criar o primeiro administrador automaticamente, defina `PEDIUPATRAO_ADMIN_PASSWORD` antes da inicialização; o usuário padrão é `admin`, alterável por `PEDIUPATRAO_ADMIN_USERNAME`.

Não grave credenciais reais no arquivo de configuração nem neste documento.
