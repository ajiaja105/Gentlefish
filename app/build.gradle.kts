plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.gentlefin.aquarium"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.gentlefin.aquarium"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        // PENTING: pastikan APK mencakup HP 32-bit (spt kemungkinan besar
        // Vivo Y91 kamu) DAN 64-bit, bukan cuma salah satu. Ini utk jaga2
        // dari masalah "device tidak didukung" yg sempat dialami dgn
        // Capacitor kemarin - kalau APK cuma di-build utk arm64-v8a,
        // device 32-bit-only akan menolak instalasi.
        ndk {
            abiFilters += listOf("armeabi-v7a", "arm64-v8a")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            // CATATAN: sebelum bikin AAB buat submit ke Play Store, kamu
            // WAJIB tambahkan signingConfig (keystore) di sini. Play Store
            // menolak AAB yang tidak ditandatangani. Bikin keystore lewat
            // Android Studio (Build > Generate Signed Bundle) atau lewat
            // command line `keytool`. JANGAN pernah kirim file keystore
            // atau passwordnya ke siapapun termasuk AI manapun.
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.webkit:webkit:1.15.0")
}
