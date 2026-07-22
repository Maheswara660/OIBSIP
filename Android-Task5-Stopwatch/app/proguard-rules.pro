# ProGuard / R8 Configuration Rules for Chrono Stopwatch App

# Preserve general annotations and signatures
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod,SourceFile,LineNumberTable

# Preserve source file names and line numbers for crash trace readability
-renamesourcefileattribute SourceFile

# Keep Application Entry Points & Manifest-Bound Activities
-keep public class com.maheswara660.chrono.MainActivity {
    public <init>();
    protected void onCreate(android.os.Bundle);
}

# Keep Data Model Classes (POJOs)
-keep class com.maheswara660.chrono.LapItem {
    public <init>(int, long, long);
    public int getLapNumber();
    public long getSplitTimeMillis();
    public long getTotalTimeMillis();
    public java.lang.String getLapNumberFormatted();
    public java.lang.String getSplitTimeFormatted();
    public java.lang.String getTotalTimeFormatted();
    public static java.lang.String formatMillis(long);
}

# Keep RecyclerView Adapter & ViewHolder
-keep class com.maheswara660.chrono.LapAdapter { *; }
-keep class com.maheswara660.chrono.LapAdapter$LapViewHolder { *; }

# Keep View Constructors for XML Layout Inflation
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# Preserve AndroidX AppCompat & Material Component Views
-keep class androidx.appcompat.** { *; }
-keep class com.google.android.material.** { *; }
-keep class androidx.recyclerview.widget.** { *; }
-keep class androidx.constraintlayout.widget.** { *; }
-keep class androidx.core.view.** { *; }

# Suppress harmless missing dependency warnings during optimization
-dontwarn androidx.appcompat.**
-dontwarn com.google.android.material.**
-dontwarn androidx.constraintlayout.**
