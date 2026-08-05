package com.example.data.model

enum class FilterGroup(val displayName: String) {
    FILM("Film & Retro"),
    CREATIVE("Creative & Mood"),
    FUJIFILM("Fujifilm Inspired"),
    MONO("Monochrome")
}

data class FilterPreset(
    val name: String,
    val group: FilterGroup,
    val description: String,
    val colorOverlayHex: String,
    val defaultIntensity: Float = 0.8f,
    val defaultGrain: Float = 0.15f,
    val defaultTemperature: Float = 0.0f
)

object FilmFilterCatalog {
    val filters = listOf(
        FilterPreset("Natural", FilterGroup.CREATIVE, "True to life color rendering", "#00000000", 0f, 0f, 0f),
        FilterPreset("Classic Film", FilterGroup.FILM, "Timeless 35mm film stock with soft grain", "#1AFFE0B2", 0.8f, 0.2f, 0.1f),
        FilterPreset("Vintage Film", FilterGroup.FILM, "Warm muted retro tones with gentle fade", "#26FFCC80", 0.85f, 0.25f, 0.2f),
        FilterPreset("Retro Warm", FilterGroup.FILM, "Golden nostalgic warmth", "#26FFA726", 0.9f, 0.18f, 0.3f),
        FilterPreset("Retro Cool", FilterGroup.FILM, "Cool cyan film shadow aesthetics", "#2629B6F6", 0.8f, 0.15f, -0.3f),
        FilterPreset("Disposable", FilterGroup.FILM, "90s party disposable camera punch", "#1FFF7043", 0.95f, 0.35f, 0.15f),
        FilterPreset("Instant Camera", FilterGroup.FILM, "Polaroid-style high contrast and soft whites", "#26FFF176", 0.85f, 0.2f, 0.1f),
        FilterPreset("Dreamy", FilterGroup.CREATIVE, "Etherial bloom with glowing highlights", "#26F48FB1", 0.75f, 0.05f, 0.1f),
        FilterPreset("Matte", FilterGroup.CREATIVE, "Elevated shadows and velvety flat blacks", "#1A263238", 0.8f, 0.12f, 0.0f),
        FilterPreset("Coffee", FilterGroup.CREATIVE, "Rich brown espresso depth", "#336D4C41", 0.9f, 0.15f, 0.25f),
        FilterPreset("Minimal", FilterGroup.CREATIVE, "Clean high-clarity muted saturation", "#10000000", 0.7f, 0.0f, -0.05f),
        FilterPreset("Travel", FilterGroup.CREATIVE, "Vibrant sky blues and lush greenery", "#1F00B0FF", 0.85f, 0.08f, 0.05f),
        FilterPreset("Street", FilterGroup.CREATIVE, "Gritty high-contrast urban shadows", "#2637474F", 0.9f, 0.3f, -0.1f),
        FilterPreset("Portrait", FilterGroup.CREATIVE, "Soft skin tones and creamy bokeh glow", "#1AFFAB91", 0.8f, 0.05f, 0.1f),
        FilterPreset("Cinematic", FilterGroup.CREATIVE, "Teal and orange Hollywood grade", "#2600838F", 0.85f, 0.1f, -0.15f),
        FilterPreset("Golden Hour", FilterGroup.CREATIVE, "Sunset radiance and amber glow", "#33FF8F00", 0.9f, 0.12f, 0.4f),
        FilterPreset("Moody", FilterGroup.CREATIVE, "Dark dramatic shadows with rich highlights", "#33121212", 0.85f, 0.2f, -0.2f),
        FilterPreset("Pastel", FilterGroup.CREATIVE, "Soft pink and lavender dreamy tones", "#26F8BBD0", 0.75f, 0.05f, 0.05f),
        FilterPreset("B&W", FilterGroup.MONO, "Classic high contrast silver halide black & white", "#FF000000", 1.0f, 0.25f, 0.0f),
        FilterPreset("Sepia", FilterGroup.MONO, "Antique warm sepia tone photo", "#338D6E63", 0.9f, 0.2f, 0.3f),
        // Fujifilm Inspired
        FilterPreset("Classic Chrome", FilterGroup.FUJIFILM, "Soft colors and rich shadow tones", "#203E2723", 0.85f, 0.15f, -0.1f),
        FilterPreset("Provia", FilterGroup.FUJIFILM, "Standard crisp color accuracy", "#150288D1", 0.8f, 0.05f, 0.0f),
        FilterPreset("Velvia", FilterGroup.FUJIFILM, "Ultra-vibrant saturation for landscapes", "#25E65100", 0.95f, 0.08f, 0.1f),
        FilterPreset("Astia", FilterGroup.FUJIFILM, "Soft portrait skin tones with vivid skies", "#1FF8BBD0", 0.85f, 0.05f, 0.05f),
        FilterPreset("Eterna", FilterGroup.FUJIFILM, "Cinematic low-saturation photo aesthetic", "#20263238", 0.8f, 0.1f, -0.05f),
        FilterPreset("Nostalgic Neg", FilterGroup.FUJIFILM, "Warm amber highlights and rich cyan shadows", "#28FFB300", 0.9f, 0.22f, 0.2f)
    )
}

enum class CameraMode(val label: String) {
    PHOTO("Photo"),
    PRO("Pro Mode"),
    PORTRAIT("Portrait"),
    NIGHT("Night"),
    MACRO("Macro"),
    BURST("Burst")
}

enum class AspectRatioOption(val label: String, val ratio: Float) {
    RATIO_1_1("1:1", 1.0f),
    RATIO_3_4("3:4", 3f / 4f),
    RATIO_4_3("4:3", 4f / 3f),
    RATIO_16_9("16:9", 16f / 9f),
    RATIO_9_16("9:16", 9f / 16f),
    RATIO_18_9("18:9", 18f / 9f),
    RATIO_21_9("21:9", 21f / 9f)
}

enum class GridType(val label: String) {
    NONE("Off"),
    RULE_OF_THIRDS("3x3"),
    GOLDEN_RATIO("Golden"),
    SQUARE("1:1 Square")
}

enum class AppTheme(val displayName: String, val primaryHex: Long) {
    DARK("Dark Minimal", 0xFF1C1B1F),
    LIGHT("Light Clean", 0xFFF6F6F6),
    SYSTEM("System Default", 0xFF6200EE),
    MATERIAL_YOU("Material You", 0xFF6750A4),
    SAKURA_PINK("Sakura Pink", 0xFFFFB7B2),
    LATTE("Latte", 0xFFE0C097),
    CAPPUCCINO("Cappuccino", 0xFFC8A2C8),
    ESPRESSO("Espresso", 0xFF4A3E3D),
    MINT("Mint", 0xFFB5EAD7),
    MATCHA("Matcha", 0xFF95B8A6),
    LAVENDER("Lavender", 0xFFE2F0CB),
    PEACH("Peach", 0xFFFFDAC1),
    OCEAN("Ocean", 0xFF81D4FA),
    CORAL("Coral", 0xFFFF8A80),
    VANILLA("Vanilla", 0xFFFFF59D),
    SKY_BLUE("Sky Blue", 0xFFB3E5FC),
    MIDNIGHT("Midnight", 0xFF121212),
    GRAPHITE("Graphite", 0xFF212121)
}
