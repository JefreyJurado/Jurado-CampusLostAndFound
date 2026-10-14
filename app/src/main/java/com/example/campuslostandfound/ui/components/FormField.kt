package com.example.campuslostandfound.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.campuslostandfound.ui.theme.CampusLostAndFoundTheme
import com.example.campuslostandfound.ui.theme.Dimens

/**
 * Outlined single-line text field with a placeholder label, used by the Report Item form and the
 * Conversation message bar. Stateless: shows [value] and reports edits through [onValueChange].
 * Width is controlled by the caller through [modifier].
 *
 * When [errorMessage] is not null the field is outlined red and the message is shown under it
 * (and read out by screen readers), so the error is not signaled by color alone.
 * [keyboardOptions] / [keyboardActions] set the keyboard's action key (Next, Done, Send).
 */
@Composable
fun FormField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    errorMessage: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(Dimens.FieldHeight)
                .semantics { if (errorMessage != null) error(errorMessage) },
            placeholder = { Text(text = label, style = MaterialTheme.typography.bodyMedium) },
            textStyle = MaterialTheme.typography.bodyMedium,
            singleLine = true,
            isError = errorMessage != null,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            shape = MaterialTheme.shapes.small,
        )
        if (errorMessage != null) {
            Text(
                text = errorMessage,
                modifier = Modifier.padding(start = Dimens.SpacingLarge, top = 4.dp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
            )
        }
    }
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

@Preview(showBackground = true, name = "With error")
@Composable
private fun FormFieldErrorPreview() {
    CampusLostAndFoundTheme {
        FormField(
            label = "Category",
            value = "",
            onValueChange = {},
            errorMessage = "Enter a category",
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        )
    }
}
