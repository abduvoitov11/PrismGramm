package org.telegram.ui;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.KryptonArchive;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;

import java.util.ArrayList;

/**
 * Krypton — o'chirilgan/tahrirlangan xabarlar arxivini ko'rsatadigan
 * sodda ro'yxat ekrani. Ma'lumot KryptonArchive orqali mahalliy SQLite
 * bazasidan o'qiladi (hech qanday server so'rovi yo'q).
 */
public class KryptonArchiveActivity extends BaseFragment {

    private RecyclerListView listView;
    private ListAdapter adapter;
    private final ArrayList<KryptonArchive.Entry> entries = new ArrayList<>();
    private boolean loading = true;

    @Override
    public View createView(Context context) {
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setAllowOverlayTitle(true);
        actionBar.setTitle("O'chirilgan xabarlar");
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finishFragment();
                }
            }
        });

        FrameLayout frameLayout = new FrameLayout(context);
        fragmentView = frameLayout;
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));

        listView = new RecyclerListView(context);
        listView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        listView.setAdapter(adapter = new ListAdapter(context));
        frameLayout.addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        loadEntries();
        return fragmentView;
    }

    private void loadEntries() {
        getMessagesStorage().getStorageQueue().postRunnable(() -> {
            ArrayList<KryptonArchive.Entry> loaded = KryptonArchive.getAllRecent(getMessagesStorage().getDatabase(), 200);
            AndroidUtilities.runOnUIThread(() -> {
                entries.clear();
                entries.addAll(loaded);
                loading = false;
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
            });
        });
    }

    private String senderName(long id) {
        if (id == 0) return "Noma'lum";
        if (id > 0) {
            TLRPC.User user = getMessagesController().getUser(id);
            return user != null ? UserObject.getUserName(user) : "Foydalanuvchi #" + id;
        } else {
            TLRPC.Chat chat = getMessagesController().getChat(-id);
            return chat != null ? chat.title : "Chat #" + (-id);
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private final Context context;

        ListAdapter(Context context) {
            this.context = context;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == 1) {
                return new RecyclerListView.Holder(new TextInfoPrivacyCell(context));
            }
            LinearLayout row = new LinearLayout(context);
            row.setOrientation(LinearLayout.VERTICAL);
            row.setPadding(AndroidUtilities.dp(16), AndroidUtilities.dp(10), AndroidUtilities.dp(16), AndroidUtilities.dp(10));

            TextView header = new TextView(context);
            header.setTextSize(14);
            header.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText));
            header.setTag("header");
            row.addView(header);

            TextView body = new TextView(context);
            body.setTextSize(15);
            body.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            body.setPadding(0, AndroidUtilities.dp(4), 0, 0);
            body.setTag("body");
            row.addView(body);

            TextView footer = new TextView(context);
            footer.setTextSize(12);
            footer.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText));
            footer.setPadding(0, AndroidUtilities.dp(4), 0, 0);
            footer.setTag("footer");
            row.addView(footer);

            return new RecyclerListView.Holder(row);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (getItemViewType(position) == 1) {
                TextInfoPrivacyCell cell = (TextInfoPrivacyCell) holder.itemView;
                if (loading) {
                    cell.setText("Yuklanmoqda...");
                } else if (entries.isEmpty()) {
                    cell.setText("Hozircha arxivda hech narsa yo'q. Kimdir xabarni o'chirsa yoki tahrirlasa, shu yerda ko'rinadi.");
                } else {
                    cell.setText("Bu — mahalliy qurilmangizda saqlangan arxiv. Faqat siz ko'ra olasiz.");
                }
                return;
            }
            KryptonArchive.Entry entry = entries.get(position);
            LinearLayout row = (LinearLayout) holder.itemView;
            TextView header = row.findViewWithTag("header");
            TextView body = row.findViewWithTag("body");
            TextView footer = row.findViewWithTag("footer");

            String kindLabel = entry.kind == 0 ? "O'chirilgan" : "Tahrirlangan (eski matn)";
            header.setText(kindLabel + " • " + senderName(entry.senderId) + " • " + senderName(entry.uid));

            String text = entry.text;
            if (TextUtils.isEmpty(text) && entry.mediaLabel != null) {
                text = entry.mediaLabel;
            } else if (!TextUtils.isEmpty(text) && entry.mediaLabel != null) {
                text = entry.mediaLabel + "\n" + text;
            }
            body.setText(TextUtils.isEmpty(text) ? "(bo'sh xabar)" : text);

            footer.setText(LocaleController.formatDateChat(entry.eventDate));
        }

        @Override
        public int getItemCount() {
            return Math.max(1, entries.size());
        }

        @Override
        public int getItemViewType(int position) {
            return entries.isEmpty() ? 1 : 0;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return false;
        }
    }
}
