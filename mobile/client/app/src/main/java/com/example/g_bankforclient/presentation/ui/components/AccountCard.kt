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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Block
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.g_bankforclient.domain.models.Account
import com.example.g_bankforclient.presentation.ui.utils.formatMoney
import com.example.g_bankforclient.ui.theme.BankColors

@Composable
fun AccountCard(account: Account, onClick: () -> Unit) {
    Surface(
        onClick = if (account.banned) {
            {}
        } else {
            onClick
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = if (account.banned) 1.dp else 2.dp,
        shadowElevation = if (account.banned) 1.dp else 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = account.name,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (account.banned) {
                            BankColors.MediumGray
                        } else {
                            BankColors.SecondaryText
                        }
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = account.balance.formatMoney(),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        if (account.banned) {
                            BankColors.MediumGray.copy(alpha = 0.1f)
                        } else {
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (account.banned) Icons.Filled.Block else Icons.Filled.AccountBalanceWallet,
                    contentDescription = null,
                    tint = if (account.banned) {
                        BankColors.MediumGray
                    } else {
                        MaterialTheme.colorScheme.primary
                    },
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
