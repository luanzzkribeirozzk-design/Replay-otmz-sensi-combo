package com.devwilltech.otimizacao.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devwilltech.otimizacao.MainViewModel
import com.devwilltech.otimizacao.ui.theme.NeonYellow

@Composable
fun LoginScreen(
    viewModel: MainViewModel,
    onLoginSuccess: () -> Unit
) {
    val uiState     by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current

    // Estado local da key digitada — sem limite de tamanho
    var keyInput     by remember { mutableStateOf("") }
    var showKey      by remember { mutableStateOf(false) }

    // Navega quando login for concluído
    LaunchedEffect(uiState.isLoggedIn) {
        if (uiState.isLoggedIn) onLoginSuccess()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier            = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ── Logo / título ──────────────────────────────────────
            Text(
                text       = "⚡ WILLTECH",
                color      = NeonYellow,
                fontSize   = 28.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 3.sp
            )
            Text(
                text      = "Insira sua chave de acesso",
                color     = Color(0xFF666666),
                fontSize  = 12.sp,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(8.dp))

            // ── Campo de key ───────────────────────────────────────
            // ⚡ FIX: sem maxLength, sem limitação de caracteres,
            //        aceita key de qualquer tamanho
            OutlinedTextField(
                value         = keyInput,
                onValueChange = { keyInput = it },   // sem filtro de tamanho
                modifier      = Modifier.fillMaxWidth(),
                placeholder   = {
                    Text(
                        "Cole sua key aqui",
                        color    = Color(0xFF444444),
                        fontSize = 13.sp
                    )
                },
                singleLine    = false,               // permite quebra visual se key for longa
                maxLines      = 4,                   // máx. 4 linhas visuais mas sem limite de chars
                visualTransformation = if (showKey)
                    VisualTransformation.None
                else
                    PasswordVisualTransformation('•'),
                trailingIcon  = {
                    TextButton(onClick = { showKey = !showKey }) {
                        Text(
                            if (showKey) "OCULTAR" else "VER",
                            color    = NeonYellow,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction    = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        if (keyInput.isNotBlank() && !uiState.isLoggingIn) {
                            viewModel.login(keyInput.trim())
                        }
                    }
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = NeonYellow,
                    unfocusedBorderColor = Color(0xFF333333),
                    focusedTextColor     = NeonYellow,
                    unfocusedTextColor   = Color(0xFFCCCCCC),
                    cursorColor          = NeonYellow
                ),
                shape = RoundedCornerShape(10.dp)
            )

            // Contador de caracteres (info visual, não é limite)
            if (keyInput.isNotEmpty()) {
                Text(
                    text      = "${keyInput.length} caracteres",
                    color     = Color(0xFF444444),
                    fontSize  = 10.sp,
                    modifier  = Modifier.align(Alignment.End)
                )
            }

            // Mensagem de erro
            if (uiState.loginError != null) {
                Text(
                    text      = uiState.loginError!!,
                    color     = Color(0xFFFF453A),
                    fontSize  = 12.sp,
                    textAlign = TextAlign.Center
                )
            }

            // ── Botão entrar ───────────────────────────────────────
            Button(
                onClick  = {
                    focusManager.clearFocus()
                    viewModel.login(keyInput.trim())
                },
                enabled  = keyInput.isNotBlank() && !uiState.isLoggingIn,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape    = RoundedCornerShape(10.dp),
                colors   = ButtonDefaults.buttonColors(
                    containerColor         = NeonYellow,
                    contentColor           = Color.Black,
                    disabledContainerColor = Color(0xFF222200),
                    disabledContentColor   = Color(0xFF555500)
                )
            ) {
                if (uiState.isLoggingIn) {
                    CircularProgressIndicator(
                        modifier    = Modifier.size(20.dp),
                        color       = Color.Black,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        "ENTRAR",
                        fontWeight    = FontWeight.Black,
                        fontSize      = 14.sp,
                        letterSpacing = 2.sp
                    )
                }
            }
        }
    }
}
