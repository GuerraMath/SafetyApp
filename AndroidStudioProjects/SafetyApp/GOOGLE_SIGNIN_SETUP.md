# Configuracao do Google Sign-In

## Passos para configurar o Google Sign-In na sua aplicacao

### 1. Criar projeto no Google Cloud Console

1. Acesse [Google Cloud Console](https://console.cloud.google.com/)
2. Crie um novo projeto ou selecione um existente
3. Anote o **Project ID**

### 2. Configurar a tela de consentimento OAuth

1. No menu lateral, va em **APIs & Services** > **OAuth consent screen**
2. Selecione **External** (para usuarios fora da sua organizacao)
3. Preencha as informacoes obrigatorias:
   - Nome do app: SafetyApp
   - Email de suporte: seu email
   - Email do desenvolvedor: seu email
4. Clique em **Save and Continue**
5. Em **Scopes**, adicione:
   - `email`
   - `profile`
   - `openid`
6. Continue ate finalizar

### 3. Criar credenciais OAuth 2.0

1. Va em **APIs & Services** > **Credentials**
2. Clique em **Create Credentials** > **OAuth client ID**

#### 3.1 Criar Web Client ID (OBRIGATORIO)

1. Selecione **Web application**
2. Nome: "SafetyApp Web Client"
3. Nao precisa adicionar origens ou URIs de redirecionamento
4. Clique em **Create**
5. **COPIE O CLIENT ID** (exemplo: `123456789.apps.googleusercontent.com`)

#### 3.2 Criar Android Client ID

1. Clique em **Create Credentials** > **OAuth client ID**
2. Selecione **Android**
3. Nome: "SafetyApp Android Client"
4. Package name: `com.guerramath.safetyapp`
5. SHA-1 certificate fingerprint: (veja abaixo como obter)
6. Clique em **Create**

### 4. Obter SHA-1 do seu app

#### Debug SHA-1 (para desenvolvimento):

```bash
# Windows (cmd)
keytool -list -v -keystore "%USERPROFILE%\.android\debug.keystore" -alias androiddebugkey -storepass android -keypass android

# Windows (PowerShell)
keytool -list -v -keystore "$env:USERPROFILE\.android\debug.keystore" -alias androiddebugkey -storepass android -keypass android

# Mac/Linux
keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android
```

#### Release SHA-1 (para producao):

```bash
keytool -list -v -keystore <path-to-your-release-keystore> -alias <your-key-alias>
```

### 5. Configurar o Client ID no codigo

Abra o arquivo:
`app/src/main/java/com/guerramath/safetyapp/auth/data/google/GoogleAuthManager.kt`

Substitua a linha:
```kotlin
const val WEB_CLIENT_ID = "YOUR_WEB_CLIENT_ID.apps.googleusercontent.com"
```

Pelo seu Web Client ID:
```kotlin
const val WEB_CLIENT_ID = "123456789-abcdef.apps.googleusercontent.com"
```

**IMPORTANTE:** Use o **Web Client ID**, NAO o Android Client ID!

### 6. Configurar o Backend

Seu backend precisa de um endpoint `POST /auth/google` que:

1. Recebe o request:
```json
{
  "id_token": "eyJhbGciOiJS...",
  "email": "usuario@gmail.com",
  "name": "Nome do Usuario",
  "avatar_url": "https://..."
}
```

2. Valida o `id_token` com a biblioteca do Google:
   - Node.js: `google-auth-library`
   - Python: `google-auth`
   - Java: `google-api-client`

3. Cria ou busca o usuario pelo email

4. Retorna a mesma estrutura do login normal:
```json
{
  "token": "jwt_access_token",
  "refresh_token": "jwt_refresh_token",
  "user": {
    "id": "123",
    "name": "Nome",
    "email": "email@gmail.com",
    "avatar_url": "https://..."
  }
}
```

### 7. Exemplo de validacao do token (Node.js)

```javascript
const { OAuth2Client } = require('google-auth-library');

const client = new OAuth2Client(WEB_CLIENT_ID);

async function verifyGoogleToken(idToken) {
  const ticket = await client.verifyIdToken({
    idToken: idToken,
    audience: WEB_CLIENT_ID,
  });

  const payload = ticket.getPayload();

  return {
    email: payload.email,
    name: payload.name,
    picture: payload.picture,
    emailVerified: payload.email_verified,
  };
}
```

### 8. Testar

1. Sincronize o projeto no Android Studio (Sync Project with Gradle Files)
2. Execute o app em um dispositivo/emulador com Google Play Services
3. Clique no botao "Continuar com Google"
4. Selecione uma conta Google
5. O app deve fazer login automaticamente

### Troubleshooting

#### Erro: "Nenhuma conta Google encontrada"
- Verifique se o dispositivo tem uma conta Google configurada
- O emulador precisa ter Google Play Services

#### Erro: "Sign in failed" ou "Developer error"
- Verifique se o SHA-1 esta correto no Google Cloud Console
- Verifique se o package name esta correto
- Certifique-se de usar o **Web Client ID**, nao o Android Client ID

#### Erro: "ApiException: 10"
- O Web Client ID esta incorreto
- O SHA-1 nao foi adicionado corretamente

#### Erro no backend
- Verifique se o endpoint `/auth/google` existe
- Verifique se a validacao do token esta usando o mesmo Web Client ID
