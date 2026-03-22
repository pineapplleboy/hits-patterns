package com.example.g_bankforclient.domain.models

data class CreditRating(
    val score: Int,                  // 1–999
    val level: CreditRatingLevel,
    val totalCredits: Long,
    val closedCredits: Long,
    val activeCredits: Long,
    val missedPayments: Long
)

enum class CreditRatingLevel {
    EXCELLENT, // 750–999
    GOOD,      // 500–749
    FAIR,      // 250–499
    POOR       // 1–249
}
