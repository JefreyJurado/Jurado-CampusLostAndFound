package com.example.campuslostandfound.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.campuslostandfound.data.ItemStatus
import com.example.campuslostandfound.ui.components.GrayButton
import com.example.campuslostandfound.ui.components.ScreenHeader
import com.example.campuslostandfound.ui.theme.CampusLostAndFoundTheme
import com.example.campuslostandfound.ui.theme.Dimens
import com.example.campuslostandfound.ui.theme.OutlineGray
import com.example.campuslostandfound.ui.theme.SelectedGray

/**
 * Report Item — short form for posting a lost or found item.
 *
 * Hierarchy:
 * Scaffold
 *  ├─ topBar: ScreenHeader("Report Item", back arrow)
 *  └─ content: Column (scrollable so the form fits small screens / open keyboard)
 *       ├─ ReportTypeToggle (Lost | Found)
 *       ├─ FormField × 3 (Name, Category, Location)
 *       ├─ PhotoPicker (dashed box)
 *       └─ GrayButton("Submit Report")
 */
@Composable
fun ReportItemScreen(
    modifier: Modifier = Modifier,
    selectedType: ItemStatus = ItemStatus.LOST,
) {
    Scaffold(
        modifier = modifier,
        topBar = { ScreenHeader(title = "Report Item", showBackArrow = true) },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Dimens.ScreenPadding),
            verticalArrangement = Arrangement.spacedBy(Dimens.SpacingMedium),
        ) {
            ReportTypeToggle(selected = selectedType)
            FormField(label = "Name")
            FormField(label = "Category")
            FormField(label = "Location")
            PhotoPicker(modifier = Modifier.padding(top = Dimens.SpacingSmall))
            GrayButton(
                text = "Submit Report",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Dimens.SpacingSmall),
            )
        }
    }
}

/**
 * Two equal-width segments for choosing Lost or Found. The [selected] one is filled gray.
 */
@Composable
fun ReportTypeToggle(
    selected: ItemStatus,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingMedium),
    ) {
        ItemStatus.entries.forEach { status ->
            ToggleSegment(
                label = status.label,
                isSelected = status == selected,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun ToggleSegment(
    label: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.height(40.dp),
        shape = RoundedCornerShape(Dimens.CornerRadius),
        color = if (isSelected) SelectedGray else MaterialTheme.colorScheme.surface,
        border = if (isSelected) null else BorderStroke(1.dp, OutlineGray),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(text = label, style = MaterialTheme.typography.titleLarge)
        }
    }
}

/**
 * Outlined single-line text field with a placeholder label.
 * Value is empty for now — input state will be added when the form becomes interactive.
 */
@Composable
fun FormField(
    label: String,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = "",
        onValueChange = {},
        modifier = modifier
            .fillMaxWidth()
            .height(Dimens.FieldHeight),
        placeholder = { Text(text = label, style = MaterialTheme.typography.bodyMedium) },
        textStyle = MaterialTheme.typography.bodyMedium,
        singleLine = true,
        shape = RoundedCornerShape(Dimens.CornerRadius),
    )
}

/**
 * Dashed drop-zone prompting the user to add an optional photo.
 */
@Composable
fun PhotoPicker(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(Dimens.PhotoPickerHeight)
            .drawBehind {
                drawRoundRect(
                    color = OutlineGray,
                    style = Stroke(
                        width = 1.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(6.dp.toPx(), 4.dp.toPx())),
                    ),
                    cornerRadius = CornerRadius(2.dp.toPx()),
                )
            },
        contentAlignment = Alignment.Center,
    ) {
        Text(text = "Tap to add a photo", style = MaterialTheme.typography.bodyMedium)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ReportItemScreenPreview() {
    CampusLostAndFoundTheme {
        ReportItemScreen()
    }
}

@Preview(showBackground = true)
@Composable
private fun ReportTypeToggleFoundPreview() {
    CampusLostAndFoundTheme {
        ReportTypeToggle(selected = ItemStatus.FOUND, modifier = Modifier.padding(Dimens.ScreenPadding))
    }
}
