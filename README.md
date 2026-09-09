# AppLog Library

A simple, persistent logging library for Android applications that stores logs in CSV format within the app's internal storage.

## Features

- **Persistent Logging**: Logs are saved to `.csv` files in the application's internal files directory.
- **Auto-rotation**: Logs are grouped by date and hour (`app_log_yyyy-MM-dd_HH.csv`).
- **Structured Data**: Supports categorizing logs by Area, Level, and Data Type.
- **Querying**: Basic filtering by date range and log area.
- **Lightweight**: Zero external dependencies other than `androidx.core`.

## Integration

To use this library in your Android project, include it in your `settings.gradle.kts`:

```kotlin
include(":applog")
project(":applog").projectDir = File("../path/to/libs/kotlin/applog")
```

Then add it as a dependency in your module's `build.gradle.kts`:

```kotlin
dependencies {
    implementation(project(":applog"))
}
```

## Usage

### 1. Get an instance
`AppLogRepository` is a singleton.

```kotlin
val appLogRepository = AppLogRepository.getInstance(context)
```

### 2. Log an event

```kotlin
import com.trodevel.applog.*

// Use standard areas
appLogRepository.log(
    area = LogArea.STOPSTART,
    level = LogLevel.INFO,
    type = LogDataType.STRING,
    functionName = "onStart",
    message = "Service started successfully"
)

// Or use user-defined areas for application-specific features
val AI_ANALYSIS = LogArea.USER_DEFINED_1
appLogRepository.log(
    area = AI_ANALYSIS,
    level = LogLevel.DEBUG,
    type = LogDataType.STRING,
    functionName = "analyze",
    message = "Threshold exceeded"
)
```

### 3. Retrieve logs

```kotlin
// Filter by specific area
val logs = appLogRepository.find(
    dateFrom = Date(System.currentTimeMillis() - 3600000), // Last hour
    area = LogArea.STOPSTART,
    maxMessages = 100
)

// Or filter using ranges (standard vs user-defined)
val standardLogs = appLogRepository.find(dateFrom = yesterday).filter { 
    it.logArea in LogArea._FIRST.value..LogArea._LAST.value 
}
```

## Data Structure

Each log entry contains:
- `timestamp`: Epoch milliseconds.
- `logArea`: Integer representing the category (from `LogArea` enum).
    - **Standard Areas**: `1` to `999` (Markers: `_FIRST` to `_LAST`).
    - **User Defined Areas**: `1000` to `1100` (`USER_DEFINED_1` to `USER_DEFINED_100`).
- `logLevel`: Severity level (DEBUG, INFO, WARNING, ERROR).
- `dataType`: Hint about the message content format (BOOL, INT, FLOAT, STRING).
- `functionName`: The context or function where the log originated.
- `logMessage`: The actual log content (automatically sanitized for CSV).

## License

Copyright © 2026 Sergey Kolevatov.

This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
