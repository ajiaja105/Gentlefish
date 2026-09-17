# Gentlefin 3D - App Android (buat Play Store)

Ini APP BIASA (bukan wallpaper) - WebView di dalam Activity normal.
Jauh lebih stabil dibanding versi wallpaper kemarin karena tidak ada
masalah WebGL-gagal-bikin-context.

## Status aset
Semua aset (model .glb, tekstur, suara, library JS) SUDAH lengkap
di app/src/main/assets/, diambil langsung dari paket CodeCanyon final
kamu. Tidak perlu tambah apapun lagi secara manual - tinggal build.

## Cara pakai lewat Termux + GitHub (sesuai yang kamu mau)

1. **Bikin akun GitHub** kalau belum punya (gratis) - github.com
2. **Bikin repository baru** (bisa Private, tidak wajib Public)
3. **Di Termux**, install git: `pkg install git`
4. **Upload folder proyek ini** ke repo GitHub kamu:
   ```
   cd AquariumApp
   git init
   git add .
   git commit -m "initial"
   git branch -M main
   git remote add origin https://github.com/USERNAME/NAMA_REPO.git
   git push -u origin main
   ```
   (ganti USERNAME dan NAMA_REPO sesuai punya kamu; GitHub akan minta
   login/token saat push - ikuti instruksi yang muncul)
5. **Tunggu ~2-5 menit.** GitHub otomatis build APK-nya karena ada file
   `.github/workflows/build.yml`.
6. **Cek hasilnya:** buka repo di github.com lewat browser HP → tab
   **"Actions"** → klik run yang paling atas → scroll ke bawah →
   download **"aquarium-app-debug"** (file .zip berisi APK).
7. Extract zip itu, install APK-nya di HP kamu → coba jalankan.

## Kalau masih gagal/tidak normal
Kirim ke saya:
- Screenshot pas app dibuka (layar hitam? ada tulisan error? ikan
  diam?)
- Kalau bisa, buka Logcat lewat app kayak "aLogcat" atau serupa,
  cari baris yang ada tulisan "WebGL" atau "GL Error"

Tapi harapannya kali ini jalan lebih normal - karena WebView di sini
attached ke window sungguhan (beda dari versi wallpaper kemarin).

## 1 risiko lain yang belum bisa saya pastikan
Dokumentasi kamu sendiri bilang: index.html tidak bisa dibuka
langsung lewat file:// di desktop browser karena pakai ES Modules
(kena CORS). Nah, WebView Android load index.html ini lewat
`file:///android_asset/index.html` - itu SECARA UMUM lebih longgar
dibanding file:// biasa di desktop (Android punya penanganan khusus
utk skema android_asset), tapi saya belum bisa pastikan 100% ini
akan mulus di semua versi WebView Android tanpa dicoba langsung.
Kalau muncul layar putih/kosong dengan error di Logcat yang
menyebut "CORS" atau "module", itu tandanya masalah ini - beda dari
masalah WebGL yang sebelumnya. Kabari saya kalau ketemu error jenis
ini, biar dicari solusinya (biasanya solusinya bukan ubah skrip,
tapi ubah cara WebView memuatnya, misal lewat WebViewAssetLoader).

## Sebelum submit ke Play Store (langkah masih panjang, ini baru awal)
- [ ] Ganti icon placeholder (res/drawable/ic_launcher_*.xml) dengan
      logo asli
- [ ] Bikin keystore & signing config (WAJIB, Play Store tolak APK
      tidak ditandatangani) - JANGAN taruh keystore/password di repo
      GitHub, apalagi kalau repo-nya Public
- [ ] Build AAB (bukan APK) buat submit - `gradle bundleRelease`
- [ ] Daftar Google Play Console (biaya sekali $25)
- [ ] Siapkan: screenshot, feature graphic, deskripsi, kebijakan
      privasi (Play Store WAJIB ada privacy policy meski app-nya
      offline)
- [ ] Isi content rating questionnaire
