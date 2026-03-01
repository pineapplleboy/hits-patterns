package com.example.g_bankforclient.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.g_bankforclient.domain.models.Transaction
import com.example.g_bankforclient.domain.models.TransactionStatus
import com.example.g_bankforclient.domain.models.TransactionType
import com.example.g_bankforclient.presentation.ui.utils.formatMoney
import com.example.g_bankforclient.ui.theme.BankColors
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun TransactionItem(transaction: Transaction) {
    val icon = when (transaction.type) {
        TransactionType.DEPOSIT -> Icons.Filled.FileDownload
        TransactionType.WITHDRAWAL -> Icons.Filled.FileUpload
        TransactionType.CREDIT_TAKEN -> Icons.Filled.CreditCard
        TransactionType.CREDIT_PAYMENT -> Icons.Default.TrendingUp
        TransactionType.INFO -> Icons.Filled.Info
    }

    val iconColor = when (transaction.type) {
        TransactionType.DEPOSIT -> BankColors.Success
        TransactionType.WITHDRAWAL -> BankColors.Warning
        TransactionType.CREDIT_TAKEN -> MaterialTheme.colorScheme.primary
        TransactionType.CREDIT_PAYMENT -> BankColors.ErrorRed
        TransactionType.INFO -> BankColors.MediumGray
    }

    val amountColor = when (transaction.type) {
        TransactionType.DEPOSIT -> BankColors.Success
        TransactionType.WITHDRAWAL -> BankColors.Warning
        TransactionType.CREDIT_TAKEN -> MaterialTheme.colorScheme.primary
        TransactionType.CREDIT_PAYMENT -> BankColors.ErrorRed
        TransactionType.INFO -> BankColors.MediumGray
    }

    val sign = if (transaction.type == TransactionType.DEPOSIT ||
        transaction.type == TransactionType.CREDIT_TAKEN
    ) "+" else "-"

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
                if (transaction.type == TransactionType.DEPOSIT && transaction.fromAccount != null) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "С счёта: ${transaction.fromAccount}",
                        style = MaterialTheme.typography.bodySmall,
                        color = BankColors.SecondaryText
                    )
                } else if ((transaction.type == TransactionType.WITHDRAWAL || transaction.type == TransactionType.CREDIT_PAYMENT) && transaction.toAccount != null) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "На счёт: ${transaction.toAccount}",
                        style = MaterialTheme.typography.bodySmall,
                        color = BankColors.SecondaryText
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = formatDate(transaction.date),
                    style = MaterialTheme.typography.bodySmall,
                    color = BankColors.SecondaryText
                )
                Spacer(modifier = Modifier.height(6.dp))
                TransactionStatusBadge(transaction.status)
            }

            Text(
                text = "$sign${transaction.amount.formatMoney().replace(" ₽", "")} ₽",
                style = MaterialTheme.typography.titleMedium,
                color = amountColor
            )
        }
    }
}

@Composable
private fun TransactionStatusBadge(status: TransactionStatus) {
    val (label, bgColor, textColor) = when (status) {
        TransactionStatus.CREATED -> Triple(
            "Создана",
            BankColors.MediumGray.copy(alpha = 0.15f),
            BankColors.MediumGray
        )

        TransactionStatus.IN_PROCESS -> Triple(
            "В обработке",
            Color(0xFFFFF3CD),
            Color(0xFF856404)
        )

        TransactionStatus.SUCCESS -> Triple(
            "Выполнена",
            BankColors.Success.copy(alpha = 0.12f),
            BankColors.Success
        )

        TransactionStatus.REJECTED -> Triple(
            "Отклонена",
            BankColors.ErrorRed.copy(alpha = 0.12f),
            BankColors.ErrorRed
        )
    }

    Surface(
        shape = RoundedCornerShape(6.dp),
        color = bgColor,
        modifier = Modifier.wrapContentSize()
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
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
