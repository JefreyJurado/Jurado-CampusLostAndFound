package com.example.campuslostandfound.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.campuslostandfound.ui.theme.CampusLostAndFoundTheme
import com.example.campuslostandfound.ui.theme.Dimens

/**
 * The flat gray action button used throughout the wireframe
 * ("Report Item", "Submit Report", "Message finder", "Send").
 * Width is controlled by the caller through [modifier]; [enabled] is driven by the caller's state.
 */
@Composable
fun GrayButton(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit = {},
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(Dimens.ButtonHeight),
        enabled = enabled,
        shape = RoundedCornerShape(Dimens.CornerRadius),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f),
        ),
        contentPadding = PaddingValues(horizontal = Dimens.SpacingLarge),
    ) {
        Text(text = text, style = MaterialTheme.typography.labelLarge)
    }
}

@Preview(showBackground = true)
@Composable
private fun GrayButtonPreview() {
    CampusLostAndFoundTheme {
        GrayButton(
            text = "Submit Report",
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun GrayButtonDisabledPreview() {
    CampusLostAndFoundTheme {
        GrayButton(
            text = "Send",
            enabled = false,
            modifier = Modifier.padding(16.dp),
        )
    }
}
