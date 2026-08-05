package com.trodevel.applog

enum class LogLevel(val value: Int) {
    DEBUG(1),
    INFO(2),
    WARNING(3),
    ERROR(4);

    fun getIcon(): String {
        return when (this) {
            ERROR -> "❗"
            WARNING -> "⚠️"
            INFO -> "ℹ️"
            DEBUG -> "🪲"
        }
    }

    fun getShortName(): String {
        return when (this) {
            DEBUG -> "D"
            INFO -> "I"
            WARNING -> "W"
            ERROR -> "E"
        }
    }
}
