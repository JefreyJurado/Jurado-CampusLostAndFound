package com.example.campuslostandfound.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.campuslostandfound.data.ItemStatus
import com.example.campuslostandfound.ui.theme.Black
import com.example.campuslostandfound.ui.theme.CampusLostAndFoundTheme
import com.example.campuslostandfound.ui.theme.FoundGreen
import com.example.campuslostandfound.ui.theme.LostOrange

/**
 * Colored pill showing whether an item is Lost or Found.
 * [width] and [textStyle] let the same badge be small in the feed and larger on the detail screen.
 */
@Composable
fun StatusBadge(
    status: ItemStatus,
    modifier: Modifier = Modifier,
    width: Dp = 60.dp,
    textStyle: TextStyle = MaterialTheme.typography.labelSmall,
) {
    val color = when (status) {
        ItemStatus.FOUND -> FoundGreen
        ItemStatus.LOST -> LostOrange
    }
    Box(
        modifier = modifier
            .width(width)
            .background(color = color, shape = RoundedCornerShape(2.dp))
            .padding(vertical = 2.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = status.label, style = textStyle, color = Black)
    }
}

@Preview(showBackground = true)
@Composable
private fun StatusBadgePreview() {
    CampusLostAndFoundTheme {
        Row(modifier = Modifier.padding(8.dp)) {
            StatusBadge(status = ItemStatus.FOUND)
            StatusBadge(status = ItemStatus.LOST, modifier = Modifier.padding(start = 8.dp))
        }
    }
}
