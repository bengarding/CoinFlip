package com.helsinkiwizard.cointoss.ui.composable

import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import com.helsinkiwizard.core.CoreConstants.VALUE_UNDEFINED

/**
 * Applies an additional size to the layout and centers the content within the newly sized layout.
 * This is useful for a list of items with a dynamic size (such as changing font weight), to
 * maintain consistent spacing and alignment despite content changes.
 *
 * @param additionalWidth The additional width in pixels to be added to the layout's original width.
 * @param additionalHeight The additional height in pixels to be added to the layout's original height.
 * @param widthWithOffset The calculated width with the offset. The value must initially be [VALUE_UNDEFINED] so the
 * width can be calculated the first time it is composed.
 * @param heightWithOffset The calculated height with the offset. The value must initially be [VALUE_UNDEFINED] so the
 * height can be calculated the first time it is composed.
 * @param updateWidthWithOffset A lambda function to update the width with the calculated offset.
 * @param updateHeightWithOffset A lambda function to update the height with the calculated offset.
 */
fun Modifier.additionalLayoutSize(
    additionalWidth: Int = VALUE_UNDEFINED,
    additionalHeight: Int = VALUE_UNDEFINED,
    widthWithOffset: Int = VALUE_UNDEFINED,
    heightWithOffset: Int = VALUE_UNDEFINED,
    updateWidthWithOffset: (Int) -> Unit = {},
    updateHeightWithOffset: (Int) -> Unit = {}
) = layout { measurable, constraints ->
    val placeable = measurable.measure(constraints)

    if (widthWithOffset == VALUE_UNDEFINED && additionalWidth != VALUE_UNDEFINED) {
        updateWidthWithOffset(placeable.width + additionalWidth)
    }

    if (heightWithOffset == VALUE_UNDEFINED && additionalHeight != VALUE_UNDEFINED) {
        updateHeightWithOffset(placeable.height + additionalHeight)
    }

    val horizontalCenterPosition = if (widthWithOffset != VALUE_UNDEFINED) {
        (widthWithOffset - placeable.width) / 2
    } else {
        0
    }

    val verticalCenterPosition = if (heightWithOffset != VALUE_UNDEFINED) {
        (heightWithOffset - placeable.height) / 2
    } else {
        0
    }

    val finalWidth = widthWithOffset.takeIf { it != VALUE_UNDEFINED } ?: placeable.width
    val finalHeight = heightWithOffset.takeIf { it != VALUE_UNDEFINED } ?: placeable.height

    layout(finalWidth, finalHeight) {
        placeable.placeRelative(horizontalCenterPosition, verticalCenterPosition)
    }
}
