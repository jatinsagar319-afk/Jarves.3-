<?xml version="1.0" encoding="utf-8"?>

<manifest xmlns:android="http://schemas.android.com/apk/res/android">

    <!-- Internet: AI backend / future live news -->
    <uses-permission android:name="android.permission.INTERNET" />

    <!-- Microphone -->
    <uses-permission android:name="android.permission.RECORD_AUDIO" />

    <!-- Foreground service -->
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE_MICROPHONE" />

    <!-- Notifications -->
    <uses-permission android:name="android.permission.POST_NOTIFICATIONS" />

    <!-- Exact alarms / reminders -->
    <uses-permission android:name="android.permission.SCHEDULE_EXACT_ALARM" />

    <!-- Allow JARVIS to check whether these apps are installed -->
    <queries>

        <package android:name="com.whatsapp" />

        <package android:name="com.google.android.youtube" />

        <package android:name="com.android.chrome" />

        <package android:name="com.instagram.android" />

    </queries>

    <application
        android:allowBackup="true"
        android:icon="@android:drawable/ic_btn_speak_now"
        android:label="JARVIS"
        android:roundIcon="@android:drawable/ic_btn_speak_now"
        android:supportsRtl="true"
        android:theme="@style/Theme.Jarvis">

        <!-- Main JARVIS screen -->
        <activity
            android:name=".MainActivity"
            android:exported="true">

            <intent-filter>

                <action android:name="android.intent.action.MAIN" />

                <category android:name="android.intent.category.LAUNCHER" />

            </intent-filter>

        </activity>

        <!-- Voice / background assistant service -->
        <service
            android:name=".JarvisService"
            android:enabled="true"
            android:exported="false"
            android:foregroundServiceType="microphone" />

        <!-- Reminder receiver -->
        <receiver
            android:name=".AlarmReceiver"
            android:enabled="true"
            android:exported="false" />

    </application>

</manifest>
