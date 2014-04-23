MustSee-Android-App
===================

Applicació en Android per connectar al servidor MustSee.

Per que funcioni correctament s'ha d'afegir l'arxiu AndroidManifest.xml amb el següent contingut, però amb la vostra API de Google Maps:

<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
          package="ioc.mustsee">

    <permission
        android:name="ioc.mustsee.permission.MAPS_RECEIVE"
        android:protectionLevel="signature"/>

    <uses-feature
        android:glEsVersion="0x00020000"
        android:required="true"/>

    <uses-permission android:name="ioc.mustsee.permission.MAPS_RECEIVE"/>
    <uses-permission android:name="com.google.android.providers.gsf.permission.READ_GSERVICES"/>
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
    <uses-permission android:name="android.permission.INTERNET"/>
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>

    <application
        android:allowBackup="true"
        android:icon="@drawable/ic_launcher"
        android:label="@string/app_name"
        android:theme="@style/AppTheme">

        <activity
            android:name=".activities.MainActivity"
            android:label="@string/app_name"
            android:screenOrientation="landscape">
            <intent-filter>
                <action android:name="android.intent.action.MAIN"/>

                <category android:name="android.intent.category.LAUNCHER"/>
            </intent-filter>
        </activity>

        <meta-data
            android:name="com.google.android.gms.version"
            android:value="@integer/google_play_services_version"/>

        <meta-data
            android:name="com.google.android.maps.v2.API_KEY"
            android:value="la vostra clau"/>

    </application>


