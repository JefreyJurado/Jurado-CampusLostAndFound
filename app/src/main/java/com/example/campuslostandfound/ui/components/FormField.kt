package com.example.campuslostandfound.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.campuslostandfound.ui.theme.CampusLostAndFoundTheme
import com.example.campuslostandfound.ui.theme.Dimens

/**
 * Outlined single-line text field with a placeholder label, used by the Report Item form and the
 * Conversation message bar. Stateless: shows [value] and reports edits through [onValueChange].
 * [isError] outlines it red. Width is controlled by the caller through [modifier].
 */
@Composable
fun FormField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.height(Dimens.FieldHeight),
        placeholder = { Text(text = label, style = MaterialTheme.typography.bodyMedium) },
        textStyle = MaterialTheme.typography.bodyMedium,
        singleLine = true,
        isError = isError,
        shape = MaterialTheme.shapes.small,
    )
}

@Preview(showBackground = true)
@Composable
private fun FormFieldPreview() {
    CampusLostAndFoundTheme {
        FormField(
            label = "Name",
            value = "",
            onValueChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        )
    }
}
