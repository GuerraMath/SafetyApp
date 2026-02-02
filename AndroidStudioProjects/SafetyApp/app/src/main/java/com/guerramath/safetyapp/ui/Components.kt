package com.guerramath.safetyapp.ui

import android.os.Build
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Fundo animado que gera o contraste necessário para o efeito Glowglass.
 * Cores inspiradas em cockpits modernos e visão noturna (Safety).
 */
@Composable
fun AnimatedSafetyBackground(content: @Composable () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "bg_transition")

    val color1 by infiniteTransition.animateColor(
        initialValue = Color(0xFF0D1B2A), // Azul Marinho Profundo
        targetValue = Color(0xFF1B263B),
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "color_shift_1"
    )

    val color2 by infiniteTransition.animateColor(
        initialValue = Color(0xFF1B263B),
        targetValue = Color(0xFF0D1B2A),
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "color_shift_2"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(color1, color2, Color.Black)
                )
            )
    ) {
        content()
    }
}

/**
 * Card Neo-Brutalista com Glassmorphism.
 * Design de alto contraste para aplicações B2B de aviação.
 */
@Composable
fun SafetyResponsiveCard(
    title: String,
    status: String,
    description: String = "STATUS OPERACIONAL: NORMAL",
    modifier: Modifier = Modifier
) {
    val canRenderBlur = Build.VERSION.SDK_INT >= 31

    Box(
        modifier = modifier
            .padding(16.dp)
            .widthIn(max = 600.dp)
            .fillMaxWidth()
            .drawBehind {
                // Sombra Neo-Brutalista (Mantemos firme)
                drawRoundRect(
                    color = Color.Black,
                    topLeft = Offset(12f, 12f),
                    size = size,
                    cornerRadius = CornerRadius(12.dp.toPx())
                )
            }
    ) {
        // CAMADA 1: O "Vidro" (Só esta camada recebe o blur)
        Box(
            modifier = Modifier
                .matchParentSize()
                .border(2.dp, Color.Black, RoundedCornerShape(12.dp))
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White.copy(alpha = 0.15f))
                .then(
                    // Reduzimos de 25.dp para 12.dp para não virar um borrão
                    if (canRenderBlur) Modifier.blur(12.dp) else Modifier
                )
        )

        // CAMADA 2: O Conteúdo (Fica por cima e SEMPRE nítido)
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = title.uppercase(),
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Black
                )

                Surface(
                    color = Color.Cyan,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = status,
                        color = Color.Black,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.9f)
            )
        }
    }
}
/**
 * Grid de visualização para o Dashboard da Sentra.
 */
@Composable
fun SafetyDashboardContent() {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 300.dp),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item { SafetyResponsiveCard("Motores", "NORMAL") }
        item { SafetyResponsiveCard("Sistemas de Voo", "ATIVO") }
        item { SafetyResponsiveCard("Combustível", "92%") }
        item { SafetyResponsiveCard("Conectividade", "SATCOM") }
    }
}