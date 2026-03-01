package com.example.g_bankforemployees.common.presentation.component

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.g_bankforemployees.R

@Composable
fun InlineLoadingText(modifier: Modifier = Modifier) {
    Text(
        text = stringResource(R.string.loading),
        style = MaterialTheme.typography.bodyMedium,
        modifier = modifier,
    )
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
fun InlineErrorText(
    message: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = message,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.error,
        modifier = modifier,
    )
    Spacer(modifier = Modifier.height(8.dp))
}
