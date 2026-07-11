# Krypton — Native Android Telegram Client

Bu — Telegram'ning rasmiy ochiq kodli Android ilovasi ([DrKLO/Telegram](https://github.com/DrKLO/Telegram)) asosidagi **to'liq native** fork (AyuGram, Plus Messenger, Nekogram kabi). Hech qanday WebView yoki brauzer texnologiyasi ishlatilmaydi — 100% Java/Kotlin + native C++ (JNI) kod.

Bu loyiha [Krypton PWA](https://github.com/abduvoitov11/Choyxona) dan **butunlay mustaqil** — ular turli texnologik stack'larda (bu native Android, u esa React/Capacitor) va birga ishlamaydi.

## Hozirgi holat (rebrand bosqichi)

✅ Bajarildi:
- Application ID: `app.krypton.messenger`
- Ilova nomi barcha asosiy tillarda: **Krypton**
- Ilova ikonkasi: barcha zichlik (mdpi–xxxhdpi) uchun yangi logotip bilan almashtirildi

⏳ Keyingi bosqich (hali bajarilmagan):
- UI mavzu ranglari (`.attheme` fayllar: `day.attheme`, `night.attheme` va h.k.) — Telegram'ning o'zining ichki mavzu formatida, yuzlab rang-kalitlari bor. Bu compile qilib, vizual tekshirish bilan birga qilinishi kerak bo'lgan nozik ish.
- O'zbek tili tarjimasi (`values-uz/strings.xml`) — rasmiy ilovada mavjud emas, qo'shilishi mumkin
- Splash screen / boot logotip

## Qanday compile qilish

Bu — professional darajadagi katta loyiha (~750MB manba kod). Compile qilish uchun kerak:
- **Android Studio** (eng so'nggi versiya)
- **Android NDK** (native C++ qismlar uchun — Android Studio orqali SDK Manager'dan o'rnatiladi)
- Kamida 16GB RAM tavsiya etiladi (build jarayoni og'ir)

```bash
git clone https://github.com/abduvoitov11/krypton-android-native.git
cd krypton-android-native
./gradlew assembleStandaloneRelease
```

Birinchi build juda uzoq davom etishi mumkin (native kutubxonalarni compile qilish tufayli — 30-60+ daqiqa, kompyuter kuchiga bog'liq).

## Litsenziya

Asl loyiha kabi GPL v2 litsenziyasi ostida (`LICENSE` faylga qarang).
