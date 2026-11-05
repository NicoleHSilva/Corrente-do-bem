# 🌿 Corrente do Bem

Aplicativo Android com o tema **Sustentabilidade**, desenvolvido como **trabalho da faculdade** no curso de **Análise e Desenvolvimento de Sistemas** da **Unicsul**. O foco da atividade é o **front end**: layout, imagens, UI, cores e temas.

O app incentiva a comunidade a **identificar pontos de lixo que precisam de ajuda para retirada**, compartilhar ações de limpeza e acompanhar metas sustentáveis do dia a dia.

> Projeto acadêmico. Os dados de exemplo (posts, autores e pontos no mapa) são fictícios e servem apenas para demonstração visual.

## ✨ Funcionalidades

### 🔐 Conta (login local)
- Ao abrir o app pela primeira vez, é preciso **criar uma conta** (nome, e-mail e senha) ou **entrar** em uma conta existente.
- Cada conta tem o **seu próprio perfil e as suas publicações**.
- As contas ficam **salvas apenas no aparelho**, e a senha é guardada como hash com sal (nunca em texto). Não há servidor, então o e-mail não é verificado e não há recuperação de senha.

O app tem uma barra de navegação inferior com três abas.

### 🏠 Início (Feed)
Tela inicial no estilo rede social:
- Cabeçalho colorido com atalhos para as telas **Dicas** e **Metas**.
- Caixa para criar uma publicação com **texto** e/ou **foto da galeria**.
- Lista de publicações com 3 posts de exemplo, ilustrados com desenhos vetoriais (praia, mutirão no rio e reciclagem).
- Em cada publicação: **curtir** (com contador), **compartilhar** (menu de compartilhamento do Android) e **excluir** (com confirmação).

### 🗺️ Mapa
- Mapa real do **OpenStreetMap**, que abre mostrando o **Brasil inteiro**, com zoom e movimentação por gestos.
- **10 pontos de exemplo** espalhados pelo país: pino laranja = precisa de ajuda; pino verde = já foi limpo.
- Botão **Adicionar ponto**: toque no mapa para escolher o local e preencha o nome e a descrição.
- Ao tocar em um pino: ver detalhes, **marcar como limpo**, **reabrir** ou **remover** o ponto.
- **Prova de limpeza:** para marcar um local como limpo, é obrigatório anexar uma **foto de prova** (com comentário opcional). A prova fica visível nos detalhes do ponto e é apagada se o ponto for reaberto.
- Aviso no topo com a contagem de pontos pendentes e já limpos.

### 👤 Perfil (estilo Instagram)
- Foto, nome e "sobre mim" no topo, com a quantidade de **publicações** e de **curtidas** recebidas.
- Botão **Editar perfil** (foto, nome e bio) e botão de **Configurações**.
- Grade com as **publicações feitas pelo usuário**. Sem publicações, aparece "Nenhuma publicação até o momento".

### ⚙️ Configurações
- **Aparência:** tema claro, escuro ou automático (igual ao celular), salvo ao fechar o app.
- **Conta:** redefinir e-mail, alterar senha, trocar de conta e sair da conta.
- **Mapa:** mostrar ou esconder os pontos já limpos.
- **Dados do app:** restaurar os dados de exemplo ou apagar publicações e pontos.
- **Sobre:** versão do app e licença.

### 📚 Telas secundárias (abertas pelos atalhos do Feed)
- **Dicas Sustentáveis:** 4 cartões (água, energia, descarte correto e consumo consciente).
- **Minhas Metas:** checklist de 5 ações ecológicas, com barra de progresso e mensagem de incentivo ao completar tudo.

## 🎨 Design
- Material Design 3, com paleta de **verdes** (natureza) e **cianos** (água).
- Fundo em **degradê** suave de azul para verde, com versão própria no **modo escuro**.
- Imagens e ícones **vetoriais** (XML): ilustrações desenhadas para o projeto, sem uso de imagens de terceiros.
- Cores, estilos, dimensões e textos organizados em `colors.xml`, `styles.xml`, `themes.xml`, `dimens.xml` e `strings.xml`.

## 🛠️ Tecnologias
| Item | Versão / detalhe |
|---|---|
| IDE | Android Studio |
| Linguagem | Kotlin 1.9.24 |
| Android Gradle Plugin | 8.5.2 |
| Gradle | 8.7 |
| JDK | 17 |
| compileSdk / targetSdk / minSdk | 34 / 34 / 26 (Android 8.0 ou superior) |
| Pacote | `com.example.correntedobem` |

**Bibliotecas:** AndroidX (core-ktx, appcompat, constraintlayout, recyclerview), Material Components 1.12.0 e osmdroid 6.1.18 (mapa OpenStreetMap, sem necessidade de chave de API).

**Permissões:** `INTERNET` e `ACCESS_NETWORK_STATE`, usadas para carregar o mapa.

## 📁 Estrutura do projeto
```
app/src/main/
├── AndroidManifest.xml
├── java/com/example/correntedobem/
│   ├── CorrenteApp.kt            # inicialização (mapa e tema salvo)
│   ├── LoginActivity.kt          # entrar e criar conta
│   ├── MainActivity.kt           # barra inferior e troca de abas
│   ├── FeedFragment.kt           # aba Início (feed)
│   ├── PostAdapter.kt            # lista de publicações
│   ├── MapaFragment.kt           # aba Mapa
│   ├── PerfilFragment.kt         # aba Perfil
│   ├── GradeAdapter.kt           # grade de publicações do perfil
│   ├── ConfiguracoesActivity.kt  # tela de configurações
│   ├── DicasActivity.kt          # tela de dicas
│   ├── MetasActivity.kt          # tela de metas
│   ├── Modelos.kt                # Ponto, Post, Perfil e Conta
│   ├── Armazenamento.kt          # salvar e ler dados
│   └── Imagens.kt                # tratamento das fotos
└── res/
    ├── layout/                   # telas, itens e caixas de diálogo
    ├── drawable/                 # ícones e ilustrações vetoriais
    ├── menu/                     # menu da barra inferior
    ├── values/                   # cores, textos, estilos, tema
    └── values-night/             # cores do modo escuro
```

## ▶️ Como executar
1. Clone o repositório:
   ```
   git clone https://github.com/NicoleHSilva/corrente-do-bem.git
   ```
2. Abra o **Android Studio** e escolha **File > Open**, selecionando a pasta do projeto (a que contém `settings.gradle.kts`).
3. Aguarde o **Gradle Sync** terminar (é preciso internet na primeira vez).
4. Escolha um emulador (ex.: Pixel com Android 14, API 34) ou um celular com depuração USB e clique em **Run**.

> O mapa precisa de **internet** para carregar. Se os posts ou pontos de exemplo não aparecerem, desinstale o app do emulador (ou use **Device Manager > Wipe Data**) e rode de novo.

## 💾 Armazenamento dos dados
Nesta versão os dados ficam **salvos apenas no aparelho** (SharedPreferences em JSON e fotos na pasta interna do app). Não há servidor, então contas, posts, pontos e provas não são compartilhados entre usuários.

## 🔮 Próximos passos
- Login e dados online (ex.: Firebase), para compartilhar posts e pontos entre usuários e recuperar senha.
- Comentários nas publicações.
- Marcar o ponto no mapa pela localização atual do usuário.

## 📝 Histórico de atualizações
| Versão | Mudanças |
|---|---|
| 1.0 | App com tema Sustentabilidade: telas de Dicas e Metas, layouts, cores e modo escuro. |
| 1.1 | Barra inferior com abas de **Mapa**, **Feed** e **Perfil**; dados salvos no aparelho. |
| 1.2 | Feed vira a tela inicial (estilo rede social, com posts de exemplo); mapa abre no Brasil com pontos de exemplo; novo fundo em degradê. |
| 1.3 | Revisão dos nomes internos do projeto (pacote, tema e classe inicial) e do README. |
| 1.4 | **Prova de limpeza** (foto obrigatória) ao marcar um ponto como limpo; tela de **Configurações**. |
| 1.5 | **Contas locais** (criar conta, entrar, trocar de conta, sair, redefinir e-mail e alterar senha); **Perfil estilo Instagram** com grade das publicações do usuário. |

## 👩‍💻 Autora
**Nicole Henrique Silva**, aluna de Análise e Desenvolvimento de Sistemas, Unicsul.

## 📄 Licença
Distribuído sob a licença **MIT**. Veja o arquivo [LICENSE](LICENSE).