# The proguard configuration file for the following section is /Users/rongkang/AndroidStudioProjects/PuzzleDemo/app/build/intermediates/proguard-files/proguard-android-optimize.txt-4.1.1
# This is a configuration file for ProGuard.
# http://proguard.sourceforge.net/index.html#manual/usage.html
#
# Starting with version 2.2 of the Android plugin for Gradle, this file is distributed together with
# the plugin and unpacked at build-time. The files in $ANDROID_HOME are no longer maintained and
# will be ignored by new version of the Android plugin for Gradle.

# Optimizations: If you don't want to optimize, use the proguard-android.txt configuration file
# instead of this one, which turns off the optimization flags.
# Adding optimization introduces certain risks, since for example not all optimizations performed by
# ProGuard works on all versions of Dalvik.  The following flags turn off various optimizations
# known to have issues, but the list may not be complete or up to date. (The "arithmetic"
# optimization can be used if you are only targeting Android 2.0 or later.)  Make sure you test
# thoroughly if you go this route.
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*
-optimizationpasses 5
-allowaccessmodification

-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-verbose

# Preserve some attributes that may be required for reflection.
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod

-keep public class com.google.vending.licensing.ILicensingService
-keep public class com.android.vending.licensing.ILicensingService
-keep public class com.google.android.vending.licensing.ILicensingService
-dontnote com.android.vending.licensing.ILicensingService
-dontnote com.google.vending.licensing.ILicensingService
-dontnote com.google.android.vending.licensing.ILicensingService

# For native methods, see http://proguard.sourceforge.net/manual/examples.html#native
-keepclasseswithmembernames,includedescriptorclasses class * {
    native <methods>;
}

# Keep setters in Views so that animations can still work.
-keepclassmembers public class * extends android.view.View {
    void set*(***);
    *** get*();
}

# We want to keep methods in Activity that could be used in the XML attribute onClick.
-keepclassmembers class * extends android.app.Activity {
    public void *(android.view.View);
}

# For enumeration classes, see http://proguard.sourceforge.net/manual/examples.html#enumerations
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keepclassmembers class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

# Preserve annotated Javascript interface methods.
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# The support libraries contains references to newer platform versions.
# Don't warn about those in case this app is linking against an older
# platform version. We know about them, and they are safe.
-dontnote android.support.**
-dontnote androidx.**
-dontwarn android.support.**
-dontwarn androidx.**

# This class is deprecated, but remains for backward compatibility.
-dontwarn android.util.FloatMath

# Understand the @Keep support annotation.
-keep class android.support.annotation.Keep
-keep class androidx.annotation.Keep

-keep @android.support.annotation.Keep class * {*;}
-keep @androidx.annotation.Keep class * {*;}

-keepclasseswithmembers class * {
    @android.support.annotation.Keep <methods>;
}

-keepclasseswithmembers class * {
    @androidx.annotation.Keep <methods>;
}

-keepclasseswithmembers class * {
    @android.support.annotation.Keep <fields>;
}

-keepclasseswithmembers class * {
    @androidx.annotation.Keep <fields>;
}

-keepclasseswithmembers class * {
    @android.support.annotation.Keep <init>(...);
}

-keepclasseswithmembers class * {
    @androidx.annotation.Keep <init>(...);
}

# These classes are duplicated between android.jar and org.apache.http.legacy.jar.
-dontnote org.apache.http.**
-dontnote android.net.http.**

# These classes are duplicated between android.jar and core-lambda-stubs.jar.
-dontnote java.lang.invoke.**

# End of content from /Users/rongkang/AndroidStudioProjects/PuzzleDemo/app/build/intermediates/proguard-files/proguard-android-optimize.txt-4.1.1
# The proguard configuration file for the following section is /Users/rongkang/AndroidStudioProjects/PuzzleDemo/app/proguard-rules.pro
# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
-printconfiguration ./proguard-project.pro
# End of content from /Users/rongkang/AndroidStudioProjects/PuzzleDemo/app/proguard-rules.pro
# The proguard configuration file for the following section is /Users/rongkang/AndroidStudioProjects/PuzzleDemo/app/build/intermediates/aapt_proguard_file/release/aapt_rules.txt
-keep class androidx.core.app.CoreComponentFactory { <init>(); }
-keep class androidx.room.MultiInstanceInvalidationService { <init>(); }
-keep class com.puzzle.PuzzleApplication { <init>(); }
-keep class com.puzzle.ui.ImageSelectActivity { <init>(); }
-keep class com.puzzle.ui.MainActivity { <init>(); }
-keep class com.puzzle.ui.SuccessActivity { <init>(); }
-keep class android.widget.Space { <init>(android.content.Context, android.util.AttributeSet); }

-keep class androidx.appcompat.app.AlertController$RecycleListView { <init>(android.content.Context, android.util.AttributeSet); }

-keep class androidx.appcompat.view.menu.ActionMenuItemView { <init>(android.content.Context, android.util.AttributeSet); }

-keep class androidx.appcompat.view.menu.ExpandedMenuView { <init>(android.content.Context, android.util.AttributeSet); }

-keep class androidx.appcompat.view.menu.ListMenuItemView { <init>(android.content.Context, android.util.AttributeSet); }

-keep class androidx.appcompat.widget.ActionBarContainer { <init>(android.content.Context, android.util.AttributeSet); }

-keep class androidx.appcompat.widget.ActionBarContextView { <init>(android.content.Context, android.util.AttributeSet); }

-keep class androidx.appcompat.widget.ActionBarOverlayLayout { <init>(android.content.Context, android.util.AttributeSet); }

-keep class androidx.appcompat.widget.ActionMenuView { <init>(android.content.Context, android.util.AttributeSet); }

-keep class androidx.appcompat.widget.ActivityChooserView$InnerLayout { <init>(android.content.Context, android.util.AttributeSet); }

-keep class androidx.appcompat.widget.AlertDialogLayout { <init>(android.content.Context, android.util.AttributeSet); }

-keep class androidx.appcompat.widget.AppCompatTextView { <init>(android.content.Context, android.util.AttributeSet); }

-keep class androidx.appcompat.widget.ButtonBarLayout { <init>(android.content.Context, android.util.AttributeSet); }

-keep class androidx.appcompat.widget.ContentFrameLayout { <init>(android.content.Context, android.util.AttributeSet); }

-keep class androidx.appcompat.widget.DialogTitle { <init>(android.content.Context, android.util.AttributeSet); }

-keep class androidx.appcompat.widget.FitWindowsFrameLayout { <init>(android.content.Context, android.util.AttributeSet); }

-keep class androidx.appcompat.widget.FitWindowsLinearLayout { <init>(android.content.Context, android.util.AttributeSet); }

-keep class androidx.appcompat.widget.SearchView$SearchAutoComplete { <init>(android.content.Context, android.util.AttributeSet); }

-keep class androidx.appcompat.widget.Toolbar { <init>(android.content.Context, android.util.AttributeSet); }

-keep class androidx.appcompat.widget.ViewStubCompat { <init>(android.content.Context, android.util.AttributeSet); }

-keep class androidx.constraintlayout.motion.widget.MotionLayout { <init>(android.content.Context, android.util.AttributeSet); }

-keep class androidx.constraintlayout.utils.widget.ImageFilterView { <init>(android.content.Context, android.util.AttributeSet); }

-keep class androidx.constraintlayout.widget.Barrier { <init>(android.content.Context, android.util.AttributeSet); }

-keep class androidx.constraintlayout.widget.ConstraintLayout { <init>(android.content.Context, android.util.AttributeSet); }

-keep class androidx.constraintlayout.widget.Guideline { <init>(android.content.Context, android.util.AttributeSet); }

-keep class androidx.coordinatorlayout.widget.CoordinatorLayout { <init>(android.content.Context, android.util.AttributeSet); }

-keep class androidx.core.widget.NestedScrollView { <init>(android.content.Context, android.util.AttributeSet); }

-keep class androidx.recyclerview.widget.RecyclerView { <init>(android.content.Context, android.util.AttributeSet); }

-keep class com.airbnb.lottie.LottieAnimationView { <init>(android.content.Context, android.util.AttributeSet); }

-keep class com.google.android.material.appbar.MaterialToolbar { <init>(android.content.Context, android.util.AttributeSet); }

-keep class com.google.android.material.button.MaterialButton { <init>(android.content.Context, android.util.AttributeSet); }

-keep class com.google.android.material.button.MaterialButtonToggleGroup { <init>(android.content.Context, android.util.AttributeSet); }

-keep class com.google.android.material.chip.Chip { <init>(android.content.Context, android.util.AttributeSet); }

-keep class com.google.android.material.chip.ChipGroup { <init>(android.content.Context, android.util.AttributeSet); }

-keep class com.google.android.material.datepicker.MaterialCalendarGridView { <init>(android.content.Context, android.util.AttributeSet); }

-keep class com.google.android.material.internal.BaselineLayout { <init>(android.content.Context, android.util.AttributeSet); }

-keep class com.google.android.material.internal.CheckableImageButton { <init>(android.content.Context, android.util.AttributeSet); }

-keep class com.google.android.material.internal.NavigationMenuItemView { <init>(android.content.Context, android.util.AttributeSet); }

-keep class com.google.android.material.internal.NavigationMenuView { <init>(android.content.Context, android.util.AttributeSet); }

-keep class com.google.android.material.snackbar.Snackbar$SnackbarLayout { <init>(android.content.Context, android.util.AttributeSet); }

-keep class com.google.android.material.snackbar.SnackbarContentLayout { <init>(android.content.Context, android.util.AttributeSet); }

-keep class com.google.android.material.tabs.TabLayout { <init>(android.content.Context, android.util.AttributeSet); }

-keep class com.google.android.material.textfield.TextInputEditText { <init>(android.content.Context, android.util.AttributeSet); }

-keep class com.google.android.material.textfield.TextInputLayout { <init>(android.content.Context, android.util.AttributeSet); }

-keep class com.google.android.material.textview.MaterialTextView { <init>(android.content.Context, android.util.AttributeSet); }

-keep class com.google.android.material.timepicker.ChipTextInputComboView { <init>(android.content.Context, android.util.AttributeSet); }

-keep class com.google.android.material.timepicker.ClockFaceView { <init>(android.content.Context, android.util.AttributeSet); }

-keep class com.google.android.material.timepicker.ClockHandView { <init>(android.content.Context, android.util.AttributeSet); }

-keep class com.google.android.material.timepicker.TimePickerView { <init>(android.content.Context, android.util.AttributeSet); }

-keep class com.puzzle.ui.view.ProgressView { <init>(android.content.Context, android.util.AttributeSet); }

-keep class com.puzzle.ui.view.PuzzleLayout { <init>(android.content.Context, android.util.AttributeSet); }


# End of content from /Users/rongkang/AndroidStudioProjects/PuzzleDemo/app/build/intermediates/aapt_proguard_file/release/aapt_rules.txt
# The proguard configuration file for the following section is /Users/rongkang/.gradle/caches/transforms-2/files-2.1/aee136b4dcf923c609c24ece02fd20f1/rules/lib/META-INF/com.android.tools/r8-from-1.6.0/coroutines.pro
# Allow R8 to optimize away the FastServiceLoader.
# Together with ServiceLoader optimization in R8
# this results in direct instantiation when loading Dispatchers.Main
-assumenosideeffects class kotlinx.coroutines.internal.MainDispatcherLoader {
    boolean FAST_SERVICE_LOADER_ENABLED return false;
}

-assumenosideeffects class kotlinx.coroutines.internal.FastServiceLoaderKt {
    boolean ANDROID_DETECTED return true;
}

-keep class kotlinx.coroutines.android.AndroidDispatcherFactory {*;}

# Disable support for "Missing Main Dispatcher", since we always have Android main dispatcher
-assumenosideeffects class kotlinx.coroutines.internal.MainDispatchersKt {
    boolean SUPPORT_MISSING return false;
}

# Statically turn off all debugging facilities and assertions
-assumenosideeffects class kotlinx.coroutines.DebugKt {
    boolean getASSERTIONS_ENABLED() return false;
    boolean getDEBUG() return false;
    boolean getRECOVER_STACK_TRACES() return false;
}
# End of content from /Users/rongkang/.gradle/caches/transforms-2/files-2.1/aee136b4dcf923c609c24ece02fd20f1/rules/lib/META-INF/com.android.tools/r8-from-1.6.0/coroutines.pro
# The proguard configuration file for the following section is /Users/rongkang/.gradle/caches/transforms-2/files-2.1/536a7ab67a663131d9977a921d195616/rules/lib/META-INF/proguard/coroutines.pro
# ServiceLoader support
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# Most of volatile fields are updated with AFU and should not be mangled
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

# Same story for the standard library's SafeContinuation that also uses AtomicReferenceFieldUpdater
-keepclassmembernames class kotlin.coroutines.SafeContinuation {
    volatile <fields>;
}

# These classes are only required by kotlinx.coroutines.debug.AgentPremain, which is only loaded when
# kotlinx-coroutines-core is used as a Java agent, so these are not needed in contexts where ProGuard is used.
-dontwarn java.lang.instrument.ClassFileTransformer
-dontwarn sun.misc.SignalHandler
-dontwarn java.lang.instrument.Instrumentation
-dontwarn sun.misc.Signal

# End of content from /Users/rongkang/.gradle/caches/transforms-2/files-2.1/536a7ab67a663131d9977a921d195616/rules/lib/META-INF/proguard/coroutines.pro
# The proguard configuration file for the following section is /Users/rongkang/.gradle/caches/transforms-2/files-2.1/0f0ddbaaa7ec4a137f209fea555e0f37/material-1.3.0/proguard.txt
# Copyright (C) 2015 The Android Open Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

# CoordinatorLayout resolves the behaviors of its child components with reflection.
-keep public class * extends androidx.coordinatorlayout.widget.CoordinatorLayout$Behavior {
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>();
}

# Make sure we keep annotations for CoordinatorLayout's DefaultBehavior
-keepattributes RuntimeVisible*Annotation*

# Copyright (C) 2018 The Android Open Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

# AppCompatViewInflater reads the viewInflaterClass theme attribute which then
# reflectively instantiates MaterialComponentsViewInflater using the no-argument
# constructor. We only need to keep this constructor and the class name if
# AppCompatViewInflater is also being kept.
-if class androidx.appcompat.app.AppCompatViewInflater
-keep class com.google.android.material.theme.MaterialComponentsViewInflater {
    <init>();
}


# End of content from /Users/rongkang/.gradle/caches/transforms-2/files-2.1/0f0ddbaaa7ec4a137f209fea555e0f37/material-1.3.0/proguard.txt
# The proguard configuration file for the following section is /Users/rongkang/.gradle/caches/transforms-2/files-2.1/f1f33b62267c75a6f99331b460a092b0/appcompat-1.2.0/proguard.txt
# Copyright (C) 2018 The Android Open Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

# aapt is not able to read app::actionViewClass and app:actionProviderClass to produce proguard
# keep rules. Add a commonly used SearchView to the keep list until b/109831488 is resolved.
-keep class androidx.appcompat.widget.SearchView { <init>(...); }

# Never inline methods, but allow shrinking and obfuscation.
-keepclassmembernames,allowobfuscation,allowshrinking class androidx.appcompat.widget.AppCompatTextViewAutoSizeHelper$Impl* {
  <methods>;
}

# End of content from /Users/rongkang/.gradle/caches/transforms-2/files-2.1/f1f33b62267c75a6f99331b460a092b0/appcompat-1.2.0/proguard.txt
# The proguard configuration file for the following section is /Users/rongkang/.gradle/caches/transforms-2/files-2.1/2a5145feb45f1c67f76a6ccfe20dac14/jetified-glide-4.11.0/proguard.txt
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule {
 <init>(...);
}
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

# Uncomment for DexGuard only
#-keepresourcexmlelements manifest/application/meta-data@value=GlideModule

# End of content from /Users/rongkang/.gradle/caches/transforms-2/files-2.1/2a5145feb45f1c67f76a6ccfe20dac14/jetified-glide-4.11.0/proguard.txt
# The proguard configuration file for the following section is /Users/rongkang/.gradle/caches/transforms-2/files-2.1/9e7e243a254b6b2e33f15b3e2a6881d0/jetified-webpdecoder-2.0.4.11.0/proguard.txt
# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Android\SDK/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-keep public class com.bumptech.glide.integration.webp.WebpImage { *; }
-keep public class com.bumptech.glide.integration.webp.WebpFrame { *; }
-keep public class com.bumptech.glide.integration.webp.WebpBitmapFactory { *; }

# End of content from /Users/rongkang/.gradle/caches/transforms-2/files-2.1/9e7e243a254b6b2e33f15b3e2a6881d0/jetified-webpdecoder-2.0.4.11.0/proguard.txt
# The proguard configuration file for the following section is /Users/rongkang/.gradle/caches/transforms-2/files-2.1/cc11c19d29ce29262dd4524ebf21f51c/rules/lib/META-INF/proguard/retrofit2.pro
# Retrofit does reflection on generic parameters. InnerClasses is required to use Signature and
# EnclosingMethod is required to use InnerClasses.
-keepattributes Signature, InnerClasses, EnclosingMethod

# Retrofit does reflection on method and parameter annotations.
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations

# Retain service method parameters when optimizing.
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# Ignore annotation used for build tooling.
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

# Ignore JSR 305 annotations for embedding nullability information.
-dontwarn javax.annotation.**

# Guarded by a NoClassDefFoundError try/catch and only used when on the classpath.
-dontwarn kotlin.Unit

# Top-level functions that can only be used by Kotlin.
-dontwarn retrofit2.KotlinExtensions
-dontwarn retrofit2.KotlinExtensions$*

# With R8 full mode, it sees no subtypes of Retrofit interfaces since they are created with a Proxy
# and replaces all potential values with null. Explicitly keeping the interfaces prevents this.
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface <1>

# End of content from /Users/rongkang/.gradle/caches/transforms-2/files-2.1/cc11c19d29ce29262dd4524ebf21f51c/rules/lib/META-INF/proguard/retrofit2.pro
# The proguard configuration file for the following section is /Users/rongkang/.gradle/caches/transforms-2/files-2.1/95a24a7a4967989859826e263fe3e390/room-runtime-2.2.6/proguard.txt
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# End of content from /Users/rongkang/.gradle/caches/transforms-2/files-2.1/95a24a7a4967989859826e263fe3e390/room-runtime-2.2.6/proguard.txt
# The proguard configuration file for the following section is /Users/rongkang/.gradle/caches/transforms-2/files-2.1/bf138d0b71de1e0a44db1c948a8d35cd/coordinatorlayout-1.1.0/proguard.txt
# Copyright (C) 2016 The Android Open Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

# CoordinatorLayout resolves the behaviors of its child components with reflection.
-keep public class * extends androidx.coordinatorlayout.widget.CoordinatorLayout$Behavior {
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>();
}

# Make sure we keep annotations for CoordinatorLayout's DefaultBehavior and ViewPager's DecorView
-keepattributes *Annotation*

# End of content from /Users/rongkang/.gradle/caches/transforms-2/files-2.1/bf138d0b71de1e0a44db1c948a8d35cd/coordinatorlayout-1.1.0/proguard.txt
# The proguard configuration file for the following section is /Users/rongkang/.gradle/caches/transforms-2/files-2.1/579188acc4336cbc34fb9d8999a11744/transition-1.2.0/proguard.txt
# Copyright (C) 2017 The Android Open Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

# Keep a field in transition that is used to keep a reference to weakly-referenced object
-keepclassmembers class androidx.transition.ChangeBounds$* extends android.animation.AnimatorListenerAdapter {
  androidx.transition.ChangeBounds$ViewBounds mViewBounds;
}

# End of content from /Users/rongkang/.gradle/caches/transforms-2/files-2.1/579188acc4336cbc34fb9d8999a11744/transition-1.2.0/proguard.txt
# The proguard configuration file for the following section is /Users/rongkang/.gradle/caches/transforms-2/files-2.1/db6f362f2f21e586a6b40eca3796432d/vectordrawable-animated-1.1.0/proguard.txt
# Copyright (C) 2016 The Android Open Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

# keep setters in VectorDrawables so that animations can still work.
-keepclassmembers class androidx.vectordrawable.graphics.drawable.VectorDrawableCompat$* {
   void set*(***);
   *** get*();
}

# End of content from /Users/rongkang/.gradle/caches/transforms-2/files-2.1/db6f362f2f21e586a6b40eca3796432d/vectordrawable-animated-1.1.0/proguard.txt
# The proguard configuration file for the following section is /Users/rongkang/.gradle/caches/transforms-2/files-2.1/b5569fc576b930571ca8a99adbf2b8a6/fragment-1.3.0-beta01/proguard.txt
# Copyright (C) 2020 The Android Open Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

# The default FragmentFactory creates Fragment instances using reflection
-if public class ** extends androidx.fragment.app.Fragment
-keepclasseswithmembers,allowobfuscation public class <1> {
    public <init>();
}

# End of content from /Users/rongkang/.gradle/caches/transforms-2/files-2.1/b5569fc576b930571ca8a99adbf2b8a6/fragment-1.3.0-beta01/proguard.txt
# The proguard configuration file for the following section is /Users/rongkang/.gradle/caches/transforms-2/files-2.1/0a04e9946b744ea06f6fbe6dd9b1e1ef/recyclerview-1.1.0/proguard.txt
# Copyright (C) 2015 The Android Open Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

# When layoutManager xml attribute is used, RecyclerView inflates
#LayoutManagers' constructors using reflection.
-keep public class * extends androidx.recyclerview.widget.RecyclerView$LayoutManager {
    public <init>(android.content.Context, android.util.AttributeSet, int, int);
    public <init>();
}

-keepclassmembers class androidx.recyclerview.widget.RecyclerView {
    public void suppressLayout(boolean);
    public boolean isLayoutSuppressed();
}
# End of content from /Users/rongkang/.gradle/caches/transforms-2/files-2.1/0a04e9946b744ea06f6fbe6dd9b1e1ef/recyclerview-1.1.0/proguard.txt
# The proguard configuration file for the following section is /Users/rongkang/.gradle/caches/transforms-2/files-2.1/79ee559483d346866425453a88a27286/core-1.3.2/proguard.txt
# Never inline methods, but allow shrinking and obfuscation.
-keepclassmembernames,allowobfuscation,allowshrinking class androidx.core.view.ViewCompat$Api* {
  <methods>;
}
-keepclassmembernames,allowobfuscation,allowshrinking class androidx.core.view.WindowInsetsCompat$Impl* {
  <methods>;
}
-keepclassmembernames,allowobfuscation,allowshrinking class androidx.core.view.WindowInsetsCompat$BuilderImpl* {
  <methods>;
}
# End of content from /Users/rongkang/.gradle/caches/transforms-2/files-2.1/79ee559483d346866425453a88a27286/core-1.3.2/proguard.txt
# The proguard configuration file for the following section is /Users/rongkang/.gradle/caches/transforms-2/files-2.1/4b7c7d2fea271b9775df9dbec2d1fbdc/versionedparcelable-1.1.0/proguard.txt
-keep public class * implements androidx.versionedparcelable.VersionedParcelable
-keep public class android.support.**Parcelizer { *; }
-keep public class androidx.**Parcelizer { *; }
-keep public class androidx.versionedparcelable.ParcelImpl

# End of content from /Users/rongkang/.gradle/caches/transforms-2/files-2.1/4b7c7d2fea271b9775df9dbec2d1fbdc/versionedparcelable-1.1.0/proguard.txt
# The proguard configuration file for the following section is /Users/rongkang/.gradle/caches/transforms-2/files-2.1/786686d89962c61ee9b9724523208c2b/jetified-lifecycle-viewmodel-savedstate-2.3.0-beta01/proguard.txt
-keepclassmembers,allowobfuscation class * extends androidx.lifecycle.ViewModel {
    <init>(androidx.lifecycle.SavedStateHandle);
}

-keepclassmembers,allowobfuscation class * extends androidx.lifecycle.AndroidViewModel {
    <init>(android.app.Application,androidx.lifecycle.SavedStateHandle);
}

# End of content from /Users/rongkang/.gradle/caches/transforms-2/files-2.1/786686d89962c61ee9b9724523208c2b/jetified-lifecycle-viewmodel-savedstate-2.3.0-beta01/proguard.txt
# The proguard configuration file for the following section is /Users/rongkang/.gradle/caches/transforms-2/files-2.1/fe7704cfa407d95cb46e692d499491a0/lifecycle-runtime-2.3.0-beta01/proguard.txt
-keepattributes *Annotation*

-keepclassmembers enum androidx.lifecycle.Lifecycle$Event {
    <fields>;
}

-keep !interface * implements androidx.lifecycle.LifecycleObserver {
}

-keep class * implements androidx.lifecycle.GeneratedAdapter {
    <init>(...);
}

-keepclassmembers class ** {
    @androidx.lifecycle.OnLifecycleEvent *;
}

# this rule is need to work properly when app is compiled with api 28, see b/142778206
# Also this rule prevents registerIn from being inlined.
-keepclassmembers class androidx.lifecycle.ReportFragment$LifecycleCallbacks { *; }
# End of content from /Users/rongkang/.gradle/caches/transforms-2/files-2.1/fe7704cfa407d95cb46e692d499491a0/lifecycle-runtime-2.3.0-beta01/proguard.txt
# The proguard configuration file for the following section is /Users/rongkang/.gradle/caches/transforms-2/files-2.1/2dc92b819e986abdaec68b94430f30b1/jetified-savedstate-1.1.0-beta01/proguard.txt
# Copyright (C) 2019 The Android Open Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

-keepclassmembers,allowobfuscation class * implements androidx.savedstate.SavedStateRegistry$AutoRecreated {
    <init>();
}

# End of content from /Users/rongkang/.gradle/caches/transforms-2/files-2.1/2dc92b819e986abdaec68b94430f30b1/jetified-savedstate-1.1.0-beta01/proguard.txt
# The proguard configuration file for the following section is /Users/rongkang/.gradle/caches/transforms-2/files-2.1/b529122dcfe35591eb329ddd4d1d1e82/lifecycle-viewmodel-2.3.0-beta01/proguard.txt
-keepclassmembers,allowobfuscation class * extends androidx.lifecycle.ViewModel {
    <init>();
}

-keepclassmembers,allowobfuscation class * extends androidx.lifecycle.AndroidViewModel {
    <init>(android.app.Application);
}

# End of content from /Users/rongkang/.gradle/caches/transforms-2/files-2.1/b529122dcfe35591eb329ddd4d1d1e82/lifecycle-viewmodel-2.3.0-beta01/proguard.txt
# The proguard configuration file for the following section is /Users/rongkang/.gradle/caches/transforms-2/files-2.1/cdea66bf245763fcb8f39dfe52ade28e/rules/lib/META-INF/proguard/androidx-annotations.pro
-keep,allowobfuscation @interface androidx.annotation.Keep
-keep @androidx.annotation.Keep class * {*;}

-keepclasseswithmembers class * {
    @androidx.annotation.Keep <methods>;
}

-keepclasseswithmembers class * {
    @androidx.annotation.Keep <fields>;
}

-keepclasseswithmembers class * {
    @androidx.annotation.Keep <init>(...);
}

# End of content from /Users/rongkang/.gradle/caches/transforms-2/files-2.1/cdea66bf245763fcb8f39dfe52ade28e/rules/lib/META-INF/proguard/androidx-annotations.pro
# The proguard configuration file for the following section is /Users/rongkang/.gradle/caches/transforms-2/files-2.1/09a22b7debb3afa9beb5a52943535571/rules/lib/META-INF/proguard/okhttp3.pro
# JSR 305 annotations are for embedding nullability information.
-dontwarn javax.annotation.**

# A resource is loaded with a relative path so the package of this class must be preserved.
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

# Animal Sniffer compileOnly dependency to ensure APIs are compatible with older versions of Java.
-dontwarn org.codehaus.mojo.animal_sniffer.*

# OkHttp platform used only on JVM and when Conscrypt dependency is available.
-dontwarn okhttp3.internal.platform.ConscryptPlatform

# End of content from /Users/rongkang/.gradle/caches/transforms-2/files-2.1/09a22b7debb3afa9beb5a52943535571/rules/lib/META-INF/proguard/okhttp3.pro
# The proguard configuration file for the following section is <unknown>
-ignorewarnings
# End of content from <unknown>