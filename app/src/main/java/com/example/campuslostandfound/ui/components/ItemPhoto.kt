package com.example.campuslostandfound.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.AsyncImage
import com.example.campuslostandfound.ui.theme.CampusLostAndFoundTheme
import com.example.campuslostandfound.ui.theme.Dimens

/**
 * An item's photo, cropped to fill the box. Items without a photo (and photos that fail to load)
 * show the plain gray box from the prototype. The caller decides the size via [modifier].
 */
@Composable
fun ItemPhoto(
    photoUri: String?,
    modifier: Modifier = Modifier,
) {
    val shape = MaterialTheme.shapes.small
    val placeholderColor = MaterialTheme.colorScheme.surfaceVariant

    if (photoUri == null) {
        Box(modifier = modifier.background(color = placeholderColor, shape = shape))
    } else {
        AsyncImage(
            model = photoUri,
            contentDescription = "Item photo",
            modifier = modifier.clip(shape),
            placeholder = ColorPainter(placeholderColor),
            error = ColorPainter(placeholderColor),
            contentScale = ContentScale.Crop,
        )
    }
}

@Preview
@Composable
private fun ItemPhotoPreview() {
    CampusLostAndFoundTheme {
        ItemPhoto(photoUri = null, modifier = Modifier.size(Dimens.FeedImageWidth, Dimens.FeedImageHeight))
    }
}
