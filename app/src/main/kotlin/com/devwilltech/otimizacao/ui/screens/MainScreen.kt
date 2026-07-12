package com.devwilltech.otimizacao.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.devwilltech.otimizacao.MainViewModel
import com.devwilltech.otimizacao.ShizukuStatus
import com.devwilltech.otimizacao.ui.components.*
import com.devwilltech.otimizacao.ui.theme.*

@Composable
fun MainScreen(viewModel: MainViewModel) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
        // Conteúdo Principal
        LazyColumn(
            modifier            = Modifier
                .fillMaxSize()
                .background(Color.Black),
            contentPadding      = PaddingValues(bottom = 48.dp, top = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            item { 
                val uiState by viewModel.uiState.collectAsState()
                Box(modifier = Modifier.fillMaxWidth()) {
                    WillTechHeader()
                    
                    // Botão Ocultar Transmissão no canto superior direito
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            if (uiState.isHideStreamEnabled) "STREAM OCULTA" else "OCULTAR STREAM",
                            color = if (uiState.isHideStreamEnabled) NeonYellow else Color.Gray,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Switch(
                            checked = uiState.isHideStreamEnabled,
                            onCheckedChange = { viewModel.toggleHideStream(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = NeonYellow,
                                checkedTrackColor = NeonYellow.copy(alpha = 0.5f),
                                uncheckedThumbColor = Color.Gray,
                                uncheckedTrackColor = Color.DarkGray
                            ),
                            modifier = Modifier.scale(0.7f)
                        )
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                        .border(1.dp, ErrorRed.copy(alpha = 0.3f), RoundedCornerShape(20.dp)),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0A0A0A))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(ErrorRed)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "AVISO DE PERFORMANCE",
                                color = ErrorRed,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp,
                                letterSpacing = 1.sp
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "AO ATIVAR, É NORMAL SEU DISPOSITIVO DIMINUIR A BATERIA MAIS RÁPIDO POIS O DESEMPENHO SERÁ AUMENTADO AO EXTREMO.\n\n" +
                                   "QUANDO NÃO QUISER MAIS USAR, BASTA DESATIVAR PARA VOLTAR AO NORMAL.\n\n" +
                                   "Obs: Recomendamos reiniciar seu dispositivo após o uso (não obrigatório).",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            item {
                ShizukuStatusCard(
                    status              = uiState.shizukuStatus,
                    onRequestPermission = viewModel::requestShizukuPermission,
                    modifier            = Modifier.padding(horizontal = 16.dp)
                )
            }

            item {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    NeonButton(
                        text      = "ATIVAR OTIMIZAÇÃO EXTREMA",
                        onClick   = viewModel::activateAll,
                        enabled   = uiState.canAct,
                        showLock  = uiState.shizukuStatus != ShizukuStatus.READY,
                        isLoading = uiState.isRunning,
                        modifier  = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                    )
                    
                    GhostDangerButton(
                        text      = "DESATIVAR E VOLTAR AO PADRÃO",
                        onClick   = viewModel::deactivateAll,
                        enabled   = uiState.canAct,
                        isLoading = uiState.isResetting,
                        modifier  = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    )
                }
            }

            item {
                AnimatedVisibility(
                    visible = uiState.showReport,
                    enter   = fadeIn() + expandVertically(),
                    exit    = fadeOut() + shrinkVertically()
                ) {
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        ReportPanel(
                            results   = uiState.results,
                            onDismiss = viewModel::dismissReport
                        )
                    }
                }
            }
            
            item { Spacer(Modifier.height(20.dp)) }
        }

        // Camada de Tela de Processamento (Ativação/Desativação)
        AnimatedVisibility(
            visible = uiState.isRunning || uiState.isResetting,
            enter   = fadeIn(),
            exit    = fadeOut()
        ) {
            ProcessingScreen(
                isResetting = uiState.isResetting,
                current     = uiState.currentCommandIndex,
                total       = uiState.totalCommands
            )
        }
    }
}
