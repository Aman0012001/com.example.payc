# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.

# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# ===================================
# PayC Production ProGuard Rules
# ===================================

# Keep source file names and line numbers for better crash reports
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Keep all annotations
-keepattributes *Annotation*

# Keep generic signatures for reflection
-keepattributes Signature

# Keep exception information
-keepattributes Exceptions

# ===================================
# Kotlin
# ===================================
-dontwarn kotlin.**
-keep class kotlin.** { *; }
-keep class kotlin.Metadata { *; }
-keepclassmembers class **$WhenMappings {
    <fields>;
}
-keepclassmembers class kotlin.Metadata {
    public <methods>;
}

# Kotlin Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

# ===================================
# AndroidX & Jetpack Compose
# ===================================
-keep class androidx.** { *; }
-keep interface androidx.** { *; }
-dontwarn androidx.**

# Compose
-keep class androidx.compose.** { *; }
-keep interface androidx.compose.** { *; }
-dontwarn androidx.compose.**

# Navigation
-keep class androidx.navigation.** { *; }
-keepnames class androidx.navigation.fragment.NavHostFragment

# Lifecycle
-keep class androidx.lifecycle.** { *; }
-dontwarn androidx.lifecycle.**

# ===================================
# Security - EncryptedSharedPreferences
# ===================================
-keep class androidx.security.crypto.** { *; }
-keep class com.google.crypto.tink.** { *; }
-dontwarn com.google.crypto.tink.**

# Keep SecureTokenManager
-keep class com.example.payc.data.SecureTokenManager { *; }
-keepclassmembers class com.example.payc.data.SecureTokenManager {
    public <methods>;
}

# ===================================
# Networking & JSON
# ===================================
# Keep data classes used for JSON serialization
-keep class com.example.payc.data.** { *; }
-keepclassmembers class com.example.payc.data.** {
    <fields>;
    <init>(...);
}

# Keep API models
-keep class com.example.payc.models.** { *; }
-keepclassmembers class com.example.payc.models.** {
    <fields>;
    <init>(...);
}

# ===================================
# Payment - Razorpay
# ===================================
-keep class com.razorpay.** { *; }
-dontwarn com.razorpay.**
-keepclassmembers class com.razorpay.** {
    *;
}

# ===================================
# Image Loading - Coil
# ===================================
-keep class coil.** { *; }
-keep interface coil.** { *; }
-dontwarn coil.**

# ===================================
# App Specific
# ===================================
# Keep MainActivity
-keep class com.example.payc.MainActivity { *; }

# Keep all screens
-keep class com.example.payc.ui.screens.** { *; }

# Keep validation utils
-keep class com.example.payc.utils.ValidationUtils { *; }
-keepclassmembers class com.example.payc.utils.ValidationUtils {
    public static <methods>;
}

# Keep navigation
-keep class com.example.payc.ui.navigation.** { *; }

# Keep theme
-keep class com.example.payc.ui.theme.** { *; }

# ===================================
# Reflection
# ===================================
# Keep classes that use reflection
-keepclassmembers class * {
    @androidx.compose.runtime.Composable <methods>;
}

# ===================================
# Enums
# ===================================
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# ===================================
# Parcelable
# ===================================
-keepclassmembers class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

# ===================================
# Serializable
# ===================================
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# ===================================
# Remove Logging in Production
# ===================================
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}

# ===================================
# Optimization
# ===================================
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-verbose

# ===================================
# Warnings to Ignore
# ===================================
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**
