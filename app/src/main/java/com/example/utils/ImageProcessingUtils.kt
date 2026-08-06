package com.example.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.compose.ui.graphics.ColorMatrix as ComposeColorMatrix
import com.example.data.model.FilmFilterCatalog
import com.example.ui.screens.EditState
import java.io.File
import java.io.FileOutputStream

object ImageProcessingUtils {

    /**
     * Build an Android graphics ColorMatrix for Bitmap processing or Compose ColorFilter.
     */
    fun buildAndroidColorMatrix(
        filterName: String,
        exposure: Float = 0f,
        contrast: Float = 0f,
        saturation: Float = 0f,
        temperature: Float = 0f,
        tint: Float = 0f
    ): ColorMatrix {
        val matrix = ColorMatrix()

        val preset = FilmFilterCatalog.filters.find { it.name.equals(filterName, ignoreCase = true) }
        val filterIntensity = preset?.defaultIntensity ?: 0.8f
        val filterWarmth = preset?.defaultTemperature ?: 0f

        // B&W or Sepia or custom presets
        val totalSat = when (filterName.lowercase()) {
            "b&w" -> 0f
            "minimal" -> 0.4f
            "velvia" -> 1.4f
            else -> (1f + saturation) * (0.5f + filterIntensity * 0.5f)
        }
        matrix.setSaturation(totalSat)

        // Special Color Matrices for Sepia / B&W
        if (filterName.equals("sepia", ignoreCase = true)) {
            val sepiaMatrix = ColorMatrix(floatArrayOf(
                0.393f, 0.769f, 0.189f, 0f, 0f,
                0.349f, 0.686f, 0.168f, 0f, 0f,
                0.272f, 0.534f, 0.131f, 0f, 0f,
                0f,     0f,     0f,     1f, 0f
            ))
            matrix.postConcat(sepiaMatrix)
        }

        // 2. Exposure / Brightness (-1.0 to 1.0 -> scale)
        val expOffset = exposure * 50f
        if (expOffset != 0f) {
            val expMatrix = ColorMatrix(floatArrayOf(
                1f, 0f, 0f, 0f, expOffset,
                0f, 1f, 0f, 0f, expOffset,
                0f, 0f, 1f, 0f, expOffset,
                0f, 0f, 0f, 1f, 0f
            ))
            matrix.postConcat(expMatrix)
        }

        // 3. Contrast adjustment
        val c = (1f + contrast) * (0.8f + filterIntensity * 0.4f)
        if (c != 1f) {
            val cOffset = 128f * (1f - c)
            val contrastMatrix = ColorMatrix(floatArrayOf(
                c, 0f, 0f, 0f, cOffset,
                0f, c, 0f, 0f, cOffset,
                0f, 0f, c, 0f, cOffset,
                0f, 0f, 0f, 1f, 0f
            ))
            matrix.postConcat(contrastMatrix)
        }

        // 4. Temperature & Tint (Warmth / Coolness / Green-Magenta)
        val totalWarmth = temperature + filterWarmth
        if (totalWarmth != 0f || tint != 0f) {
            val rOffset = totalWarmth * 25f + tint * 12f
            val bOffset = -totalWarmth * 25f - tint * 12f
            val tempMatrix = ColorMatrix(floatArrayOf(
                1f, 0f, 0f, 0f, rOffset,
                0f, 1f, 0f, 0f, 0f,
                0f, 0f, 1f, 0f, bOffset,
                0f, 0f, 0f, 1f, 0f
            ))
            matrix.postConcat(tempMatrix)
        }

        return matrix
    }

    /**
     * Convert Android ColorMatrix array to Compose ColorMatrix for live UI preview.
     */
    fun buildComposeColorMatrix(
        filterName: String,
        exposure: Float = 0f,
        contrast: Float = 0f,
        saturation: Float = 0f,
        temperature: Float = 0f,
        tint: Float = 0f
    ): ComposeColorMatrix {
        val androidMatrix = buildAndroidColorMatrix(filterName, exposure, contrast, saturation, temperature, tint)
        return ComposeColorMatrix(androidMatrix.array)
    }

    /**
     * Render a new Bitmap from source file with all edits and filter applied.
     */
    fun renderEditedBitmap(sourceFile: File, editState: EditState): Bitmap? {
        if (!sourceFile.exists()) return null
        val originalBitmap = BitmapFactory.decodeFile(sourceFile.absolutePath) ?: return null
        val mutableBitmap = Bitmap.createBitmap(
            originalBitmap.width,
            originalBitmap.height,
            Bitmap.Config.ARGB_8888
        )

        val canvas = Canvas(mutableBitmap)
        val paint = Paint().apply {
            isAntiAlias = true
            colorFilter = ColorMatrixColorFilter(
                buildAndroidColorMatrix(
                    filterName = editState.filterName,
                    exposure = editState.exposure,
                    contrast = editState.contrast,
                    saturation = editState.saturation,
                    temperature = editState.temperature,
                    tint = editState.tint
                )
            )
        }

        canvas.drawBitmap(originalBitmap, 0f, 0f, paint)
        return mutableBitmap
    }

    /**
     * Save/Export Bitmap to Android System MediaStore (Pictures/Clickit) so it appears in device photo gallery.
     */
    fun exportToSystemGallery(context: Context, bitmap: Bitmap, filenamePrefix: String = "CLICKIT"): String? {
        val filename = "${filenamePrefix}_${System.currentTimeMillis()}.jpg"
        var savedFilePath: String? = null

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}/Clickit")
                    put(MediaStore.MediaColumns.IS_PENDING, 1)
                }

                val resolver = context.contentResolver
                val uri: Uri? = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

                uri?.let {
                    resolver.openOutputStream(it)?.use { stream ->
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 95, stream)
                    }
                    contentValues.clear()
                    contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                    resolver.update(it, contentValues, null, null)

                    // Also save local file copy for app DB access if needed
                    val localDir = File(context.filesDir, "photos")
                    if (!localDir.exists()) localDir.mkdirs()
                    val localFile = File(localDir, filename)
                    FileOutputStream(localFile).use { out ->
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 95, out)
                    }
                    savedFilePath = localFile.absolutePath
                }
            } else {
                val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                val clickitDir = File(imagesDir, "Clickit")
                if (!clickitDir.exists()) clickitDir.mkdirs()
                val imageFile = File(clickitDir, filename)
                FileOutputStream(imageFile).use { stream ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 95, stream)
                }
                savedFilePath = imageFile.absolutePath

                val values = ContentValues().apply {
                    put(MediaStore.Images.Media.DATA, imageFile.absolutePath)
                    put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                }
                context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return savedFilePath
    }
}
