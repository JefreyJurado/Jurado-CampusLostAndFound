package com.example.campuslostandfound.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.campuslostandfound.ui.theme.CampusLostAndFoundTheme
import com.example.campuslostandfound.ui.theme.Dimens

/**
 * Large bold screen title, optionally preceded by a back arrow.
 * Used as the top bar of every screen so headers line up across the app.
 */
@Composable
fun ScreenHeader(
    title: String,
    modifier: Modifier = Modifier,
    showBackArrow: Boolean = false,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(
                start = Dimens.ScreenPadding,
                end = Dimens.ScreenPadding,
                top = Dimens.SpacingXLarge,
                bottom = Dimens.SpacingLarge,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (showBackArrow) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier.size(32.dp),
            )
            Spacer(modifier = Modifier.width(Dimens.SpacingSmall))
        }
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ScreenHeaderPreview() {
    CampusLostAndFoundTheme {
        ScreenHeader(title = "Item Detail", showBackArrow = true)
    }
}
