package org.telegram.messenger;

import org.telegram.SQLite.SQLiteDatabase;
import org.telegram.SQLite.SQLiteException;
import org.telegram.SQLite.SQLitePreparedStatement;
import org.telegram.SQLite.SQLiteCursor;
import org.telegram.tgnet.TLRPC;

import java.util.ArrayList;

/**
 * Krypton — o'chirilgan va tahrirlangan xabarlarni mahalliy arxivga saqlab
 * qoladi (AyuGram uslubidagi "anti-delete" funksiyasi).
 *
 * Bu klass butunlay qo'shimcha va izolyatsiyalangan: asosiy Telegram
 * kodini o'zgartirmaydi, faqat MessagesStorage'dagi ikkita joyga bitta
 * qatordan chaqiriladi. Har qanday xato shu yerning o'zida ushlanadi va
 * asl (o'chirish/tahrirlash) oqimini hech qachon to'xtatmaydi.
 */
public class KryptonArchive {

    public static void ensureTable(SQLiteDatabase database) {
        try {
            database.executeFast(
                "CREATE TABLE IF NOT EXISTS krypton_archive(" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "mid INTEGER, " +
                    "uid INTEGER, " +
                    "sender_id INTEGER, " +
                    "kind INTEGER, " +          // 0 = o'chirilgan, 1 = tahrirlangan (eski matn)
                    "message_text TEXT, " +
                    "media_label TEXT, " +
                    "orig_date INTEGER, " +
                    "event_date INTEGER" +
                ")"
            ).stepThis().dispose();
            database.executeFast(
                "CREATE INDEX IF NOT EXISTS idx_krypton_archive_uid ON krypton_archive(uid, event_date)"
            ).stepThis().dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    private static String mediaLabel(TLRPC.Message message) {
        if (message == null || message.media == null) return null;
        if (message.media instanceof TLRPC.TL_messageMediaPhoto) return "\uD83D\uDDBC Rasm";
        if (message.media instanceof TLRPC.TL_messageMediaDocument) {
            if (message.media.voice) return "\uD83C\uDFA4 Ovozli xabar";
            if (message.media.video) return "\uD83C\uDFA5 Video";
            return "\uD83D\uDCCE Fayl";
        }
        if (message.media instanceof TLRPC.TL_messageMediaGeo) return "\uD83D\uDCCD Joylashuv";
        if (message.media instanceof TLRPC.TL_messageMediaContact) return "\uD83D\uDC64 Kontakt";
        if (message.media instanceof TLRPC.TL_messageMediaPoll) return "\uD83D\uDCCA So'rovnoma";
        return "Media";
    }

    /** Xabar o'chirilishidan OLDIN chaqiriladi — mavjud kontentni arxivlaydi. */
    public static void archiveDeleted(SQLiteDatabase database, long uid, int mid, TLRPC.Message message) {
        insert(database, uid, mid, message, 0);
    }

    /** Xabar tahrirlanishidan OLDIN chaqiriladi — eski (almashtirilayotgan) matnni arxivlaydi. */
    public static void archiveEdited(SQLiteDatabase database, long uid, int mid, TLRPC.Message oldMessage) {
        if (database == null || oldMessage == null) return;
        String text = oldMessage.message != null ? oldMessage.message : "";
        if (text.isEmpty()) return;

        // Oxirgi saqlangan versiya bilan bir xil bo'lsa takroran saqlamaymiz
        SQLiteCursor cursor = null;
        try {
            cursor = database.queryFinalized(
                "SELECT message_text FROM krypton_archive WHERE uid = " + uid + " AND mid = " + mid + " AND kind = 1 ORDER BY id DESC LIMIT 1"
            );
            if (cursor.next()) {
                String lastText = cursor.stringValue(0);
                if (text.equals(lastText)) {
                    return;
                }
            }
        } catch (Exception e) {
            FileLog.e(e);
        } finally {
            if (cursor != null) {
                cursor.dispose();
            }
        }

        insert(database, uid, mid, oldMessage, 1);
    }

    private static void insert(SQLiteDatabase database, long uid, int mid, TLRPC.Message message, int kind) {
        if (database == null || message == null) return;
        SQLitePreparedStatement state = null;
        try {
            String text = message.message != null ? message.message : "";
            // Xabar mazmuni bo'sh bo'lsa (masalan faqat media) va media ham yo'q bo'lsa, arxivlashning hojati yo'q
            String media = mediaLabel(message);
            if (text.isEmpty() && media == null) return;

            long senderId = message.from_id != null ? MessageObject.getPeerId(message.from_id) : 0;

            state = database.executeFast(
                "INSERT INTO krypton_archive(mid, uid, sender_id, kind, message_text, media_label, orig_date, event_date) VALUES(?, ?, ?, ?, ?, ?, ?, ?)"
            );
            state.requery();
            state.bindInteger(1, mid);
            state.bindLong(2, uid);
            state.bindLong(3, senderId);
            state.bindInteger(4, kind);
            state.bindString(5, text);
            if (media != null) {
                state.bindString(6, media);
            } else {
                state.bindNull(6);
            }
            state.bindInteger(7, message.date);
            state.bindInteger(8, (int) (System.currentTimeMillis() / 1000));
            state.step();
        } catch (Exception e) {
            FileLog.e(e);
        } finally {
            if (state != null) {
                state.dispose();
            }
        }
    }

    public static class Entry {
        public int mid;
        public long uid;
        public long senderId;
        public int kind;
        public String text;
        public String mediaLabel;
        public int origDate;
        public int eventDate;
    }

    /** Berilgan chat (uid) uchun arxivlangan yozuvlarni, eng yangisidan boshlab qaytaradi. */
    public static ArrayList<Entry> getForDialog(SQLiteDatabase database, long uid, int limit) {
        ArrayList<Entry> result = new ArrayList<>();
        if (database == null) return result;
        if (limit <= 0) limit = 50;
        SQLiteCursor cursor = null;
        try {
            cursor = database.queryFinalized(
                "SELECT mid, sender_id, kind, message_text, media_label, orig_date, event_date FROM krypton_archive WHERE uid = " + uid + " ORDER BY event_date DESC LIMIT " + limit
            );
            while (cursor.next()) {
                Entry entry = new Entry();
                entry.mid = cursor.intValue(0);
                entry.uid = uid;
                entry.senderId = cursor.longValue(1);
                entry.kind = cursor.intValue(2);
                entry.text = cursor.stringValue(3);
                entry.mediaLabel = cursor.isNull(4) ? null : cursor.stringValue(4);
                entry.origDate = cursor.intValue(5);
                entry.eventDate = cursor.intValue(6);
                result.add(entry);
            }
        } catch (Exception e) {
            FileLog.e(e);
        } finally {
            if (cursor != null) {
                cursor.dispose();
            }
        }
        return result;
    }

    /** Barcha chatlar bo'yicha, eng yangisidan boshlab, arxivlangan yozuvlar ro'yxati. */
    public static ArrayList<Entry> getAllRecent(SQLiteDatabase database, int limit) {
        ArrayList<Entry> result = new ArrayList<>();
        if (database == null) return result;
        if (limit <= 0) limit = 50;
        SQLiteCursor cursor = null;
        try {
            cursor = database.queryFinalized(
                "SELECT mid, uid, sender_id, kind, message_text, media_label, orig_date, event_date FROM krypton_archive ORDER BY event_date DESC LIMIT " + limit
            );
            while (cursor.next()) {
                Entry entry = new Entry();
                entry.mid = cursor.intValue(0);
                entry.uid = cursor.longValue(1);
                entry.senderId = cursor.longValue(2);
                entry.kind = cursor.intValue(3);
                entry.text = cursor.stringValue(4);
                entry.mediaLabel = cursor.isNull(5) ? null : cursor.stringValue(5);
                entry.origDate = cursor.intValue(6);
                entry.eventDate = cursor.intValue(7);
                result.add(entry);
            }
        } catch (Exception e) {
            FileLog.e(e);
        } finally {
            if (cursor != null) {
                cursor.dispose();
            }
        }
        return result;
    }

    /** Berilgan xabar o'chirilganini arxivdan tekshiradi. */
    public static boolean isMessageDeleted(SQLiteDatabase database, long uid, int mid) {
        if (database == null) return false;
        SQLiteCursor cursor = null;
        try {
            cursor = database.queryFinalized(
                "SELECT id FROM krypton_archive WHERE uid = " + uid + " AND mid = " + mid + " AND kind = 0 LIMIT 1"
            );
            boolean found = cursor.next();
            return found;
        } catch (Exception e) {
            FileLog.e(e);
        } finally {
            if (cursor != null) {
                cursor.dispose();
            }
        }
        return false;
    }

    /** Berilgan xabarning tahrirlar tarixini qaytaradi. */
    public static ArrayList<String> getEditHistory(SQLiteDatabase database, long uid, int mid) {
        ArrayList<String> history = new ArrayList<>();
        if (database == null) return history;
        SQLiteCursor cursor = null;
        try {
            cursor = database.queryFinalized(
                "SELECT message_text FROM krypton_archive WHERE uid = " + uid + " AND mid = " + mid + " AND kind = 1 ORDER BY event_date ASC"
            );
            while (cursor.next()) {
                String txt = cursor.stringValue(0);
                if (txt != null && !txt.isEmpty()) {
                    history.add(txt);
                }
            }
        } catch (Exception e) {
            FileLog.e(e);
        } finally {
            if (cursor != null) {
                cursor.dispose();
            }
        }
        return history;
    }

    /** Arxivdagi barcha yozuvlarni tozalaydi. */
    public static void clearAll(SQLiteDatabase database) {
        if (database == null) return;
        try {
            database.executeFast("DELETE FROM krypton_archive").stepThis().dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }
}
