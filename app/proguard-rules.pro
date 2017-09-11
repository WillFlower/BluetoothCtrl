# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\Users\WGH\AppData\Local\Android\sdk/tools/proguard/proguard-android.txt
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
-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
-renamesourcefileattribute SourceFile

#-----------------混淆配置设定------------------------------------------------------------------------
-optimizationpasses 9                                                       #指定代码压缩级别
-dontusemixedcaseclassnames                                                 #混淆时不会产生形形色色的类名
-dontskipnonpubliclibraryclasses                                            #指定不忽略非公共类库
-dontpreverify                                                              #不预校验，如果需要预校验，是-dontoptimize
-ignorewarnings                                                             #屏蔽警告
-verbose                                                                    #混淆时记录日志
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*    #优化

#-----------------导入第三方包,但是在当前版本中使用会报 input jar file is specified twice 错误，所以注释掉
#-libraryjars libs/android.support.v4.jar
#-libraryjars libs/xUtils-2.6.14.jar

#-----------------不需要混淆第三方类库------------------------------------------------------------------
#-dontwarn android.support.v4.**                                             #去掉警告
#-keep class android.support.v4.** { *; }                                    #过滤android.support.v4
#-keep interface android.support.v4.app.** { *; }
#-keep public class * extends android.support.v4.**
#-keep public class * extends android.app.Fragment
#
#-keep class org.apache.**{*;}                                               #过滤commons-httpclient-3.1.jar
#-keep class com.fasterxml.jackson.**{*;}                                    #过滤jackson-core-2.1.4.jar等
#
#-dontwarn com.lidroid.xutils.**                                             #去掉警告
#-keep class com.lidroid.xutils.**{*;}                                       #过滤xUtils-2.6.14.jar
#-keep class * extends java.lang.annotation.Annotation{*;}                   #这是xUtils文档中提到的过滤掉注解
#
#-dontwarn com.baidu.**                                                      #去掉警告
#-dontwarn com.baidu.mapapi.**
#-keep class com.baidu.** {*;}                                               #过滤BaiduLBS_Android.jar
#-keep class vi.com.gdi.bgl.android.**{*;}
#-keep class com.baidu.platform.**{*;}
#-keep class com.baidu.location.**{*;}
#-keep class com.baidu.vi.**{*;}

#-----------------不需要混淆系统组件等------------------------------------------------------------------
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService

-keep class com.wgh.bluetoothctrl.bean.**{*;}                                #过滤掉自己编写的实体类


##----------------保护指定的类和类的成员，但条件是所有指定的类和类成员是要存在-----------------------------------
-keepclasseswithmembernames class * {
    public <init>(android.content.Context);
}

-keepclasseswithmembernames class * {
    public <init>(android.content.Context, int);
}
