package com.example.g_bankforclient.presentation.ui.utils

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

fun Double.formatMoney(): String {
    val symbols = DecimalFormatSymbols(Locale.getDefault())
    symbols.groupingSeparator = ' '
    symbols.decimalSeparator = ','
    val formatter = DecimalFormat("#,##0.00", symbols)
    return "${formatter.format(this)} ₽"
}

