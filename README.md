# ⚡ Otimização Will Tech
### by @DEVWILLTECH

> Aplicativo Android para otimização extrema de Free Fire e Free Fire MAX via ADB Shell / Depuração Sem Fio.

[![Android CI](https://github.com/SEU_USUARIO/OtimizacaoWillTech/actions/workflows/android.yml/badge.svg)](https://github.com/SEU_USUARIO/OtimizacaoWillTech/actions/workflows/android.yml)

---

## 📋 Funcionalidades

- ✅ **100 comandos de otimização** categorizados (Sistema, Gráficos, Touch, Rede, Bateria, Memória, Desempenho, Jogo)
- ✅ **Pop-up de boas-vindas** com links para TikTok e Instagram do @DEVWILLTECH
- ✅ **Conexão via ADB Shell / Depuração Sem Fio** com controle de IP, Porta e Código
- ✅ **Sistema de cadeado** — botões bloqueados enquanto ADB não estiver conectado
- ✅ **Barra de progresso fluida** ao ativar os comandos
- ✅ **Relatório detalhado** de sucesso/falha por comando
- ✅ **Filtro por categoria** com chips interativos
- ✅ **Design Gamer Premium** — fundo grafite fosco + acentos Neon Amarelo (#FFD700)
- ✅ **CI/CD automático** via GitHub Actions (build + upload do APK)

---

## 🚀 Começar

### Pré-requisitos
- Android Studio Hedgehog (2023.1.1) ou superior
- JDK 17
- Android SDK 26+

### Clonar e compilar
```bash
git clone https://github.com/SEU_USUARIO/OtimizacaoWillTech.git
cd OtimizacaoWillTech
./gradlew assembleDebug
```
O APK gerado fica em `app/build/outputs/apk/debug/app-debug.apk`.

---

## 📱 Como usar

1. No dispositivo alvo, ative **Opções do Desenvolvedor** e depois **Depuração Sem Fio**
2. Anote o IP e a Porta exibidos pelo Android
3. Abra o app e insira as informações na seção **ADB Shell**
4. Toque em **Conectar via ADB** — o cadeado desaparece
5. Use **ATIVAR TUDO** para disparar todos os 100 comandos sequencialmente
6. Consulte o **relatório de resultado** ao final
7. Se precisar reverter, use **DESATIVAR TUDO** (Restaurar Padrões)

---

## 📂 Estrutura do Projeto

```
OtimizacaoWillTech/
├── .github/
│   └── workflows/
│       └── android.yml          ← GitHub Actions CI/CD
├── app/
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── kotlin/com/devwilltech/otimizacao/
│       │   ├── MainActivity.kt
│       │   ├── MainViewModel.kt
│       │   ├── data/
│       │   │   └── CommandRepository.kt   ← 100 comandos ADB
│       │   ├── utils/
│       │   │   └── AdbManager.kt          ← Camada de conexão ADB
│       │   └── ui/
│       │       ├── components/
│       │       │   ├── Components.kt      ← Botões, chips, cards
│       │       │   └── WelcomePopup.kt    ← Modal de boas-vindas
│       │       ├── screens/
│       │       │   ├── MainScreen.kt      ← Tela principal
│       │       │   ├── AdbConnectionScreen.kt
│       │       │   └── ReportPanel.kt     ← Painel de resultado
│       │       └── theme/
│       │           ├── Color.kt
│       │           └── Theme.kt
│       └── res/
│           ├── drawable/                  ← Ícone placeholder (substituir)
│           └── values/
│               ├── strings.xml
│               └── themes.xml
├── build.gradle.kts
├── settings.gradle.kts
└── gradle/
    └── libs.versions.toml               ← Version catalog
```

---

## 🖼️ Personalização

### Ícone do App
Substitua os arquivos em `app/src/main/res/drawable/`:
- `ic_launcher_background.xml` → cor de fundo
- `ic_launcher_foreground.xml` → ícone vetorial (ou PNG)

Ou use **Android Studio › File › New › Image Asset** para gerar ícones adaptativos automaticamente.

### Avatar do Pop-up
No arquivo `WelcomePopup.kt`, localize o comentário `// 🖼️ PLACEHOLDER` e substitua o `Box` por um `Image`:
```kotlin
Image(
    painter = painterResource(R.drawable.seu_avatar),
    contentDescription = "Avatar @DEVWILLTECH",
    modifier = Modifier.size(96.dp).clip(CircleShape)
)
```

---

## ⚙️ GitHub Actions

A cada `push` ou `pull_request` na branch `main`, o workflow:
1. Configura JDK 17
2. Cacheia dependências Gradle
3. Compila o APK Debug (`./gradlew assembleDebug`)
4. Faz upload do APK como artefato (disponível por 30 dias na aba **Actions** do GitHub)

Para baixar: **GitHub › Actions › último workflow bem-sucedido › Artifacts → OtimizacaoWillTech-debug-X**

---

## 📣 Redes Sociais

- **TikTok:** [@devwilltech](https://www.tiktok.com/@devwilltech)
- **Instagram:** [@devwilltech](https://www.instagram.com/devwilltech)

---

> ⚠️ **Aviso:** Alguns comandos requerem permissões de root ou execução via ADB com nível de acesso adequado. Teste em dispositivo próprio. O desenvolvedor não se responsabiliza por danos causados pelo uso indevido dos comandos.
