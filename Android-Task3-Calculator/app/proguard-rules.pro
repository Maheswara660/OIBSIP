# QuickCalc - ProGuard / R8 Optimization & Obfuscation Rules

# Keep basic reflection attributes and line numbers for diagnostic crash stack traces
-keepattributes *Annotation*,Signature,InnerClasses,SourceFile,LineNumberTable

# Preserve main activity entry points referenced in AndroidManifest.xml
-keep public class com.maheswara660.quickcalc.MainActivity {
    public <init>();
    public void *(android.view.View);
}

# Preserve AndroidX AppCompat components used in XML layout inflation
-keep class androidx.appcompat.widget.** { *; }
-keep class androidx.appcompat.app.** { *; }

# Preserve Material Components (MaterialButton, MaterialCardView) used in layouts
-keep class com.google.android.material.** { *; }

# Preserve ConstraintLayout and GridLayout components
-keep class androidx.constraintlayout.widget.** { *; }
-keep class android.widget.GridLayout { *; }

# Keep view constructors called during XML layout inflation
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# Suppress harmless R8 optimization warnings
-dontwarn androidx.appcompat.**
-dontwarn com.google.android.material.**
