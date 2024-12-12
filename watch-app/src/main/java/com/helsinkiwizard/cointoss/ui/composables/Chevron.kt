package com.helsinkiwizard.cointoss.ui.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.wear.compose.material.Icon
import com.helsinkiwizard.core.R
import com.helsinkiwizard.core.theme.WhiteTransparent

const val OFFSET_ANIMATION_DURATION = 750
const val OFFSET_ANIMATION_TARGET_VALUE = 5f
const val QUARTER_WIDTH_DIVISOR = 4

@Composable
fun Chevron(showChevron: Boolean) {
    val infiniteTransition = rememberInfiniteTransition()
    val offsetAnimation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = OFFSET_ANIMATION_TARGET_VALUE,
        animationSpec = infiniteRepeatable(
            animation = tween(OFFSET_ANIMATION_DURATION, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    AnimatedVisibility(
        visible = showChevron,
        modifier = Modifier
            .fillMaxSize()
            .zIndex(1f),
        enter = slideInHorizontally(
            initialOffsetX = { fullWidth -> fullWidth / QUARTER_WIDTH_DIVISOR },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        ),
        exit = slideOutHorizontally(
            targetOffsetX = { fullWidth -> fullWidth / QUARTER_WIDTH_DIVISOR },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    ) {
        Box(
            contentAlignment = Alignment.CenterEnd,
            modifier = Modifier
                .fillMaxSize()
                .offset(offsetAnimation.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.chevron_right),
                contentDescription = "",
                tint = Black,
                modifier = Modifier.background(
                    Brush.radialGradient(
                        colors = listOf(
                            WhiteTransparent,
                            WhiteTransparent,
                            WhiteTransparent,
                            Color.Transparent
                        )
                    )
                )
            )
        }
    }
}
