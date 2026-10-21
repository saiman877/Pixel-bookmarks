# Proguard rules for Pixelmarks
-keepattributes *Annotation*
-keepclassmembers class * {
    @androidx.room.* *;
}
