package com.devwilltech.otimizacao.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devwilltech.otimizacao.MainViewModel
import kotlinx.coroutines.launch

@Composable
fun BypassScreen(viewModel: MainViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    val purpleColor = Color(0xFFBB86FC)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        
        Text(
            "BYPASS REPLAYS",
            color = purpleColor,
            fontSize = 20.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 2.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = { viewModel.startBypass("MAX_TO_NORMAL") },
                modifier = Modifier
                    .weight(1f)
                    .height(80.dp)
                    .border(1.dp, purpleColor.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("MAX ➔ NORMAL", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = { viewModel.startBypass("NORMAL_TO_MAX") },
                modifier = Modifier
                    .weight(1f)
                    .height(80.dp)
                    .border(1.dp, purpleColor.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("NORMAL ➔ MAX", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color(0xFF0A0A0A), RoundedCornerShape(16.dp))
                .border(1.dp, Color(0xFF333333), RoundedCornerShape(16.dp))
                .padding(12.dp)
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(uiState.bypassLogs) { log ->
                    // Filtra comandos internos e erros técnicos, mostra apenas mensagens amigáveis
                    val cleanLog = when {
                        log.contains("rm -rf") || log.contains("mkdir") || log.contains("cp -r") -> null
                        log.contains("Iniciando Bypass") -> "➔ Preparando arquivos..."
                        log.contains("Bypass Concluído") -> "✔ Processo finalizado com sucesso!"
                        log.contains("ERRO") -> "✖ Falha ao acessar arquivos (Verifique o Shizuku)"
                        else -> log
                    }

                    if (cleanLog != null) {
                        Text(
                            cleanLog,
                            color = if (cleanLog.contains("✔")) Color.Green else if (cleanLog.contains("✖")) Color.Red else Color.Gray,
                            fontSize = 13.sp,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { viewModel.clearBypassLogs() },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .border(1.dp, Color(0xFF333333), RoundedCornerShape(12.dp)),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("LIMPAR LOG", color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
        
        Spacer(modifier = Modifier.height(60.dp))
    }
}
