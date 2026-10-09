package com.example.campuslostandfound.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.campuslostandfound.data.ItemStatus
import com.example.campuslostandfound.ui.components.DiscardReportDialog
import com.example.campuslostandfound.ui.components.GrayButton
import com.example.campuslostandfound.ui.components.ReportPostedBanner
import com.example.campuslostandfound.ui.components.ScreenHeader
import com.example.campuslostandfound.ui.theme.CampusLostAndFoundTheme
import com.example.campuslostandfound.ui.theme.Dimens
import com.example.campuslostandfound.ui.theme.OutlineGray
import com.example.campuslostandfound.ui.theme.SelectedGray
import kotlinx.coroutines.delay

/**
 * Report Item — stateful entry point. Owns every piece of state for the form
 * and passes values + event callbacks down to the stateless [ReportItemContent].
 *
 * State:
 *  - selectedType      → which Lost / Found segment is highlighted
 *  - name / category / location → text in each field
 *  - showErrors        → set after a submit attempt with blank fields; outlines them red
 *  - showDiscardDialog → whether the "Discard this report?" dialog is visible
 *  - showPostedBanner  → whether the "Report Posted" banner is visible
 */
@Composable
fun ReportItemScreen(modifier: Modifier = Modifier) {
    var selectedType by remember { mutableStateOf(ItemStatus.LOST) }
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var showErrors by remember { mutableStateOf(false) }
    var showDiscardDialog by remember { mutableStateOf(false) }
    var showPostedBanner by remember { mutableStateOf(false) }

    // Derived values — recalculated on every recomposition, so they never go out of sync.
    val hasUnsavedChanges = name.isNotBlank() || category.isNotBlank() || location.isNotBlank()
    val isFormComplete = name.isNotBlank() && category.isNotBlank() && location.isNotBlank()

    fun clearForm() {
        selectedType = ItemStatus.LOST
        name = ""
        category = ""
        location = ""
        showErrors = false
    }

    // Hide the banner automatically a couple of seconds after it appears.
    LaunchedEffect(showPostedBanner) {
        if (showPostedBanner) {
            delay(2_000)
            showPostedBanner = false
        }
    }

    ReportItemContent(
        modifier = modifier,
        selectedType = selectedType,
        name = name,
        category = category,
        location = location,
        showErrors = showErrors,
        showPostedBanner = showPostedBanner,
        onTypeSelect = { selectedType = it },
        onNameChange = { name = it },
        onCategoryChange = { category = it },
        onLocationChange = { location = it },
        onBackClick = {
            // Only ask for confirmation when there is something to lose.
            if (hasUnsavedChanges) showDiscardDialog = true
        },
        onSubmitClick = {
            if (isFormComplete) {
                clearForm()
                showPostedBanner = true
            } else {
                showErrors = true
            }
        },
    )

    if (showDiscardDialog) {
        Dialog(onDismissRequest = { showDiscardDialog = false }) {
            DiscardReportDialog(
                onKeepEditing = { showDiscardDialog = false },
                onDiscard = {
                    clearForm()
                    showDiscardDialog = false
                },
            )
        }
    }
}

/**
 * Report Item — stateless layout. Renders whatever state it is given and reports user events upward.
 *
 * Hierarchy:
 * Scaffold
 *  ├─ topBar: ScreenHeader("Report Item", back arrow)
 *  └─ content: Box
 *       ├─ Column (scrollable)
 *       │    ├─ ReportTypeToggle (Lost | Found)
 *       │    ├─ FormField × 3 (Name, Category, Location)
 *       │    ├─ PhotoPicker (dashed box)
 *       │    └─ GrayButton("Submit Report")
 *       └─ ReportPostedBanner (only while showPostedBanner is true)
 */
@Composable
fun ReportItemContent(
    selectedType: ItemStatus,
    name: String,
    category: String,
    location: String,
    showErrors: Boolean,
    showPostedBanner: Boolean,
    onTypeSelect: (ItemStatus) -> Unit,
    onNameChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onLocationChange: (String) -> Unit,
    onBackClick: () -> Unit,
    onSubmitClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            ScreenHeader(title = "Report Item", showBackArrow = true, onBackClick = onBackClick)
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = Dimens.ScreenPadding),
                verticalArrangement = Arrangement.spacedBy(Dimens.SpacingMedium),
            ) {
                ReportTypeToggle(selected = selectedType, onSelect = onTypeSelect)
                FormField(
                    label = "Name",
                    value = name,
                    onValueChange = onNameChange,
                    isError = showErrors && name.isBlank(),
                )
                FormField(
                    label = "Category",
                    value = category,
                    onValueChange = onCategoryChange,
                    isError = showErrors && category.isBlank(),
                )
                FormField(
                    label = "Location",
                    value = location,
                    onValueChange = onLocationChange,
                    isError = showErrors && location.isBlank(),
                )
                PhotoPicker(modifier = Modifier.padding(top = Dimens.SpacingSmall))
                GrayButton(
                    text = "Submit Report",
                    onClick = onSubmitClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = Dimens.SpacingSmall),
                )
            }

            if (showPostedBanner) {
                ReportPostedBanner(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .navigationBarsPadding()
                        .padding(bottom = Dimens.SpacingXLarge),
                )
            }
        }
    }
}

/**
 * Two equal-width segments for choosing Lost or Found. The [selected] one is filled gray.
 * Stateless: tapping a segment reports it through [onSelect]; the parent decides what is selected.
 */
@Composable
fun ReportTypeToggle(
    selected: ItemStatus,
    onSelect: (ItemStatus) -> Unit,
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
                onClick = { onSelect(status) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun ToggleSegment(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .height(40.dp)
            .clickable(onClick = onClick),
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
 * Stateless: shows [value] and reports edits through [onValueChange]. [isError] outlines it red.
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
        modifier = modifier
            .fillMaxWidth()
            .height(Dimens.FieldHeight),
        placeholder = { Text(text = label, style = MaterialTheme.typography.bodyMedium) },
        textStyle = MaterialTheme.typography.bodyMedium,
        singleLine = true,
        isError = isError,
        shape = RoundedCornerShape(Dimens.CornerRadius),
    )
}

/**
 * Dashed drop-zone prompting the user to add an optional photo.
 * Photo selection needs device access, so it stays visual-only for now.
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

// Interactive: use the preview's "Start Interactive Mode" button to type, toggle, and submit.
@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ReportItemScreenPreview() {
    CampusLostAndFoundTheme {
        ReportItemScreen()
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Missing fields after submit")
@Composable
private fun ReportItemErrorsPreview() {
    CampusLostAndFoundTheme {
        ReportItemContent(
            selectedType = ItemStatus.FOUND,
            name = "Black Headphones",
            category = "",
            location = "",
            showErrors = true,
            showPostedBanner = false,
            onTypeSelect = {},
            onNameChange = {},
            onCategoryChange = {},
            onLocationChange = {},
            onBackClick = {},
            onSubmitClick = {},
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Report posted")
@Composable
private fun ReportItemPostedPreview() {
    CampusLostAndFoundTheme {
        ReportItemContent(
            selectedType = ItemStatus.LOST,
            name = "",
            category = "",
            location = "",
            showErrors = false,
            showPostedBanner = true,
            onTypeSelect = {},
            onNameChange = {},
            onCategoryChange = {},
            onLocationChange = {},
            onBackClick = {},
            onSubmitClick = {},
        )
    }
}
