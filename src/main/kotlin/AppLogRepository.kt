package com.trodevel.applog

import android.content.Context
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class AppLogRepository private constructor(private val context: Context) {
    private val prefix = "app_log"
    private val separator = ";"
    private val memoryLogs = mutableListOf<LogEntry>()

    init {
        loadLogsFromFiles()
    }

    private fun loadLogsFromFiles() {
        val files = context.filesDir.listFiles { _, name ->
            name.startsWith(prefix) && name.endsWith(".csv")
        } ?: return

        memoryLogs.clear()
        files.sortedBy { it.name }.forEach { file ->
            try {
                file.readLines().forEach { line ->
                    parseLine(line)?.let { memoryLogs.add(it) }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun log(area: LogArea, level: LogLevel, type: LogDataType, functionName: String, message: String) {
        val timestamp = System.currentTimeMillis()
        val entry = LogEntry(timestamp, area.value, level.value, type.value, functionName, message)
        
        memoryLogs.add(entry)
        saveToFile(entry)
    }

    private fun saveToFile(entry: LogEntry) {
        try {
            val sdf = SimpleDateFormat("yyyy-MM-dd_HH", Locale.getDefault())
            val dateStr = sdf.format(Date(entry.timestamp))
            val fileName = "${prefix}_$dateStr.csv"

            val csvLine = "${entry.timestamp}$separator${entry.logArea}$separator${entry.logLevel}$separator${entry.dataType}$separator\"${sanitize(entry.functionName)}\"$separator\"${sanitize(entry.logMessage)}\"\n"

            val file = File(context.filesDir, fileName)
            FileOutputStream(file, true).use { fos ->
                fos.write(csvLine.toByteArray())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun find(dateFrom: Date? = null, dateTo: Date? = null, area: LogArea? = null, maxMessages: Int? = null): List<LogEntry> {
        var filtered = memoryLogs.asSequence()

        if (dateFrom != null) {
            val fromMs = dateFrom.time
            filtered = filtered.filter { it.timestamp >= fromMs }
        }

        if (dateTo != null) {
            val toMs = dateTo.time
            filtered = filtered.filter { it.timestamp <= toMs }
        }

        if (area != null) {
            filtered = filtered.filter { it.logArea == area.value }
        }

        var result = filtered.toList()
        if (maxMessages != null && result.size > maxMessages) {
            result = result.takeLast(maxMessages)
        }
        
        return result
    }

    private fun parseLine(line: String): LogEntry? {
        val parts = line.split(separator)
        if (parts.size < 6) return null

        return try {
            val timestamp = parts[0].toLong()
            val area = parts[1].toInt()
            val level = parts[2].toInt()
            val type = parts[3].toInt()
            val functionName = unescape(parts[4])
            val message = unescape(parts.subList(5, parts.size).joinToString(separator))
            
            LogEntry(timestamp, area, level, type, functionName, message)
        } catch (e: Exception) {
            null
        }
    }

    private fun sanitize(input: String): String {
        return input.replace("\n", " ").replace("\r", " ").replace("\"", "\"\"")
    }

    private fun unescape(input: String): String {
        return input.removeSurrounding("\"").replace("\"\"", "\"")
    }

    companion object {
        @Volatile
        private var instance: AppLogRepository? = null

        fun getInstance(context: Context): AppLogRepository {
            return instance ?: synchronized(this) {
                instance ?: AppLogRepository(context.applicationContext).also { instance = it }
            }
        }
    }
}
