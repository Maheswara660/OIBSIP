# Taskify ProGuard & R8 Optimization Rules

# Preserve annotation attributes and signatures
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes InnerClasses
-keepattributes EnclosingMethod
-keepattributes LineNumberTable,SourceFile

# Keep line numbers for accurate stack trace logging
-renamesourcefileattribute SourceFile

# Preserve all application Activities
-keep public class com.maheswara660.taskify.LoginActivity { *; }
-keep public class com.maheswara660.taskify.SignUpActivity { *; }
-keep public class com.maheswara660.taskify.TaskListActivity { *; }

# Preserve Data Models (User, Task)
-keep class com.maheswara660.taskify.model.** {
    private <fields>;
    public <methods>;
    public <init>(...);
}

# Preserve Database Helper & SQLite classes
-keep class com.maheswara660.taskify.database.DatabaseHelper { *; }

# Preserve Utility Classes (PasswordUtils, SessionManager)
-keep class com.maheswara660.taskify.utils.** { *; }

# Preserve RecyclerView Adapter & ViewHolder
-keep class com.maheswara660.taskify.adapter.TaskAdapter { *; }
-keep class com.maheswara660.taskify.adapter.TaskAdapter$* { *; }

# Preserve AndroidX and Material Components used in XML layouts
-keep class androidx.appcompat.widget.** { *; }
-keep class com.google.android.material.** { *; }
-keep class androidx.recyclerview.widget.** { *; }
-keep class androidx.constraintlayout.widget.** { *; }

# Suppress non-fatal warnings
-dontwarn com.google.android.material.**
-dontwarn androidx.**
