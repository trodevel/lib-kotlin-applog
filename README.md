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

appLogRepository.log(
    area = LogArea.STOPSTART,
    level = LogLevel.INFO,
    type = LogDataType.STRING,
    functionName = "onStart",
    message = "Service started successfully"
)
```

### 3. Retrieve logs

```kotlin
val logs = appLogRepository.find(
    dateFrom = Date(System.currentTimeMillis() - 3600000), // Last hour
    area = LogArea.STOPSTART,
    maxMessages = 100
)
```

## Data Structure

Each log entry contains:
- `timestamp`: Epoch milliseconds.
- `logArea`: Integer representing the category (from `LogArea` enum).
- `logLevel`: Severity level (DEBUG, INFO, WARNING, ERROR).
- `dataType`: Hint about the message content format (BOOL, INT, FLOAT, STRING).
- `functionName`: The context or function where the log originated.
- `logMessage`: The actual log content (automatically sanitized for CSV).

## License

Copyright © 2026 Sergey Kolevatov.

This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
