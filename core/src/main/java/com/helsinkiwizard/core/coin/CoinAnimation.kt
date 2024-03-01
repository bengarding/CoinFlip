package com.helsinkiwizard.core.coin

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.helsinkiwizard.core.R
import java.util.Random
import kotlin.math.abs

private const val QUARTER_ROTATION = 90f
private const val HALF_ROTATION = 180
private const val THREE_QUARTER_ROTATION = 270f
private const val FULL_ROTATION = 360
private const val CAMERA_DISTANCE = 100f

private var rotationAmount = 1
private var currentSide = CoinSide.HEADS
private var nextSide = CoinSide.TAILS

var flipCount = 0

@Composable
fun CoinAnimation(
    coinType: CoinType,
    modifier: Modifier,
    startFlipping: Boolean? = null,
    onStartFlipping: (() -> Unit)? = null,
    onFlip: (() -> Unit)? = null
) {
    var flipping by remember { mutableStateOf(startFlipping == true) }

    LaunchedEffect(startFlipping) {
        if (startFlipping == true) {
            flipCount++
            randomizeRotationAmount()
            flipping = !flipping
            onStartFlipping?.invoke()
        }
    }

    val valueFloat: Float by animateFloatAsState(
        targetValue = if (flipping) 0f else 1f,
        animationSpec = tween(
            durationMillis = 3000,
            easing = LinearOutSlowInEasing
        ),
        label = "flip animation"
    )

    Box(
        modifier = modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                flipCount++
                randomizeRotationAmount()
                flipping = !flipping
                onFlip?.invoke()
            },
        contentAlignment = Alignment.Center
    ) {
        FlipAnimation(
            rotationY = valueFloat * rotationAmount,
            front = {
                if (currentSide == CoinSide.HEADS) Heads(coinType.heads) else Tails(coinType.tails)
            }, back = {
                if (currentSide == CoinSide.HEADS) Tails(coinType.tails) else Heads(coinType.heads)
            })
    }
}

@Composable
fun Heads(headsRes: Int) {
    Image(
        painter = painterResource(headsRes),
        contentDescription = stringResource(R.string.heads),
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
fun Tails(tailsRes: Int) {
    Image(
        painter = painterResource(tailsRes),
        contentDescription = stringResource(R.string.tails),
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
fun FlipAnimation(
    rotationY: Float = 0f,
    front: @Composable () -> Unit,
    back: @Composable () -> Unit
) {
    fun isFlipped() = abs(rotationY) % FULL_ROTATION > QUARTER_ROTATION
            && abs(rotationY) % FULL_ROTATION < THREE_QUARTER_ROTATION

    if (isFlipped()) {
        Box(
            Modifier.graphicsLayer(
                rotationY = rotationY - HALF_ROTATION,
                cameraDistance = CAMERA_DISTANCE
            )
        ) {
            back()
        }
    } else {
        Box(
            Modifier.graphicsLayer(
                rotationY = rotationY,
                cameraDistance = CAMERA_DISTANCE
            )
        ) {
            front()
        }
    }
}

/**
 * Randomizes the rotation amount to be used for each coin flip.
 * There is a bug in Kotlin's Random class where it repeats itself every time the app loads,
 * so use java.util.Random here and add 10 to increase the number of rotations per flip.
 * https://issuetracker.google.com/issues/234631055
 */
private fun randomizeRotationAmount() {
    val randomFlips = Random().nextInt(8) + 10
    rotationAmount = randomFlips * HALF_ROTATION

    currentSide = nextSide
    nextSide = if (randomFlips % 2 == 0) CoinSide.HEADS else CoinSide.TAILS
}

enum class CoinSide {
    HEADS, TAILS
}
