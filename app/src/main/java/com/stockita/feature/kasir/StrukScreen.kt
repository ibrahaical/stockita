package com.stockita.feature.kasir

import android.graphics.Picture
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.stockita.core.util.ImageExportUtil
import com.stockita.core.util.captureToPicture
import com.stockita.core.util.createBitmap
import com.stockita.feature.kasir.component.StrukContent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StrukScreen(
    onNavigateBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    // Picture to capture the composable
    val picture = remember { Picture() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Struk Pembayaran") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFF6900),
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            BottomAppBar {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            val bitmap = withContext(Dispatchers.Default) {
                                picture.createBitmap()
                            }
                            val uri = withContext(Dispatchers.IO) {
                                ImageExportUtil.saveBitmapToCache(context, bitmap)
                            }
                            if (uri != null) {
                                ImageExportUtil.shareImageToWhatsApp(context, uri)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6900))
                ) {
                    Text("Bagikan ke WA / Unduh")
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF7F7F8)),
            contentAlignment = Alignment.Center
        ) {
            // Container for Struk
            Box(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
                    .clip(RoundedCornerShape(8.dp))
                    // Capture what's inside this Box
                    .captureToPicture(picture)
            ) {
                StrukContent()
            }
        }
    }
}
