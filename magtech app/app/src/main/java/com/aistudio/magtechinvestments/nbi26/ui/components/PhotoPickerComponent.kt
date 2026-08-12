package com.aistudio.magtechinvestments.nbi26.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.aistudio.magtechinvestments.nbi26.ui.theme.*
import java.io.File

@Composable
fun PhotoPickerComponent(
    photos: List<String>,
    onPhotosUpdated: (List<String>) -> Unit,
    minPhotos: Int = 2,
    maxPhotos: Int = 4
) {
    val context = LocalContext.current
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }

    // Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempCameraUri != null) {
            val newList = photos.toMutableList()
            if (newList.size < maxPhotos) {
                newList.add(tempCameraUri.toString())
                onPhotosUpdated(newList)
            }
        }
    }

    // Gallery launcher fallback
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val newList = photos.toMutableList()
            if (newList.size < maxPhotos) {
                newList.add(uri.toString())
                onPhotosUpdated(newList)
            }
        }
    }

    // Camera Permission Launcher
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            try {
                val imagesDir = File(context.cacheDir, "images")
                if (!imagesDir.exists()) imagesDir.mkdirs()
                val file = File.createTempFile("magtech_item_${System.currentTimeMillis()}", ".jpg", imagesDir)
                val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
                tempCameraUri = uri
                cameraLauncher.launch(uri)
            } catch (e: Exception) {
                galleryLauncher.launch("image/*")
            }
        } else {
            galleryLauncher.launch("image/*")
        }
    }

    fun launchCamera() {
        if (photos.size >= maxPhotos) return
        cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(DarkSurface)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Piga Picha za Item (Product Photos)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Chukua angalau picha $minPhotos mpaka $maxPhotos za kuonyesha item vizuri",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (photos.size >= minPhotos) AccentGreen else TerracottaPeach)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "${photos.size}/$maxPhotos Photos",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (photos.size >= minPhotos) Color.Black else TextOnTerracotta
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Thumbnail List + Add Photo Button
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Photos Thumbnails
            itemsIndexed(photos) { index, photoUrl ->
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(DarkSurfaceVariant)
                        .border(1.dp, TerracottaPeach, RoundedCornerShape(14.dp))
                ) {
                    AsyncImage(
                        model = photoUrl,
                        contentDescription = "Photo ${index + 1}",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    // Overlay Action Bar (Delete & Reorder)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .background(Color.Black.copy(alpha = 0.75f))
                            .padding(horizontal = 4.dp, vertical = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Move left
                        if (index > 0) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Move Left",
                                tint = Color.White,
                                modifier = Modifier
                                    .size(18.dp)
                                    .clickable {
                                        val mutable = photos.toMutableList()
                                        val temp = mutable[index]
                                        mutable[index] = mutable[index - 1]
                                        mutable[index - 1] = temp
                                        onPhotosUpdated(mutable)
                                    }
                            )
                        } else {
                            Spacer(modifier = Modifier.width(18.dp))
                        }

                        // Delete
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Photo",
                            tint = AccentRed,
                            modifier = Modifier
                                .size(20.dp)
                                .clickable {
                                    val mutable = photos.toMutableList()
                                    mutable.removeAt(index)
                                    onPhotosUpdated(mutable)
                                }
                        )

                        // Move right
                        if (index < photos.size - 1) {
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = "Move Right",
                                tint = Color.White,
                                modifier = Modifier
                                    .size(18.dp)
                                    .clickable {
                                        val mutable = photos.toMutableList()
                                        val temp = mutable[index]
                                        mutable[index] = mutable[index + 1]
                                        mutable[index + 1] = temp
                                        onPhotosUpdated(mutable)
                                    }
                            )
                        } else {
                            Spacer(modifier = Modifier.width(18.dp))
                        }
                    }
                }
            }

            // Add Photo Card
            if (photos.size < maxPhotos) {
                item {
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(DarkSurfaceVariant)
                            .border(1.dp, DarkBorder, RoundedCornerShape(14.dp))
                            .clickable { launchCamera() },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.AddAPhoto,
                                contentDescription = "Take Photo",
                                tint = TerracottaPeach,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Take Photo",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TerracottaPeach
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Photos Validation Status Text
        if (photos.size < minPhotos) {
            Text(
                text = "⚠️ Boss, piga angalau picha $minPhotos za item kabla ya kusave.",
                fontSize = 11.sp,
                color = AccentRed
            )
        } else {
            Text(
                text = "✅ Picha ziko ready na zitawaka kwa Marketplace!",
                fontSize = 11.sp,
                color = AccentGreen
            )
        }
    }
}
