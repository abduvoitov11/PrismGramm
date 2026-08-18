package org.telegram.ui;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
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
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;

import java.util.ArrayList;

/**
 * Krypton — o'chirilgan va tahrirlangan xabarlar arxivi ekrani.
 * Foydalanuvchi alohida xabarlarni yoki barcha arxivni to'liq o'chirish (tozalash) imkoniyatiga ega.
 */
public class KryptonArchiveActivity extends BaseFragment {

    private static final int MENU_CLEAR_ALL = 1;

    private RecyclerListView listView;
    private ListAdapter adapter;
    private final ArrayList<KryptonArchive.Entry> entries = new ArrayList<>();
    private boolean loading = true;
    private ActionBarMenuItem clearAllItem;

    @Override
    public View createView(Context context) {
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setAllowOverlayTitle(true);
        actionBar.setTitle(LocaleController.getString(R.string.PrismaArchive));
        
        clearAllItem = actionBar.createMenu().addItem(MENU_CLEAR_ALL, R.drawable.msg_delete);
        clearAllItem.setContentDescription(LocaleController.getString(R.string.ClearHistory));

        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finishFragment();
                } else if (id == MENU_CLEAR_ALL) {
                    confirmClearAll();
                }
            }
        });

        FrameLayout frameLayout = new FrameLayout(context);
        fragmentView = frameLayout;
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));

        listView = new RecyclerListView(context);
        listView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        listView.setAdapter(adapter = new ListAdapter(context));
        listView.setVerticalScrollBarEnabled(false);
        frameLayout.addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        listView.setOnItemClickListener((view, position) -> {
            if (position >= 0 && position < entries.size()) {
                showEntryOptions(position);
            }
        });

        listView.setOnItemLongClickListener((view, position) -> {
            if (position >= 0 && position < entries.size()) {
                showEntryOptions(position);
                return true;
            }
            return false;
        });

        loadEntries();
        return fragmentView;
    }

    private void confirmClearAll() {
        if (entries.isEmpty()) {
            BulletinFactory.of(this).createSimpleBulletin(R.raw.ic_delete, "Arxiv allaqachon bo'sh").show();
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle(LocaleController.getString(R.string.ClearHistory));
        builder.setMessage("Barcha o'chirilgan va tahrirlangan xabarlar arxivini butunlay tozalashni xohlaysizmi?");
        builder.setPositiveButton(LocaleController.getString(R.string.Delete), (dialog, which) -> {
            getMessagesStorage().getStorageQueue().postRunnable(() -> {
                KryptonArchive.clearAll(getMessagesStorage().getDatabase());
                AndroidUtilities.runOnUIThread(() -> {
                    entries.clear();
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
                    try {
                        BulletinFactory.of(KryptonArchiveActivity.this).createSimpleBulletin(R.raw.ic_delete, "Barcha arxiv tozalandi").show();
                    } catch (Exception ignored) {}
                });
            });
        });
        builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
        AlertDialog dialog = builder.create();
        showDialog(dialog);
        TextView button = (TextView) dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        if (button != null) {
            button.setTextColor(Theme.getColor(Theme.key_text_RedBold));
        }
    }

    private void showEntryOptions(int position) {
        if (position < 0 || position >= entries.size()) return;
        KryptonArchive.Entry entry = entries.get(position);

        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle(senderName(entry.senderId));

        ArrayList<CharSequence> items = new ArrayList<>();
        ArrayList<Integer> icons = new ArrayList<>();
        ArrayList<Integer> actions = new ArrayList<>();

        if (entry.uid != 0) {
            items.add("Chatga o'tish");
            icons.add(R.drawable.msg_message);
            actions.add(1);
        }

        if (!TextUtils.isEmpty(entry.text)) {
            items.add(LocaleController.getString(R.string.Copy));
            icons.add(R.drawable.msg_copy);
            actions.add(2);
        }

        items.add(LocaleController.getString(R.string.Delete));
        icons.add(R.drawable.msg_delete);
        actions.add(3);

        int[] iconArray = new int[icons.size()];
        for (int i = 0; i < icons.size(); i++) {
            iconArray[i] = icons.get(i);
        }

        builder.setItems(items.toArray(new CharSequence[0]), iconArray, (dialog, which) -> {
            int action = actions.get(which);
            if (action == 1) {
                // Chatga o'tish
                Bundle args = new Bundle();
                if (entry.uid > 0) {
                    args.putLong("user_id", entry.uid);
                } else {
                    args.putLong("chat_id", -entry.uid);
                }
                args.putInt("message_id", entry.mid);
                presentFragment(new ChatActivity(args));
            } else if (action == 2) {
                // Nusxa olish
                AndroidUtilities.addToClipboard(entry.text);
                BulletinFactory.of(KryptonArchiveActivity.this).createCopyBulletin(LocaleController.getString(R.string.TextCopied)).show();
            } else if (action == 3) {
                // Arxivdan o'chirish
                deleteSingleEntry(position);
            }
        });
        showDialog(builder.create());
    }

    private void deleteSingleEntry(int position) {
        if (position < 0 || position >= entries.size()) return;
        KryptonArchive.Entry entry = entries.remove(position);
        if (adapter != null) {
            if (entries.isEmpty()) {
                adapter.notifyDataSetChanged();
            } else {
                adapter.notifyItemRemoved(position);
            }
        }

        getMessagesStorage().getStorageQueue().postRunnable(() -> {
            KryptonArchive.deleteEntry(getMessagesStorage().getDatabase(), entry.id, entry.uid, entry.mid);
        });

        try {
            BulletinFactory.of(this).createSimpleBulletin(R.raw.ic_delete, "Xabar arxivdan o'chirildi").show();
        } catch (Exception ignored) {}
    }

    private void loadEntries() {
        getMessagesStorage().getStorageQueue().postRunnable(() -> {
            ArrayList<KryptonArchive.Entry> loaded = KryptonArchive.getAllRecent(getMessagesStorage().getDatabase(), 300);
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
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setPadding(AndroidUtilities.dp(16), AndroidUtilities.dp(12), AndroidUtilities.dp(12), AndroidUtilities.dp(12));
            row.setBackground(Theme.getSelectorDrawable(false));

            LinearLayout contentLayout = new LinearLayout(context);
            contentLayout.setOrientation(LinearLayout.VERTICAL);
            row.addView(contentLayout, LayoutHelper.createLinear(0, LayoutHelper.WRAP_CONTENT, 1.0f, Gravity.CENTER_VERTICAL));

            TextView header = new TextView(context);
            header.setTextSize(14);
            header.setTypeface(AndroidUtilities.bold());
            header.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText));
            header.setTag("header");
            contentLayout.addView(header);

            TextView body = new TextView(context);
            body.setTextSize(15);
            body.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            body.setPadding(0, AndroidUtilities.dp(4), 0, 0);
            body.setTag("body");
            contentLayout.addView(body);

            TextView footer = new TextView(context);
            footer.setTextSize(12);
            footer.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText));
            footer.setPadding(0, AndroidUtilities.dp(4), 0, 0);
            footer.setTag("footer");
            contentLayout.addView(footer);

            ImageView deleteBtn = new ImageView(context);
            deleteBtn.setImageResource(R.drawable.msg_delete);
            deleteBtn.setColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteGrayIcon));
            deleteBtn.setScaleType(ImageView.ScaleType.CENTER);
            deleteBtn.setPadding(AndroidUtilities.dp(8), AndroidUtilities.dp(8), AndroidUtilities.dp(8), AndroidUtilities.dp(8));
            deleteBtn.setBackground(Theme.createSelectorDrawable(Theme.getColor(Theme.key_listSelector), 1));
            deleteBtn.setTag("deleteBtn");
            row.addView(deleteBtn, LayoutHelper.createLinear(38, 38, Gravity.CENTER_VERTICAL));

            return new RecyclerListView.Holder(row);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (getItemViewType(position) == 1) {
                TextInfoPrivacyCell cell = (TextInfoPrivacyCell) holder.itemView;
                if (loading) {
                    cell.setText("Yuklanmoqda...");
                } else if (entries.isEmpty()) {
                    cell.setText("Hozircha arxivda hech narsa yo'q. Kimdir xabarni o'chirsa yoki tahrirlasa, shu yerda saqlanadi.");
                } else {
                    cell.setText("Bu — mahalliy qurilmangizda saqlangan arxiv. Istalgan xabarni alohida yoki yuqoridagi tugma orqali butunlay o'chirishingiz mumkin.");
                }
                return;
            }
            KryptonArchive.Entry entry = entries.get(position);
            LinearLayout row = (LinearLayout) holder.itemView;
            TextView header = row.findViewWithTag("header");
            TextView body = row.findViewWithTag("body");
            TextView footer = row.findViewWithTag("footer");
            ImageView deleteBtn = row.findViewWithTag("deleteBtn");

            String kindLabel = entry.kind == 0 ? "🗑️ O'chirilgan" : "✏️ Tahrirlangan";
            header.setText(kindLabel + " • " + senderName(entry.senderId) + " (" + senderName(entry.uid) + ")");

            String text = entry.text;
            if (TextUtils.isEmpty(text) && entry.mediaLabel != null) {
                text = entry.mediaLabel;
            } else if (!TextUtils.isEmpty(text) && entry.mediaLabel != null) {
                text = entry.mediaLabel + "\n" + text;
            }
            body.setText(TextUtils.isEmpty(text) ? "(bo'sh xabar)" : text);

            footer.setText(LocaleController.formatDateChat(entry.eventDate));

            deleteBtn.setOnClickListener(v -> deleteSingleEntry(holder.getAdapterPosition()));
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
            return holder.getItemViewType() == 0;
        }
    }
}
