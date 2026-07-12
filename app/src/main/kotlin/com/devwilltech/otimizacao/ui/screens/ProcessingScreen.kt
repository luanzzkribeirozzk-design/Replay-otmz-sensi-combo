package com.devwilltech.otimizacao.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devwilltech.otimizacao.ui.theme.NeonYellow

@Composable
fun ProcessingScreen(
    isResetting: Boolean,
    current: Int,
    total: Int
) {
    val progress = if (total > 0) current.toFloat() / total else 0f
    val title = if (isResetting) "RESTAURANDO PADRÃO" else "ATIVANDO OTIMIZAÇÃO"
    val subtitle = if (isResetting) "Removendo modificações..." else "Aplicando comandos extremos..."

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Círculo de Carregamento Neon
            val infiniteTransition = rememberInfiniteTransition(label = "loader")
            val rotation by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1500, easing = LinearEasing)
                ),
                label = "rotation"
            )

            CircularProgressIndicator(
                modifier = Modifier.size(80.dp),
                color = NeonYellow,
                strokeWidth = 6.dp,
                trackColor = Color(0xFF1A1A1A)
            )

            Spacer(Modifier.height(40.dp))

            Text(
                text = title,
                color = NeonYellow,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = subtitle,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(48.dp))

            // Barra de Progresso Estilizada
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "PROGRESSO",
                        color = Color.Gray,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        "${(progress * 100).toInt()}%",
                        color = NeonYellow,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black
                    )
                }
                Spacer(Modifier.height(12.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(14.dp)
                        .clip(CircleShape)
                        .border(1.dp, NeonYellow.copy(alpha = 0.2f), CircleShape),
                    color = NeonYellow,
                    trackColor = Color(0xFF1A1A1A)
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = "ETAPA $current DE $total",
                color = Color.Gray,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
