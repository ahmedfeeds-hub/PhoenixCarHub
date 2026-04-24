# Keep data model classes for Gson serialization
-keep class com.phoenix.carhub.data.model.** { *; }
-keep class com.phoenix.carhub.data.repository.** { *; }

# Retrofit
-keepattributes Signature
-keepattributes Exceptions
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**

# Gson
-keep class com.google.gson.** { *; }
-keepattributes *Annotation*

# Hilt
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }

# Google Maps
-keep class com.google.android.gms.maps.** { *; }

# Media
-keep class android.support.v4.media.** { *; }
-keep class androidx.media.** { *; }
