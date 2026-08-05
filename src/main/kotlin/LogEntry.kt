package com.trodevel.applog

data class LogEntry(
    val timestamp: Long, // epoch in ms
    val logArea: Int,
    val logLevel: Int,
    val dataType: Int,
    val functionName: String,
    val logMessage: String
)
