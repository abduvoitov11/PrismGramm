package org.telegram.ui;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;

import static org.telegram.messenger.LocaleController.getString;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.ListView.AdapterWithDiffUtils;
import org.telegram.ui.Components.RecyclerListView;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Krypton Settings — bizning ilovaga qo'shgan barcha maxsus funksiyalar
 * (Ghost Mode, Media Downloader, o'chirilgan xabarlar arxivi) faqat
 * shu yerda joylashadi. Rasmiy Telegram sozlamalari orasida sochilmaydi.
 */
public class KryptonSettingsActivity extends BaseFragment {

    private RecyclerListView listView;
    private ListAdapter adapter;

    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_CHECK = 1;
    private static final int VIEW_TYPE_SHADOW = 2;
    private static final int VIEW_TYPE_NAV = 3;

    private static final int ID_GHOST_MODE = 1;
    private static final int ID_MEDIA_DOWNLOADER = 2;
    private static final int ID_ARCHIVE = 3;
    private static final int ID_HIDE_ADS = 4;
    private static final int ID_ANTI_DELETE_IN_CHAT = 5;
    private static final int ID_EDIT_HISTORY = 6;
    private static final int ID_SAVE_DELETED_MEDIA = 7;

    @Override
    public View createView(Context context) {
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setAllowOverlayTitle(true);
        actionBar.setTitle(getString("KryptonSettings", R.string.KryptonSettings));
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
        listView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        });
        listView.setVerticalScrollBarEnabled(false);
        listView.setAdapter(adapter = new ListAdapter());
        DefaultItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setDurations(280);
        itemAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
        itemAnimator.setSupportsChangeAnimations(false);
        listView.setItemAnimator(itemAnimator);
        frameLayout.addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        listView.setOnItemClickListener((view, position) -> {
            if (position < 0 || position >= items.size()) return;
            ItemInner item = items.get(position);
            if (item.id == ID_GHOST_MODE) {
                boolean newState = !SharedConfig.ghostModeEnabled;
                SharedConfig.setGhostModeEnabled(newState);
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(newState);
                }
            } else if (item.id == ID_MEDIA_DOWNLOADER) {
                presentFragment(new KryptonMediaDownloaderActivity());
            } else if (item.id == ID_ARCHIVE) {
                presentFragment(new KryptonArchiveActivity());
            } else if (item.id == ID_HIDE_ADS) {
                boolean newState = !SharedConfig.hideSponsoredAds;
                SharedConfig.setHideSponsoredAds(newState);
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(newState);
                }
            } else if (item.id == ID_ANTI_DELETE_IN_CHAT) {
                boolean newState = !SharedConfig.antiDeleteInChatEnabled;
                SharedConfig.setAntiDeleteInChatEnabled(newState);
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(newState);
                }
            } else if (item.id == ID_EDIT_HISTORY) {
                boolean newState = !SharedConfig.editHistoryEnabled;
                SharedConfig.setEditHistoryEnabled(newState);
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(newState);
                }
            } else if (item.id == ID_SAVE_DELETED_MEDIA) {
                boolean newState = !SharedConfig.saveDeletedMediaEnabled;
                SharedConfig.setSaveDeletedMediaEnabled(newState);
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(newState);
                }
            }
        });

        updateItems();
        return fragmentView;
    }

    private final ArrayList<ItemInner> items = new ArrayList<>();

    private void updateItems() {
        items.clear();

        items.add(new ItemInner(VIEW_TYPE_HEADER, 0, "Ghost Mode"));
        items.add(new ItemInner(VIEW_TYPE_CHECK, ID_GHOST_MODE, getString("KryptonGhostMode", R.string.KryptonGhostMode)));
        items.add(new ItemInner(VIEW_TYPE_SHADOW, 0, getString("KryptonGhostModeInfo", R.string.KryptonGhostModeInfo)));

        items.add(new ItemInner(VIEW_TYPE_HEADER, 0, "Media Downloader"));
        items.add(new ItemInner(VIEW_TYPE_NAV, ID_MEDIA_DOWNLOADER, "Media yuklab olish"));
        items.add(new ItemInner(VIEW_TYPE_SHADOW, 0, "TikTok, YouTube va Instagram'dan video, rasm va musiqa yuklab olish. Yuklab olingan fayllar galereyangizga saqlanadi."));
        items.add(new ItemInner(VIEW_TYPE_HEADER, 0, "Reklama Blokirovkasi (Ad Blocker)"));
        items.add(new ItemInner(VIEW_TYPE_CHECK, ID_HIDE_ADS, "Barcha reklamalarni butunlay bloklash"));
        items.add(new ItemInner(VIEW_TYPE_SHADOW, 0, "Telegram rasmiy Sponsored postlari, video pleyer reklamalari, proxy sponsor kanallari hamda tavsiya etiladigan (Recommended) kanallar butunlay yuklanmaydi va ko'rsatilmaydi."));

        items.add(new ItemInner(VIEW_TYPE_HEADER, 0, "Anti-Delete va Tahrirlar"));
        items.add(new ItemInner(VIEW_TYPE_CHECK, ID_ANTI_DELETE_IN_CHAT, "Chat ichida o'chirilgan xabarlarni saqlash"));
        items.add(new ItemInner(VIEW_TYPE_SHADOW, 0, "Suhbatdosh xabarni o'chirib yuborganda, u chatdan o'chib ketmaydi va 🗑️ belgisi bilan ko'rinib turadi (AyuGram uslubida)."));

        items.add(new ItemInner(VIEW_TYPE_CHECK, ID_EDIT_HISTORY, "Tahrirlangan xabarlar tarixini saqlash"));
        items.add(new ItemInner(VIEW_TYPE_SHADOW, 0, "Xabar tahrirlanganda uning asl (eski) versiyalari saqlab qolinadi."));

        items.add(new ItemInner(VIEW_TYPE_CHECK, ID_SAVE_DELETED_MEDIA, "O'chirilgan media fayllarni saqlash"));
        items.add(new ItemInner(VIEW_TYPE_SHADOW, 0, "O'chirilgan rasmlar, videolar va ovozli xabarlar qurilma keshidan o'chirib tashlanmaydi."));

        items.add(new ItemInner(VIEW_TYPE_HEADER, 0, "Arxiv"));
        items.add(new ItemInner(VIEW_TYPE_NAV, ID_ARCHIVE, "O'chirilgan xabarlar arxivi"));
        items.add(new ItemInner(VIEW_TYPE_SHADOW, 0, "Suhbatdoshingiz o'chirgan va tahrirlagan barcha xabarlar ro'yxati. Faqat sizning qurilmangizda saqlanadi."));

        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    private static class ItemInner extends AdapterWithDiffUtils.Item {
        int id;
        CharSequence text;

        ItemInner(int viewType, int id, CharSequence text) {
            super(viewType, false);
            this.id = id;
            this.text = text;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ItemInner item = (ItemInner) o;
            return id == item.id && Objects.equals(text, item.text) && viewType == item.viewType;
        }

        @Override
        public int hashCode() {
            return Objects.hash(viewType, id);
        }
    }

    private class ListAdapter extends AdapterWithDiffUtils {
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;
            if (viewType == VIEW_TYPE_HEADER) {
                view = new HeaderCell(getContext());
            } else if (viewType == VIEW_TYPE_CHECK) {
                view = new TextCheckCell(getContext());
            } else if (viewType == VIEW_TYPE_NAV) {
                view = new TextCell(getContext());
            } else {
                view = new TextInfoPrivacyCell(getContext());
            }
            return new RecyclerListView.Holder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (position < 0 || position >= items.size()) return;
            ItemInner item = items.get(position);
            int viewType = holder.getItemViewType();
            if (viewType == VIEW_TYPE_HEADER) {
                ((HeaderCell) holder.itemView).setText(item.text.toString());
            } else if (viewType == VIEW_TYPE_SHADOW) {
                TextInfoPrivacyCell cell = (TextInfoPrivacyCell) holder.itemView;
                cell.setFixedSize(0);
                cell.setText(item.text);
            } else if (viewType == VIEW_TYPE_CHECK) {
                TextCheckCell cell = (TextCheckCell) holder.itemView;
                boolean checked;
                if (item.id == ID_GHOST_MODE) {
                    checked = SharedConfig.ghostModeEnabled;
                } else if (item.id == ID_HIDE_ADS) {
                    checked = SharedConfig.hideSponsoredAds;
                } else if (item.id == ID_ANTI_DELETE_IN_CHAT) {
                    checked = SharedConfig.antiDeleteInChatEnabled;
                } else if (item.id == ID_EDIT_HISTORY) {
                    checked = SharedConfig.editHistoryEnabled;
                } else if (item.id == ID_SAVE_DELETED_MEDIA) {
                    checked = SharedConfig.saveDeletedMediaEnabled;
                } else {
                    checked = SharedConfig.mediaDownloaderEnabled;
                }
                cell.setTextAndCheck(item.text, checked, false);
            } else if (viewType == VIEW_TYPE_NAV) {
                TextCell cell = (TextCell) holder.itemView;
                cell.setText(item.text, false);
            }
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int type = holder.getItemViewType();
            return type == VIEW_TYPE_CHECK || type == VIEW_TYPE_NAV;
        }

        @Override
        public int getItemViewType(int position) {
            if (position < 0 || position >= items.size()) return VIEW_TYPE_SHADOW;
            return items.get(position).viewType;
        }
    }

    @Override
    public boolean isSupportEdgeToEdge() {
        return true;
    }

    @Override
    public void onInsets(int left, int top, int right, int bottom) {
        listView.setPadding(0, 0, 0, bottom);
        listView.setClipToPadding(false);
    }
}
