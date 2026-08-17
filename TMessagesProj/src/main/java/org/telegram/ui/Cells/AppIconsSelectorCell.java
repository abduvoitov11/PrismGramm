package org.telegram.ui.Cells;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.ColoredImageSpan;
import org.telegram.ui.Components.Easings;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Premium.PremiumFeatureBottomSheet;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.LauncherIconController;
import org.telegram.ui.PremiumPreviewFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AppIconsSelectorCell extends RecyclerListView implements NotificationCenter.NotificationCenterDelegate {
    public final static float ICONS_ROUND_RADIUS = 18;

    private List<LauncherIconController.LauncherIcon> availableIcons = new ArrayList<>();
    private GridLayoutManager gridLayoutManager;
    private int currentAccount;

    public AppIconsSelectorCell(Context context, BaseFragment fragment, int currentAccount) {
        super(context);
        this.currentAccount = currentAccount;
        setPadding(AndroidUtilities.dp(10), AndroidUtilities.dp(12), AndroidUtilities.dp(10), AndroidUtilities.dp(12));

        setFocusable(false);
        setItemAnimator(null);
        setLayoutAnimation(null);
        setNestedScrollingEnabled(false);
        setOverScrollMode(OVER_SCROLL_NEVER);

        int spanCount = 3;
        setLayoutManager(gridLayoutManager = new GridLayoutManager(context, spanCount));
        setAdapter(new Adapter() {

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new RecyclerListView.Holder(new IconHolderView(parent.getContext()));
            }

            @Override
            public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
                IconHolderView holderView = (IconHolderView) holder.itemView;
                LauncherIconController.LauncherIcon icon = availableIcons.get(position);
                holderView.bind(icon);
                holderView.iconView.setBackground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(ICONS_ROUND_RADIUS), Color.TRANSPARENT, Theme.getColor(Theme.key_listSelector), Color.BLACK));
                holderView.iconView.setForeground(icon.foreground);
            }

            @Override
            public int getItemCount() {
                return availableIcons.size();
            }
        });
        addItemDecoration(new ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull State state) {
                outRect.left = AndroidUtilities.dp(6);
                outRect.right = AndroidUtilities.dp(6);
                outRect.top = AndroidUtilities.dp(6);
                outRect.bottom = AndroidUtilities.dp(12);
            }
        });
        setOnItemClickListener((view, position) -> {
            IconHolderView holderView = (IconHolderView) view;
            LauncherIconController.LauncherIcon icon = availableIcons.get(position);
            if (icon.premium && !UserConfig.hasPremiumOnAccounts()) {
                fragment.showDialog(new PremiumFeatureBottomSheet(fragment, PremiumPreviewFragment.PREMIUM_FEATURE_APPLICATION_ICONS, true));
                return;
            }

            if (LauncherIconController.isEnabled(icon)) {
                return;
            }

            LauncherIconController.setIcon(icon);
            holderView.setSelected(true, true);

            for (int i = 0; i < getChildCount(); i++) {
                IconHolderView otherView = (IconHolderView) getChildAt(i);
                if (otherView != holderView) {
                    otherView.setSelected(false, true);
                }
            }

            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.showBulletin, Bulletin.TYPE_APP_ICON, icon);
        });
        updateIconsVisibility();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateIconsVisibility() {
        availableIcons.clear();
        availableIcons.addAll(Arrays.asList(LauncherIconController.LauncherIcon.values()));
        if (MessagesController.getInstance(currentAccount).premiumFeaturesBlocked()) {
            for (int i = 0; i < availableIcons.size(); i++) {
                if (availableIcons.get(i).premium) {
                    availableIcons.remove(i);
                    i--;
                }
            }
        }
        getAdapter().notifyDataSetChanged();
        invalidateItemDecorations();
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        int width = MeasureSpec.getSize(widthSpec);
        int spanCount = width > AndroidUtilities.dp(500) ? 4 : 3;
        if (gridLayoutManager != null && gridLayoutManager.getSpanCount() != spanCount) {
            gridLayoutManager.setSpanCount(spanCount);
        }
        super.onMeasure(
            MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(536870911, MeasureSpec.AT_MOST)
        );
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        invalidateItemDecorations();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.premiumStatusChangedGlobal);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.premiumStatusChangedGlobal);
    }

    @Override
    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.premiumStatusChangedGlobal) {
            updateIconsVisibility();
        }
    }

    private final static class IconHolderView extends LinearLayout {
        private Paint outlinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private Paint fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        private AdaptiveIconImageView iconView;
        private TextView titleView;

        private float progress;

        private IconHolderView(@NonNull Context context) {
            super(context);

            setOrientation(VERTICAL);

            setWillNotDraw(false);
            iconView = new AdaptiveIconImageView(context);
            iconView.setPadding(AndroidUtilities.dp(8), AndroidUtilities.dp(8), AndroidUtilities.dp(8), AndroidUtilities.dp(8));
            addView(iconView, LayoutHelper.createLinear(58, 58, Gravity.CENTER_HORIZONTAL));

            titleView = new TextView(context);
            titleView.setSingleLine();
            titleView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
            titleView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            addView(titleView, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL, 0, 4, 0, 0));

            outlinePaint.setStyle(Paint.Style.STROKE);
            outlinePaint.setStrokeWidth(Math.max(2, AndroidUtilities.dp(0.5f)));

            fillPaint.setColor(Color.WHITE);
        }

        @Override
        public void draw(Canvas canvas) {
            float stroke = outlinePaint.getStrokeWidth();
            AndroidUtilities.rectTmp.set(iconView.getLeft() + stroke, iconView.getTop() + stroke, iconView.getRight() - stroke, iconView.getBottom() - stroke);
            canvas.drawRoundRect(AndroidUtilities.rectTmp, AndroidUtilities.dp(ICONS_ROUND_RADIUS), AndroidUtilities.dp(ICONS_ROUND_RADIUS), fillPaint);

            super.draw(canvas);

            canvas.drawRoundRect(AndroidUtilities.rectTmp, AndroidUtilities.dp(ICONS_ROUND_RADIUS), AndroidUtilities.dp(ICONS_ROUND_RADIUS), outlinePaint);
        }

        private void setProgress(float progress) {
            this.progress = progress;

            titleView.setTextColor(ColorUtils.blendARGB(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText), Theme.getColor(Theme.key_windowBackgroundWhiteValueText), progress));
            outlinePaint.setColor(ColorUtils.blendARGB(ColorUtils.setAlphaComponent(Theme.getColor(Theme.key_switchTrack), 0x3F), Theme.getColor(Theme.key_windowBackgroundWhiteValueText), progress));
            outlinePaint.setStrokeWidth(Math.max(2, AndroidUtilities.dp(AndroidUtilities.lerp(0.5f, 2f, progress))));
            invalidate();
        }

        private void setSelected(boolean selected, boolean animate) {
            float to = selected ? 1 : 0;
            if (to == progress && animate) {
                return;
            }

            if (animate) {
                ValueAnimator animator = ValueAnimator.ofFloat(progress, to).setDuration(250);
                animator.setInterpolator(Easings.easeInOutQuad);
                animator.addUpdateListener(animation -> setProgress((Float) animation.getAnimatedValue()));
                animator.start();
            } else {
                setProgress(to);
            }
        }

        private void bind(LauncherIconController.LauncherIcon icon) {
            iconView.setImageResource(icon.background);

            MarginLayoutParams params = (MarginLayoutParams) titleView.getLayoutParams();
            if (icon.premium && !UserConfig.hasPremiumOnAccounts()) {
                SpannableString str = new SpannableString("d " + LocaleController.getString(icon.title));
                ColoredImageSpan span = new ColoredImageSpan(R.drawable.msg_mini_premiumlock);
                span.setTopOffset(1);
                span.setSize(AndroidUtilities.dp(13));
                str.setSpan(span, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                params.rightMargin = AndroidUtilities.dp(4);
                titleView.setText(str);
            } else {
                params.rightMargin = 0;
                titleView.setText(LocaleController.getString(icon.title));
            }
            setSelected(LauncherIconController.isEnabled(icon), false);
        }
    }

    public static class AdaptiveIconImageView extends ImageView {
        private Drawable foreground;
        private Path path = new Path();

        public AdaptiveIconImageView(Context context) {
            super(context);
            setScaleType(ScaleType.FIT_XY);
        }

        public void setForeground(int res) {
            foreground = ContextCompat.getDrawable(getContext(), res);
            invalidate();
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            updatePath();
        }

        public void setPadding(int padding) {
            setPadding(padding, padding, padding, padding);
        }

        public void setOuterPadding(int outerPadding) {}

        public void setBackgroundOuterPadding(int backgroundOuterPadding) {}

        @Override
        public void draw(Canvas canvas) {
            canvas.save();
            canvas.clipPath(path);
            super.draw(canvas);

            if (foreground != null) {
                foreground.setBounds(getPaddingLeft(), getPaddingTop(), getWidth() - getPaddingRight(), getHeight() - getPaddingBottom());
                foreground.draw(canvas);
            }
            canvas.restore();
        }

        private void updatePath() {
            path.rewind();
            float left = getPaddingLeft();
            float top = getPaddingTop();
            float right = getWidth() - getPaddingRight();
            float bottom = getHeight() - getPaddingBottom();
            AndroidUtilities.rectTmp.set(left, top, right, bottom);
            path.addRoundRect(AndroidUtilities.rectTmp, AndroidUtilities.dp(16), AndroidUtilities.dp(16), Path.Direction.CW);
        }
    }
}
