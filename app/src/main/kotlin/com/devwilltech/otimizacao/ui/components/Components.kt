package com.devwilltech.otimizacao.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devwilltech.otimizacao.R
import com.devwilltech.otimizacao.ui.theme.*

@Composable
fun WillTechHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp, bottom = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(110.dp)
                .clip(CircleShape)
                .border(3.dp, NeonYellow, CircleShape)
        ) {
            Image(
                painter = painterResource(id = R.drawable.app_logo),
                contentDescription = "Logo",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
        Spacer(Modifier.height(16.dp))
        Text(
            text = "@DEVWILLTECH",
            color = NeonYellow,
            fontSize = 22.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 2.sp
        )
        Text(
            text = "OTIMIZAÇÃO EXTREMA",
            color = Color.White,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
    }
}

@Composable
fun NeonButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    showLock: Boolean = false,
    isLoading: Boolean = false,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = enabled && !isLoading,
        modifier = modifier,
        shape = RoundedCornerShape(32.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (enabled) NeonYellow else Color(0xFF222222),
            contentColor = Color.Black
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.size(28.dp), color = Color.Black, strokeWidth = 3.dp)
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (showLock) {
                    Text("LOCKED ", fontWeight = FontWeight.Black, fontSize = 12.sp)
                }
                Text(text, fontWeight = FontWeight.Black, fontSize = 16.sp, letterSpacing = 0.5.sp)
            }
        }
    }
}

@Composable
fun GhostDangerButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled && !isLoading,
        modifier = modifier,
        shape = RoundedCornerShape(28.dp),
        border = BorderStroke(2.dp, if (enabled) ErrorRed else Color.DarkGray),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed)
    ) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = ErrorRed, strokeWidth = 2.dp)
        } else {
            Text(text, fontWeight = FontWeight.Black, fontSize = 14.sp, letterSpacing = 0.5.sp)
        }
    }
}

@Composable
fun ExecutionProgressBar(current: Int, total: Int) {
    val progress = if (total > 0) current.toFloat() / total else 0f
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("OTIMIZANDO SISTEMA...", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Black)
            Text("${(progress * 100).toInt()}%", color = NeonYellow, fontSize = 14.sp, fontWeight = FontWeight.Black)
        }
        Spacer(Modifier.height(10.dp))
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier.fillMaxWidth().height(12.dp).clip(CircleShape).border(1.dp, NeonYellow.copy(alpha = 0.3f), CircleShape),
            color = NeonYellow,
            trackColor = Color(0xFF1A1A1A)
        )
    }
}
