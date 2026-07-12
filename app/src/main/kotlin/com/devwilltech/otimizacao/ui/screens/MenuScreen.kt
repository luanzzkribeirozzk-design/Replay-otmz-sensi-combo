package com.devwilltech.otimizacao.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devwilltech.otimizacao.ui.theme.NeonYellow
import com.devwilltech.otimizacao.ui.components.WillTechHeader

@Composable
fun MenuScreen(
    onSelectOptimization: () -> Unit,
    onSelectBypass: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            WillTechHeader()

            Text(
                "PAINEL DE FERRAMENTAS",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Opção Otimização
                Card(
                    onClick = onSelectOptimization,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, NeonYellow.copy(alpha = 0.5f), RoundedCornerShape(24.dp)),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0A0A0A))
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text("OTIMIZAÇÃO EXTREMA", color = NeonYellow, fontWeight = FontWeight.Black, fontSize = 18.sp)
                        Text("Acelerar sistema, jogos e sensibilidade", color = Color.Gray, fontSize = 12.sp)
                    }
                }

                // Opção Bypass
                Card(
                    onClick = onSelectBypass,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFF7B00AA).copy(alpha = 0.5f), RoundedCornerShape(24.dp)),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0A0A0A))
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text("BYPASS REPLAYS", color = Color(0xFF7B00AA), fontWeight = FontWeight.Black, fontSize = 18.sp)
                        Text("Migrar replays entre FF Normal e Max", color = Color.Gray, fontSize = 12.sp)
                    }
                }
            }

            Text(
                "SEGURANÇA MÁXIMA ATIVADA",
                color = Color(0xFF003311),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
