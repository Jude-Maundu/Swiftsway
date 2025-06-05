package com.judemaundu.swiftsway2.ui.theme.Data.Payment

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


data class Payment(
    val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val amount: Double = 0.0,
    val date: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
)