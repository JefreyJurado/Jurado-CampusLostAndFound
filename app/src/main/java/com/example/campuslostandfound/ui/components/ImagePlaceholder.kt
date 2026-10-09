package com.example.campuslostandfound.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.campuslostandfound.ui.theme.CampusLostAndFoundTheme
import com.example.campuslostandfound.ui.theme.Dimens

/**
 * Gray box standing in for an item photo. The caller decides the size via [modifier].
 */
@Composable
fun ImagePlaceholder(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.background(
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(Dimens.CornerRadius),
        ),
    )
}

@Preview
@Composable
private fun ImagePlaceholderPreview() {
    CampusLostAndFoundTheme {
        ImagePlaceholder(modifier = Modifier.size(Dimens.FeedImageWidth, Dimens.FeedImageHeight))
    }
}
