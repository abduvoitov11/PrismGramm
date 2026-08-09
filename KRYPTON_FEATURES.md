# Krypton — "O'chirilgan xabarlarni saqlash" funksiyasi

AyuGram uslubidagi anti-delete funksiyasi. Hozirgi holat va ishlash tamoyili quyida yozilgan.

## ✅ Bajarildi: Backend (xabarni arxivlash)

**Fayl:** `TMessagesProj/src/main/java/org/telegram/messenger/KryptonArchive.java`

Yangi, butunlay mustaqil klass. Asosiy Telegram kodiga faqat **2 ta joyda, bittadan qator** qo'shildi:

1. **`MessagesStorage.java` — `openDatabase()`**: ilova ochilganda `krypton_archive` jadvali yaratiladi (`CREATE TABLE IF NOT EXISTS`, xavfsiz — mavjud foydalanuvchilar bazasini buzmaydi).

2. **`MessagesStorage.java` — `markMessagesAsDeletedInternal(...)`**: xabar serverdan "o'chirildi" degan signal kelganda, u fizik ravishda bazadan o'chirilishidan **bir necha qator OLDIN**, uning matni allaqachon xotiraga deserializatsiya qilingan bo'ladi (fayllarni tozalash uchun) — men aynan shu joyga bitta chaqiruv qo'shdim: `KryptonArchive.archiveDeleted(database, did, mid, message)`.

**Nima saqlanadi:** xabar matni, media turi (rasm/video/fayl va h.k. nomi), yuboruvchi ID, asl sana, o'chirilgan sana — alohida `krypton_archive` jadvalida.

**Xavfsizlik:** Har ikkala chaqiruv `try/catch` bilan o'ralgan — agar arxivlashda xatolik yuz bersa, bu asl o'chirish jarayoniga **hech qanday ta'sir qilmaydi**, faqat log yoziladi.

## ✅ Bajarildi: Chat ichida o'chirilgan xabarlarni ko'rsatish (In-Chat Anti-Delete)

O'chirilgan xabarlar alohida arxiv oynasida emas, **to'g'ridan-to'g'ri chatda** qolib turadi va 🗑️ belgisi bilan ajralib ko'rinadi.

### Ishlash mexanizmi (4 qadam):

1. **`MessagesStorage.java` — `markMessagesAsDeletedInternal(...)`**: `SharedConfig.antiDeleteInChatEnabled` yoqilgan bo'lsa, xabar bazadan `DELETE` qilinmaydi — buning o'rniga `messages_v2` jadvalidagi `flags` maydoniga **bit 30** qo'yiladi (`UPDATE ... SET flags = flags | (1 << 30)`). Bu xabar bazada saqlanib qolishini ta'minlaydi.

2. **`MessageObject.java` — konstruktor**: Xabar bazadan yuklanayotganda, `flags & (1 << 30)` tekshiriladi. Agar bit 30 o'rnatilgan bo'lsa, `kryptonDeleted = true` qo'yiladi.

3. **`ChatActivity.java` — `processDeletedMessages(...)`**: Server "xabar o'chirildi" signali kelganda, agar `antiDeleteInChatEnabled` yoqilgan bo'lsa va xabar boshqa foydalanuvchi tomonidan o'chirilgan bo'lsa (`!sent && !obj.isOutOwner()`), xabar UI ro'yxatidan **olib tashlanmaydi** — buning o'rniga `obj.kryptonDeleted = true` qo'yiladi va `chatAdapter.notifyDataSetChanged()` orqali UI yangilanadi.

4. **`ChatMessageCell.java`**: `isKryptonDeleted()` `true` bo'lganda, xabar vaqti oldiga 🗑️ emoji qo'yiladi — `currentTimeString = TextUtils.concat("🗑️ ", currentTimeString)`.

### Sozlama:
- **`SharedConfig.antiDeleteInChatEnabled`** — sukut bo'yicha `true`
- Krypton Settings → "Chat ichida o'chirilgan xabarlarni saqlash" orqali yoqish/o'chirish mumkin

## ⏳ Hali qilinmagan

1. **Tahrirlangan xabarlar arxivi** — texnik jihatdan buni to'g'ri joyga ulash (xabar tahrirlash oqimi "yangi xabar qo'shish" bilan bir xil kodni ishlatadi, bu yerga xato ulasam ishlash tezligiga yoki barqarorlikka salbiy ta'sir qilishi mumkin). Bu — DELETE funksiyasi Android Studio'da sinovdan muvaffaqiyatli o'tgandan keyin qo'shiladigan **keyingi bosqich**.

## ⚠️ Push-bildirishnomalar haqida muhim eslatma

Build xatosini oldini olish uchun `google-services.json` fayllariga `app.krypton.messenger` paket nomlari uchun texnik yozuvlar qo'shildi (build muvaffaqiyatli o'tishi uchun). **Lekin bu — Telegram'ning o'z Firebase loyihasi**, biz uni boshqarmaymiz.

**Natija:** Ilova ochiq turganda hammasi (xabar kelishi, real vaqtda yangilanish) odatdagidek ishlaydi — chunki bu doimiy MTProto ulanishi orqali amalga oshadi. Lekin **ilova butunlay yopiq/background'da bo'lganda push-bildirishnoma** kelishi ishonchli bo'lmasligi mumkin, chunki Google'ning FCM serverlari bizning paket nomimizni **haqiqiy** Firebase loyihasida tan olmaydi.

**To'liq ishonchli push uchun keyingi qadam:**
1. https://console.firebase.google.com da yangi (bepul) loyiha yarating
2. Android ilova sifatida `app.krypton.messenger` (va xohlasangiz `app.krypton.messenger.beta`) qo'shing
3. U yerdan yangi `google-services.json` yuklab oling
4. Uni `TMessagesProj_App/google-services.json` va `TMessagesProj_AppStandalone/google-services.json` bilan almashtiring

## Qanday tekshirish mumkin (compile qilingandan keyin)

### Anti-delete (chat ichida):
1. Ikki qurilma/akkauntdan test chat oching
2. Bir tomondan xabar yuboring, ikkinchi tomondan uni o'chiring ("Delete for everyone")
3. Krypton ilovasida xabar **o'chib ketmaydi** — vaqt yonida 🗑️ belgisi paydo bo'ladi
4. Chatdan chiqib qaytib kirsangiz ham, xabar 🗑️ bilan ko'rinib turadi (bazada saqlanadi)

### Arxiv tekshiruvi (SQLite orqali):
```bash
adb shell "run-as app.krypton.messenger sqlite3 /data/data/app.krypton.messenger/files/account0.db 'SELECT * FROM krypton_archive;'"
```
Agar o'chirilgan xabar matni shu yerda ko'rinsa — backend to'g'ri ishlayapti.
