package com.devwilltech.otimizacao.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devwilltech.otimizacao.data.CommandResult
import com.devwilltech.otimizacao.ui.theme.NeonYellow
import com.devwilltech.otimizacao.ui.theme.SuccessGreen

@Composable
fun ReportPanel(
    results: List<CommandResult>,
    onDismiss: () -> Unit
) {
    val successCount = results.count { it.success }
    val totalCount = results.size

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(2.dp, SuccessGreen, RoundedCornerShape(28.dp)),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0A0A0A))
    ) {
        Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "OTIMIZAÇÃO COMPLETA",
                color = SuccessGreen,
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )
            
            Spacer(Modifier.height(20.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "$successCount",
                    color = SuccessGreen,
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = " / $totalCount",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Text("COMANDOS APLICADOS COM SUCESSO", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NeonYellow, contentColor = Color.Black)
            ) {
                Text("CONCLUIR", fontWeight = FontWeight.Black, fontSize = 16.sp)
            }
        }
    }
}
