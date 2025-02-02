package com.applivery.android.sdk.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
internal fun AppliveryTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ColorScheme(
            primary = colorPrimary,
            onPrimary = colorOnPrimary,
            primaryContainer = colorPrimaryContainer,
            onPrimaryContainer = colorOnPrimaryContainer,
            inversePrimary = colorInversePrimary,
            secondary = colorSecondary,
            onSecondary = colorOnSecondary,
            secondaryContainer = colorSecondaryContainer,
            onSecondaryContainer = colorOnSecondaryContainer,
            tertiary = colorTertiary,
            onTertiary = colorOnTertiary,
            tertiaryContainer = colorTertiaryContainer,
            onTertiaryContainer = colorOnTertiaryContainer,
            error = colorError,
            onError = colorOnError,
            surface = colorSurface,
            onSurface = colorOnSurface,
            surfaceVariant = colorSurfaceVariant,
            onSurfaceVariant = colorOnSurfaceVariant,
            inverseSurface = colorInverseSurface,
            surfaceTint = colorSurfaceTint,
            inverseOnSurface = colorInverseOnSurface,
            background = colorBackground,
            onBackground = colorOnBackground,
            errorContainer = colorErrorContainer,
            onErrorContainer = colorOnErrorContainer,
            outline = colorOutline,
            outlineVariant = colorOutlineVariant,
            scrim = colorScrim,
            surfaceContainer = colorSurface,
            surfaceContainerHigh = colorSurface,
            surfaceContainerLow = colorSurface,
            surfaceContainerHighest = colorSurface,
            surfaceContainerLowest = colorSurface,
            surfaceDim = colorSurface,
            surfaceBright = colorSurface
        ),
        content = content
    )
}
