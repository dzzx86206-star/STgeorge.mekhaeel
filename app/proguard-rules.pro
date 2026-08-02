# Add project specific ProGuard rules here.
# Firestore/Auth model classes need their fields kept for reflection-based deserialization.
-keep class com.stgeorge.church.models.** { *; }
