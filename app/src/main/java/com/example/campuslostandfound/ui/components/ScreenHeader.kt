package com.example.campuslostandfound.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.campuslostandfound.ui.theme.CampusLostAndFoundTheme
import com.example.campuslostandfound.ui.theme.Dimens

/**
 * Large bold screen title, optionally preceded by a back arrow and followed by [actions].
 * Used as the top bar of every screen so headers line up across the app.
 *
 * The back arrow is shown only when [onBackClick] is given, so an arrow can never appear without
 * an action. Stateless: the screen decides what happens when it is tapped.
 */
@Composable
fun ScreenHeader(
    title: String,
    modifier: Modifier = Modifier,
    onBackClick: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
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
        if (onBackClick != null) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier
                    .size(32.dp)
                    .clickable(role = Role.Button, onClick = onBackClick),
            )
            Spacer(modifier = Modifier.width(Dimens.SpacingSmall))
        }
        Text(
            text = title,
            modifier = Modifier
                .weight(1f)
                .semantics { heading() },
            style = MaterialTheme.typography.headlineMedium,
        )
        actions()
    }
}

@Preview(showBackground = true)
@Composable
private fun ScreenHeaderPreview() {
    CampusLostAndFoundTheme {
        ScreenHeader(title = "Item Detail", onBackClick = {})
    }
}

@Preview(showBackground = true)
@Composable
private fun ScreenHeaderWithActionPreview() {
    CampusLostAndFoundTheme {
        ScreenHeader(title = "Item Detail", onBackClick = {}) {
            IconButton(onClick = {}) {
                Icon(imageVector = Icons.Filled.FavoriteBorder, contentDescription = "Save item")
            }
        }
    }
}
