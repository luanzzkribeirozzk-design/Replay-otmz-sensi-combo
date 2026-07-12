package com.devwilltech.otimizacao.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devwilltech.otimizacao.ShizukuStatus
import com.devwilltech.otimizacao.ui.theme.*

@Composable
fun ShizukuStatusCard(
    status: ShizukuStatus,
    onRequestPermission: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (statusLabel, title, subtitle, color) = when (status) {
        ShizukuStatus.CHECKING -> StatusInfo("...", "VERIFICANDO", "Aguarde...", Color.Gray)
        ShizukuStatus.NOT_INSTALLED -> StatusInfo("!", "SEM SHIZUKU", "Instale na Play Store", ErrorRed)
        ShizukuStatus.NOT_RUNNING -> StatusInfo("!", "INATIVO", "Inicie o serviço no Shizuku", WarnOrange)
        ShizukuStatus.PERMISSION_NEEDED -> StatusInfo("?", "PERMISSÃO", "Autorize no Shizuku", NeonYellow)
        ShizukuStatus.READY -> StatusInfo("OK", "SISTEMA PRONTO", "Conectado com Sucesso", SuccessGreen)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0A0A0A))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(color.copy(alpha = 0.2f))
                        .border(1.5.dp, color, CircleShape)
                ) {
                    Text(
                        text = statusLabel,
                        color = color,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                Spacer(Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        color = color,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = subtitle,
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (status == ShizukuStatus.PERMISSION_NEEDED) {
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = onRequestPermission,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonYellow, contentColor = Color.Black)
                ) {
                    Text("AUTORIZAR AGORA", fontWeight = FontWeight.Black, fontSize = 15.sp)
                }
            }
        }
    }
}

private data class StatusInfo(
    val statusLabel: String,
    val title: String,
    val subtitle: String,
    val color: Color
)
