package com.applivery.android.sdk.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import com.applivery.android.sdk.R


/*Material Colors*/
internal val colorPrimary @Composable get() = colorResource(id = R.color.applivery_primary_color)
internal val colorOnPrimary = Color.White
internal val colorPrimaryContainer @Composable get() = colorPrimary
internal val colorOnPrimaryContainer = colorOnPrimary
internal val colorSecondary @Composable get() = colorResource(id = R.color.applivery_accent_color)
internal val colorOnSecondary = Color.White
internal val colorSecondaryContainer @Composable get() = colorSecondary
internal val colorOnSecondaryContainer = colorOnSecondary
internal val colorTertiary @Composable get() = colorPrimary
internal val colorOnTertiary = Color.White
internal val colorTertiaryContainer @Composable get() = colorTertiary
internal val colorOnTertiaryContainer = colorOnTertiary
internal val colorError = Color(0xFFEF4444)
internal val colorErrorContainer = Color.White
internal val colorOnError = colorErrorContainer
internal val colorOnErrorContainer = colorError
internal val colorBackground = Color(0xFFF8F8F8)
internal val colorOnBackground @Composable get() = colorResource(id = R.color.applivery_foreground_color)
internal val colorSurface = colorBackground
internal val colorOnSurface @Composable get() = colorOnBackground
internal val colorSurfaceVariant = colorBackground
internal val colorOnSurfaceVariant @Composable get() = colorOnBackground
internal val colorSurfaceTint = colorBackground
internal val colorInverseOnSurface @Composable get() = colorOnBackground
internal val colorInverseSurface = colorBackground
internal val colorInversePrimary = colorOnPrimary
internal val colorOutline @Composable get() = colorPrimary
internal val colorOutlineVariant @Composable get() = colorPrimary
internal val colorScrim = Color.Black