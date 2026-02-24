package com.example.g_bankforclient.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.g_bankforclient.common.models.Transaction
import com.example.g_bankforclient.common.models.TransactionType
import com.example.g_bankforclient.ui.theme.BankColors
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TransactionItem(transaction: Transaction) {
    val icon = when (transaction.type) {
        TransactionType.DEPOSIT -> Icons.Filled.FileDownload
        TransactionType.WITHDRAWAL -> Icons.Filled.FileUpload
        TransactionType.CREDIT_TAKEN -> Icons.Filled.CreditCard
        TransactionType.CREDIT_PAYMENT -> Icons.Default.TrendingUp
    }

    val iconColor = when (transaction.type) {
        TransactionType.DEPOSIT -> BankColors.Success
        TransactionType.WITHDRAWAL -> BankColors.Warning
        TransactionType.CREDIT_TAKEN -> MaterialTheme.colorScheme.primary
        TransactionType.CREDIT_PAYMENT -> BankColors.ErrorRed
    }

    val amountColor = when (transaction.type) {
        TransactionType.DEPOSIT -> BankColors.Success
        TransactionType.WITHDRAWAL -> BankColors.Warning
        TransactionType.CREDIT_TAKEN -> MaterialTheme.colorScheme.primary
        TransactionType.CREDIT_PAYMENT -> BankColors.ErrorRed
    }

    val sign = if (transaction.type == TransactionType.DEPOSIT || transaction.type == TransactionType.CREDIT_TAKEN) "+" else "-"

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(BankColors.LightGray),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.description,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = formatDate(transaction.date),
                    style = MaterialTheme.typography.bodySmall,
                    color = BankColors.SecondaryText
                )
            }

            Text(
                text = "$sign${transaction.amount.toInt().toString().replace(Regex("(\\d)(?=(\\d{3})+$)"), "$1 ")} ₽",
                style = MaterialTheme.typography.titleMedium,
                color = amountColor
            )
        }
    }
}

fun formatDate(date: Date): String {
    val today = Calendar.getInstance()
    val yesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }
    val dateCalendar = Calendar.getInstance().apply { time = date }

    return when {
        isSameDay(dateCalendar, today) -> {
            "Сегодня, ${SimpleDateFormat("HH:mm", Locale.getDefault()).format(date)}"
        }
        isSameDay(dateCalendar, yesterday) -> {
            "Вчера, ${SimpleDateFormat("HH:mm", Locale.getDefault()).format(date)}"
        }
        else -> {
            SimpleDateFormat("d MMM, HH:mm", Locale.getDefault()).format(date)
        }
    }
}

fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}
