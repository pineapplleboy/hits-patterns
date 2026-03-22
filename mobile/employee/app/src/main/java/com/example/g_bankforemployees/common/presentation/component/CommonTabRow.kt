package com.example.g_bankforemployees.common.presentation.component

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun CommonTabRow(
    titles: List<String>,
    selectedTabIndex: Int,
    onSelectedTabIndexChange: (Int) -> Unit,
) {
    TabRow(
        selectedTabIndex = selectedTabIndex,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
    ) {
        titles.forEachIndexed { index, title ->
            Tab(
                selected = selectedTabIndex == index,
                onClick = { onSelectedTabIndexChange(index) },
                text = { Text(text = title, style = MaterialTheme.typography.titleMedium) },
            )
        }
    }
}
