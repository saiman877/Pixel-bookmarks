package com.pixelmarks.app.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.pixelmarks.app.ui.theme.getExpressiveShapeByName
import java.net.URLEncoder

@Composable
fun WebsiteThumbnail(
    url: String,
    imageUrl: String?,
    domain: String,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 20.dp,
    aspectRatio: Float = 16f / 10f,
    showLiveBadge: Boolean = true
) {
    val context = LocalContext.current
    val screenshotUrl = remember(url) {
        "https://s0.wp.com/mshots/v1/${URLEncoder.encode(url, "UTF-8")}?w=960"
    }

    var currentSource by remember(imageUrl, screenshotUrl) {
        mutableStateOf(imageUrl ?: screenshotUrl)
    }
    var isScreenshotFallback by remember(imageUrl, screenshotUrl) {
        mutableStateOf(imageUrl == null || imageUrl == screenshotUrl)
    }
    var loadFailed by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .aspectRatio(aspectRatio)
            .clip(RoundedCornerShape(cornerRadius))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
    ) {
        if (!loadFailed && currentSource != null) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(currentSource)
                    .crossfade(true)
                    .build(),
                contentDescription = "Website thumbnail for $domain",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                onError = {
                    if (!isScreenshotFallback) {
                        isScreenshotFallback = true
                        currentSource = screenshotUrl
                    } else {
                        loadFailed = true
                    }
                }
            )

            // Subtle live snapshot badge in corner if using live screenshot
            if (isScreenshotFallback && showLiveBadge) {
                Surface(
                    shape = RoundedCornerShape(percent = 50),
                    color = MaterialTheme.colorScheme.surfaceContainerLowest.copy(alpha = 0.9f),
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.CameraAlt,
                            contentDescription = null,
                            modifier = Modifier.size(12.dp),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Live preview",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        } else {
            // Elegant Material 3 Expressive geometric background without any middle logo
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f))
            )
        }
    }
}

@Composable
fun RowWebsiteThumbnail(
    url: String,
    imageUrl: String?,
    domain: String,
    modifier: Modifier = Modifier
) {
    WebsiteThumbnail(
        url = url,
        imageUrl = imageUrl,
        domain = domain,
        modifier = modifier
            .width(88.dp)
            .height(58.dp),
        cornerRadius = 14.dp,
        aspectRatio = 88f / 58f,
        showLiveBadge = false
    )
}
