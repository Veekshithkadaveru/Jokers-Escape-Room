# ─── Gson data models (JSON deserialization via reflection) ──────────────────
-keep class app.krafted.jokersescaperoom.data.model.** { *; }
-keepclassmembers class app.krafted.jokersescaperoom.data.model.** { *; }

# ─── Enums (PuzzleType.valueOf() / values()) ─────────────────────────────────
-keepclassmembers enum app.krafted.jokersescaperoom.** {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# ─── Room entities and DAOs ──────────────────────────────────────────────────
-keep class app.krafted.jokersescaperoom.data.db.** { *; }
-keepclassmembers class app.krafted.jokersescaperoom.data.db.** { *; }

# ─── Gson generic type handling ──────────────────────────────────────────────
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# ─── Keep line numbers for crash reporting ───────────────────────────────────
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
