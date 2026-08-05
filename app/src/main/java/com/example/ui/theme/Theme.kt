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
    primary = Color(0xFFFF6D00),
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
        AppTheme.SAKURA_PINK -> lightColorScheme(
            primary = SakuraPrimary, background = SakuraBackground, surface = SakuraSurface, onPrimary = Color.White
        )
        AppTheme.LATTE -> lightColorScheme(
            primary = LattePrimary, background = LatteBackground, surface = LatteSurface, onPrimary = Color.White
        )
        AppTheme.CAPPUCCINO -> lightColorScheme(
            primary = CappuccinoPrimary, background = CappuccinoBackground, surface = CappuccinoSurface, onPrimary = Color.White
        )
        AppTheme.ESPRESSO -> darkColorScheme(
            primary = EspressoPrimary, background = EspressoBackground, surface = EspressoSurface, onPrimary = Color.Black
        )
        AppTheme.MINT -> lightColorScheme(
            primary = MintPrimary, background = MintBackground, surface = MintSurface, onPrimary = Color.White
        )
        AppTheme.MATCHA -> lightColorScheme(
            primary = MatchaPrimary, background = MatchaBackground, surface = MatchaSurface, onPrimary = Color.White
        )
        AppTheme.LAVENDER -> lightColorScheme(
            primary = LavenderPrimary, background = LavenderBackground, surface = LavenderSurface, onPrimary = Color.White
        )
        AppTheme.PEACH -> lightColorScheme(
            primary = PeachPrimary, background = PeachBackground, surface = PeachSurface, onPrimary = Color.White
        )
        AppTheme.OCEAN -> lightColorScheme(
            primary = OceanPrimary, background = OceanBackground, surface = OceanSurface, onPrimary = Color.White
        )
        AppTheme.CORAL -> lightColorScheme(
            primary = CoralPrimary, background = CoralBackground, surface = CoralSurface, onPrimary = Color.White
        )
        AppTheme.VANILLA -> lightColorScheme(
            primary = VanillaPrimary, background = VanillaBackground, surface = VanillaSurface, onPrimary = Color.Black
        )
        AppTheme.SKY_BLUE -> lightColorScheme(
            primary = SkyBluePrimary, background = SkyBlueBackground, surface = SkyBlueSurface, onPrimary = Color.White
        )
        AppTheme.MIDNIGHT -> darkColorScheme(
            primary = MidnightPrimary, background = MidnightBackground, surface = MidnightSurface, onPrimary = Color.White
        )
        AppTheme.GRAPHITE -> darkColorScheme(
            primary = GraphitePrimary, background = GraphiteBackground, surface = GraphiteSurface, onPrimary = Color.White
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
