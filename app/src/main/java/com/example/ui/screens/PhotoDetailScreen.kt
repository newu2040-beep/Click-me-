package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.example.data.db.PhotoDao
import com.example.data.model.PhotoItem
import com.example.ui.components.GlassCard
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoDetailScreen(
    photoId: Long,
    photoDao: PhotoDao,
    onEditPhoto: (Long) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var photo by remember { mutableStateOf<PhotoItem?>(null) }
    var showExifSheet by remember { mutableStateOf(false) }

    LaunchedEffect(photoId) {
        photo = photoDao.getPhotoById(photoId)
    }

    val currentPhoto = photo ?: return

    fun sharePhoto() {
        val file = File(currentPhoto.filePath)
        if (!file.exists()) return
        val uri: Uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/jpeg"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Share photo via Clickit"))
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Fullscreen Image
        AsyncImage(
            model = File(currentPhoto.filePath),
            contentDescription = currentPhoto.title,
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize()
        )

        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp, start = 16.dp, end = 16.dp)
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.Black.copy(alpha = 0.5f))
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }

            IconButton(
                onClick = { showExifSheet = true },
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.Black.copy(alpha = 0.5f))
            ) {
                Icon(Icons.Default.Info, contentDescription = "EXIF Info", tint = Color.White)
            }
        }

        // Bottom Action Bar
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 24.dp)
                .align(Alignment.BottomCenter),
            cornerRadius = 32.dp,
            backgroundColor = Color.Black.copy(alpha = 0.65f)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    coroutineScope.launch {
                        photoDao.toggleFavorite(currentPhoto.id, !currentPhoto.isFavorite)
                        photo = photoDao.getPhotoById(currentPhoto.id)
                    }
                }) {
                    Icon(
                        imageVector = if (currentPhoto.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (currentPhoto.isFavorite) Color.Red else Color.White
                    )
                }

                IconButton(onClick = { onEditPhoto(currentPhoto.id) }) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color(0xFFFF6D00))
                }

                IconButton(onClick = { sharePhoto() }) {
                    Icon(Icons.Default.Share, contentDescription = "Share", tint = Color.White)
                }

                IconButton(onClick = {
                    coroutineScope.launch {
                        photoDao.moveToTrash(currentPhoto.id)
                        onBack()
                    }
                }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                }
            }
        }

        // EXIF Info Modal Bottom Sheet
        if (showExifSheet) {
            ModalBottomSheet(
                onDismissRequest = { showExifSheet = false },
                containerColor = Color(0xFF1C1C22)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Text("PHOTO METADATA", color = Color(0xFFFF6D00), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))

                    Text("File: ${currentPhoto.title}", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    Text("Filter: ${currentPhoto.filterApplied}", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)

                    val df = SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.getDefault())
                    Text("Date: ${df.format(Date(currentPhoto.timestamp))}", color = Color.Gray, fontSize = 13.sp)

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        ExifItem("ISO", currentPhoto.iso)
                        ExifItem("Shutter", currentPhoto.shutterSpeed)
                        ExifItem("Lens", currentPhoto.focalLength)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        ExifItem("Resolution", "${currentPhoto.width} x ${currentPhoto.height}")
                        ExifItem("Size", "${currentPhoto.fileSizeMb} MB")
                        ExifItem("RAW", if (currentPhoto.isRaw) "DNG RAW" else "JPEG")
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
private fun ExifItem(label: String, value: String) {
    Column {
        Text(label, color = Color.Gray, fontSize = 11.sp)
        Text(value, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}
