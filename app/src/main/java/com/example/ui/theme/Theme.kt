package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.data.model.AppTheme

private fun createPastelLightScheme(
    primary: Color,
    background: Color,
    surface: Color
) = lightColorScheme(
    primary = primary,
    secondary = primary,
    tertiary = primary,
    background = background,
    surface = surface,
    surfaceVariant = surface,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFF1C1C1E),
    onSurface = Color(0xFF1C1C1E),
    onSurfaceVariant = Color(0xFF555558)
)

private fun createPastelDarkScheme(
    primary: Color,
    background: Color,
    surface: Color
) = darkColorScheme(
    primary = primary,
    secondary = primary,
    tertiary = primary,
    background = background,
    surface = surface,
    surfaceVariant = surface,
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = Color(0xFFFAFAFC),
    onSurface = Color(0xFFFAFAFC),
    onSurfaceVariant = Color(0xFFCCCCCC)
)

private val ClickitDarkColorScheme = darkColorScheme(
    primary = ClickitOrangeAccent,
    secondary = ClickitGoldAccent,
    tertiary = ClickitOrangeAccent,
    background = ClickitDarkBackground,
    surface = ClickitDarkSurface,
    surfaceVariant = ClickitDarkSurfaceVariant,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = ClickitTextPrimary,
    onSurface = ClickitTextPrimary,
    onSurfaceVariant = ClickitTextSecondary
)

private val ClickitLightColorScheme = lightColorScheme(
    primary = Color(0xFFF27D26),
    secondary = Color(0xFFFFB300),
    tertiary = Color(0xFFFF8F00),
    background = Color(0xFFFAFAFC),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFF0F0F5),
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color(0xFF1C1C1E),
    onSurface = Color(0xFF1C1C1E),
    onSurfaceVariant = Color(0xFF6E6E73)
)

@Composable
fun ClickitTheme(
    themeOption: AppTheme = AppTheme.DARK,
    systemInDark: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val colorScheme = when (themeOption) {
        AppTheme.DARK -> ClickitDarkColorScheme
        AppTheme.LIGHT -> ClickitLightColorScheme
        AppTheme.SYSTEM -> if (systemInDark) ClickitDarkColorScheme else ClickitLightColorScheme
        AppTheme.MATERIAL_YOU -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (systemInDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            } else ClickitDarkColorScheme
        }
        AppTheme.SAKURA_PINK -> createPastelLightScheme(SakuraPrimary, SakuraBackground, SakuraSurface)
        AppTheme.LATTE -> createPastelLightScheme(LattePrimary, LatteBackground, LatteSurface)
        AppTheme.CAPPUCCINO -> createPastelLightScheme(CappuccinoPrimary, CappuccinoBackground, CappuccinoSurface)
        AppTheme.ESPRESSO -> createPastelDarkScheme(EspressoPrimary, EspressoBackground, EspressoSurface)
        AppTheme.MINT -> createPastelLightScheme(MintPrimary, MintBackground, MintSurface)
        AppTheme.MATCHA -> createPastelLightScheme(MatchaPrimary, MatchaBackground, MatchaSurface)
        AppTheme.LAVENDER -> createPastelLightScheme(LavenderPrimary, LavenderBackground, LavenderSurface)
        AppTheme.PEACH -> createPastelLightScheme(PeachPrimary, PeachBackground, PeachSurface)
        AppTheme.OCEAN -> createPastelLightScheme(OceanPrimary, OceanBackground, OceanSurface)
        AppTheme.CORAL -> createPastelLightScheme(CoralPrimary, CoralBackground, CoralSurface)
        AppTheme.VANILLA -> createPastelLightScheme(VanillaPrimary, VanillaBackground, VanillaSurface)
        AppTheme.SKY_BLUE -> createPastelLightScheme(SkyBluePrimary, SkyBlueBackground, SkyBlueSurface)
        AppTheme.MIDNIGHT -> createPastelDarkScheme(MidnightPrimary, MidnightBackground, MidnightSurface)
        AppTheme.GRAPHITE -> createPastelDarkScheme(GraphitePrimary, GraphiteBackground, GraphiteSurface)
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
