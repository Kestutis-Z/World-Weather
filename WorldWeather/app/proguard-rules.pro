# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-keep class sun.misc.Unsafe { *; }
#-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.examples.android.model.** { *; }

# AppCompat issue; see http://stackoverflow.com/questions/23413938/null-pointer-exception-
# when-using-searchview-with-appcompat
-keep class android.support.v7.widget.SearchView { *; }

-keepattributes SourceFile,LineNumberTable