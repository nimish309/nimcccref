-dontoptimize
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-verbose
-ignorewarnings
-keepdirectories
-dontnote
-keepattributes Exceptions,InnerClasses,Signature,Deprecated,
                SourceFile,LineNumberTable,*Annotation*,EnclosingMethod
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

#-libraryjars C:/Users/Hobbs/Closr/Closr/libs/aws-android-sdk-1.7.1.1.jar

-keep public class com.google.vending.licensing.ILicensingService
-keep public class com.android.vending.licensing.ILicensingService
-keep public class org.apache.commons.** { *; }
-keep public class com.nostra13.universalimageloader.** { *; }
-keep public class uk.co.senab.actionbarpulltorefresh.** { *; }
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.content.Context
-keep public class * extends android.app.Activity
-keep public class * extends android.support.v4.app.Activity
-keep public class * extends android.app.Fragment
-keep public class * extends android.support.v4.app.Fragment

-keep class com.google.inject.** { *; }
-keep class com.facebook.** { *; }
-keep class android.os.** { *; }
-keep class com.google.android.gms.maps.** { *; }
-keep class com.google.android.gms.auth.** { *; }
-keep class com.google.android.gms.common.** { *; }
-keep class com.google.android.gms.location.** { *; }
-keep class com.google.android.gms.gcm.** { *; }
-keep class com.google.android.gms.internal.** { *; }
-keep class com.google.android.finsky.utils.** { *; }
-keep class com.amazonaws.services.sqs.QueueUrlHandler  { *; }
-keep class com.amazonaws.javax.xml.transform.sax.*     { public *; }
-keep class com.amazonaws.javax.xml.stream.**           { *; }
-keep class com.amazonaws.services.**.model.*Exception* { *; }
-keep class org.codehaus.**                             { *; }
-keep class javax.inject.** { *; }
-keep class javax.annotation.** { *; }
-keep class com.amazonaws.** { *; }
-keep class org.joda.convert.*                          { *; }
-keepnames class com.fasterxml.jackson.**               { *; }

-keep class com.closr.closr.** { *; }
-keep interface com.closr.closr.** { *; }

-keepclassmembers class com.closr.closr.** {
    public static <fields>;
}

-keepclasseswithmembernames class * {
    native <methods>;
}
-keepclassmembers public class * extends android.view.View {
   void set*(***);
   *** get*();
}
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}
-keepclassmembers class **.R$* {
    public static <fields>;
}


-dontwarn javax.xml.stream.events.**
-dontwarn org.codehaus.jackson.**
-dontwarn org.apache.commons.logging.impl.**
-dontwarn org.apache.http.conn.scheme.**
-dontwarn com.amazonaws.**




-dontwarn butterknife.internal.**

-keep class **$$ViewInjector { *; }

-keepnames class * { @butterknife.InjectView *;}

-dontwarn butterknife.Views$InjectViewProcessor

# support design
-dontwarn android.support.design.**
-keep class android.support.design.** { *; }
-keep interface android.support.design.** { *; }
-keep public class android.support.design.R$* { *; }

# support v4
-dontwarn android.support.v4.**
-keep class android.support.v4.** { *; }
-keep interface android.support.v4.** { *; }
-keep public class android.support.v4.R$* { *; }

# support v7
-dontwarn android.support.v7.**
-keep class android.support.v7.** { *; }
-keep interface android.support.v7.** { *; }
-keep public class android.support.v7.R$* { *; }



# --------------------------------------------------------------------
# REMOVE all Log messages except warnings and errors
# --------------------------------------------------------------------
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}