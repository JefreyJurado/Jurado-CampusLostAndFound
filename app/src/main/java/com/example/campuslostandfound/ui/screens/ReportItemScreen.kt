package com.example.campuslostandfound.ui.screens

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.example.campuslostandfound.data.ReportSubmission
import com.example.campuslostandfound.ui.components.DiscardReportDialog
import com.example.campuslostandfound.ui.components.FormField
import com.example.campuslostandfound.ui.components.GrayButton
import com.example.campuslostandfound.ui.components.ItemPhoto
import com.example.campuslostandfound.ui.components.ScreenHeader
import com.example.campuslostandfound.ui.theme.CampusLostAndFoundTheme
import com.example.campuslostandfound.ui.theme.Dimens
import com.example.campuslostandfound.ui.theme.OutlineGray
import com.example.campuslostandfound.ui.theme.SelectedGray

/**
 * Report Item — stateful entry point. Owns every piece of state for the form
 * and passes values + event callbacks down to the stateless [ReportItemContent].
 *
 * State (rememberSaveable, so typing survives rotation):
 *  - selectedType      → which Lost / Found segment is highlighted
 *  - name / category / location → text in each field
 *  - photoUri          → photo chosen with the system photo picker (optional)
 *  - showErrors        → set after a submit attempt with blank fields; outlines them red
 *  - showDiscardDialog → whether the "Discard this report?" dialog is visible
 *
 * Leaving: the back arrow and the system Back button behave the same. With unsaved changes they
 * open the discard dialog; otherwise they call [onNavigateBack] straight away.
 */
@Composable
fun ReportItemScreen(
    onNavigateBack: () -> Unit,
    onSubmit: (ReportSubmission) -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedType by rememberSaveable { mutableStateOf(ItemStatus.LOST) }
    var name by rememberSaveable { mutableStateOf("") }
    var category by rememberSaveable { mutableStateOf("") }
    var location by rememberSaveable { mutableStateOf("") }
    var photoUri by rememberSaveable { mutableStateOf<String?>(null) }
    var showErrors by rememberSaveable { mutableStateOf(false) }
    var showDiscardDialog by rememberSaveable { mutableStateOf(false) }

    // Derived values — recalculated on every recomposition, so they never go out of sync.
    val hasUnsavedChanges =
        name.isNotBlank() || category.isNotBlank() || location.isNotBlank() || photoUri != null
    val isFormComplete = name.isNotBlank() && category.isNotBlank() && location.isNotBlank()

    // The system photo picker needs no storage permission. A cancelled pick returns null and changes nothing.
    val photoPicker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) photoUri = uri.toString()
    }

    // Only intercept the system Back button when there is something to lose.
    BackHandler(enabled = hasUnsavedChanges) { showDiscardDialog = true }

    ReportItemContent(
        modifier = modifier,
        selectedType = selectedType,
        name = name,
        category = category,
        location = location,
        photoUri = photoUri,
        showErrors = showErrors,
        onTypeSelect = { selectedType = it },
        onNameChange = { name = it },
        onCategoryChange = { category = it },
        onLocationChange = { location = it },
        onPhotoClick = {
            photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        },
        onBackClick = {
            if (hasUnsavedChanges) showDiscardDialog = true else onNavigateBack()
        },
        onSubmitClick = {
            if (isFormComplete) {
                onSubmit(
                    ReportSubmission(
                        status = selectedType,
                        name = name.trim(),
                        category = category.trim(),
                        location = location.trim(),
                        photoUri = photoUri,
                    ),
                )
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
                    showDiscardDialog = false
                    onNavigateBack()
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
 *  └─ content: Column (scrollable)
 *       ├─ ReportTypeToggle (Lost | Found)
 *       ├─ FormField × 3 (Name, Category, Location)
 *       ├─ PhotoPicker (dashed box, or the chosen photo)
 *       └─ GrayButton("Submit Report")
 */
@Composable
fun ReportItemContent(
    selectedType: ItemStatus,
    name: String,
    category: String,
    location: String,
    photoUri: String?,
    showErrors: Boolean,
    onTypeSelect: (ItemStatus) -> Unit,
    onNameChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onLocationChange: (String) -> Unit,
    onPhotoClick: () -> Unit,
    onBackClick: () -> Unit,
    onSubmitClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            ScreenHeader(title = "Report Item", onBackClick = onBackClick)
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
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
                modifier = Modifier.fillMaxWidth(),
            )
            FormField(
                label = "Category",
                value = category,
                onValueChange = onCategoryChange,
                isError = showErrors && category.isBlank(),
                modifier = Modifier.fillMaxWidth(),
            )
            FormField(
                label = "Location",
                value = location,
                onValueChange = onLocationChange,
                isError = showErrors && location.isBlank(),
                modifier = Modifier.fillMaxWidth(),
            )
            PhotoPicker(
                photoUri = photoUri,
                onClick = onPhotoClick,
                modifier = Modifier.padding(top = Dimens.SpacingSmall),
            )
            GrayButton(
                text = "Submit Report",
                onClick = onSubmitClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Dimens.SpacingSmall),
            )
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
        shape = MaterialTheme.shapes.small,
        color = if (isSelected) SelectedGray else MaterialTheme.colorScheme.surface,
        border = if (isSelected) null else BorderStroke(1.dp, OutlineGray),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(text = label, style = MaterialTheme.typography.titleLarge)
        }
    }
}

/**
 * Dashed drop-zone prompting the user to add an optional photo. Once a photo is chosen it fills
 * the box; tapping again picks a different one.
 */
@Composable
fun PhotoPicker(
    photoUri: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val boxModifier = modifier
        .fillMaxWidth()
        .height(Dimens.PhotoPickerHeight)
        .clickable(onClickLabel = "Choose a photo", onClick = onClick)

    if (photoUri != null) {
        ItemPhoto(photoUri = photoUri, modifier = boxModifier)
    } else {
        Box(
            modifier = boxModifier.drawBehind {
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
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ReportItemScreenPreview() {
    CampusLostAndFoundTheme {
        ReportItemScreen(onNavigateBack = {}, onSubmit = {})
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
            photoUri = null,
            showErrors = true,
            onTypeSelect = {},
            onNameChange = {},
            onCategoryChange = {},
            onLocationChange = {},
            onPhotoClick = {},
            onBackClick = {},
            onSubmitClick = {},
        )
    }
}
