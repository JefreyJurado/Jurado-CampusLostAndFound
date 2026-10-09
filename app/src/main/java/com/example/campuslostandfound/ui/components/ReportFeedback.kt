package com.example.campuslostandfound.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.campuslostandfound.ui.theme.Black
import com.example.campuslostandfound.ui.theme.CampusLostAndFoundTheme
import com.example.campuslostandfound.ui.theme.Dimens
import com.example.campuslostandfound.ui.theme.FoundGreen
import com.example.campuslostandfound.ui.theme.White

/**
 * Confirmation shown when leaving the Report Item screen with unsaved changes.
 * Built as a plain Card so it renders in @Preview; it will be hosted in a Dialog
 * once navigation and state are added.
 */
@Composable
fun DiscardReportDialog(
    modifier: Modifier = Modifier,
    onKeepEditing: () -> Unit = {},
    onDiscard: () -> Unit = {},
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = White),
    ) {
        Column(
            modifier = Modifier.padding(Dimens.ScreenPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = "Discard this report?", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(Dimens.SpacingSmall))
            Text(text = "You have unsaved changes", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(Dimens.SpacingLarge))
            Row(horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingLarge)) {
                OutlinedButton(
                    onClick = onKeepEditing,
                    modifier = Modifier.height(Dimens.ButtonHeight),
                    shape = RoundedCornerShape(Dimens.CornerRadius),
                    border = BorderStroke(1.dp, Black),
                ) {
                    Text(text = "Keep Editing", style = MaterialTheme.typography.labelLarge, color = Black)
                }
                Button(
                    onClick = onDiscard,
                    modifier = Modifier
                        .height(Dimens.ButtonHeight)
                        .width(110.dp),
                    shape = RoundedCornerShape(Dimens.CornerRadius),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                ) {
                    Text(text = "Discard", style = MaterialTheme.typography.labelLarge, color = White)
                }
            }
        }
    }
}

/**
 * Small green confirmation banner shown after a report is submitted.
 */
@Composable
fun ReportPostedBanner(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        color = FoundGreen,
        shape = RoundedCornerShape(Dimens.CornerRadius),
    ) {
        Text(
            text = "Report Posted",
            modifier = Modifier.padding(horizontal = Dimens.SpacingLarge, vertical = Dimens.SpacingSmall),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = Black,
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1E1E1E)
@Composable
private fun DiscardReportDialogPreview() {
    CampusLostAndFoundTheme {
        DiscardReportDialog(modifier = Modifier.padding(16.dp))
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1E1E1E)
@Composable
private fun ReportPostedBannerPreview() {
    CampusLostAndFoundTheme {
        ReportPostedBanner(modifier = Modifier.padding(16.dp))
    }
}
