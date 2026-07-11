# Krypton — "O'chirilgan xabarlarni saqlash" funksiyasi

AyuGram uslubidagi anti-delete funksiyasi. Hozirgi holat va ishlash tamoyili quyida yozilgan.

## ✅ Bajarildi: Backend (xabarni arxivlash)

**Fayl:** `TMessagesProj/src/main/java/org/telegram/messenger/KryptonArchive.java`

Yangi, butunlay mustaqil klass. Asosiy Telegram kodiga faqat **2 ta joyda, bittadan qator** qo'shildi:

1. **`MessagesStorage.java` — `openDatabase()`**: ilova ochilganda `krypton_archive` jadvali yaratiladi (`CREATE TABLE IF NOT EXISTS`, xavfsiz — mavjud foydalanuvchilar bazasini buzmaydi).

2. **`MessagesStorage.java` — `markMessagesAsDeletedInternal(...)`**: xabar serverdan "o'chirildi" degan signal kelganda, u fizik ravishda bazadan o'chirilishidan **bir necha qator OLDIN**, uning matni allaqachon xotiraga deserializatsiya qilingan bo'ladi (fayllarni tozalash uchun) — men aynan shu joyga bitta chaqiruv qo'shdim: `KryptonArchive.archiveDeleted(database, did, mid, message)`.

**Nima saqlanadi:** xabar matni, media turi (rasm/video/fayl va h.k. nomi), yuboruvchi ID, asl sana, o'chirilgan sana — alohida `krypton_archive` jadvalida.

**Xavfsizlik:** Har ikkala chaqiruv `try/catch` bilan o'ralgan — agar arxivlashda xatolik yuz bersa, bu asl o'chirish jarayoniga **hech qanday ta'sir qilmaydi**, faqat log yoziladi.

## ⏳ Hali qilinmagan

1. **Tahrirlangan xabarlar arxivi** — texnik jihatdan buni to'g'ri joyga ulash (xabar tahrirlash oqimi "yangi xabar qo'shish" bilan bir xil kodni ishlatadi, bu yerga xato ulasam ishlash tezligiga yoki barqarorlikka salbiy ta'sir qilishi mumkin). Bu — DELETE funksiyasi Android Studio'da sinovdan muvaffaqiyatli o'tgandan keyin qo'shiladigan **keyingi bosqich**.

2. **Ko'rish uchun UI** — hozircha arxiv faqat ma'lumotlar bazasida saqlanadi, uni ko'rsatadigan ekran hali yo'q. Dasturiy jihatdan ma'lumotni olish tayyor:
   ```java
   ArrayList<KryptonArchive.Entry> entries = KryptonArchive.getForDialog(database, dialogId, 50);
   ```
   Bu funksiyani biror chat ekraniga (masalan uzoq bosilganda chiqadigan menyuga "O'chirilgan xabarlar" bandi sifatida) ulash — keyingi bosqich.

## Qanday tekshirish mumkin (compile qilingandan keyin)

1. Ikki qurilma/akkauntdan test chat oching
2. Bir tomondan xabar yuboring, ikkinchi tomondan uni o'chiring ("Delete for everyone")
3. `adb shell` orqali ilovaning SQLite bazasini tekshiring:
   ```bash
   adb shell "run-as app.krypton.messenger sqlite3 /data/data/app.krypton.messenger/files/account0.db 'SELECT * FROM krypton_archive;'"
   ```
   Agar o'chirilgan xabar matni shu yerda ko'rinsa — backend to'g'ri ishlayapti.
