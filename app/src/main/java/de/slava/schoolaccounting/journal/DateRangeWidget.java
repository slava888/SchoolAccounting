package de.slava.schoolaccounting.journal;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import java.beans.PropertyChangeEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Objects;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.slava.schoolaccounting.Main;
import de.slava.schoolaccounting.R;
import de.slava.schoolaccounting.model.DateRangeFilter;
import de.slava.schoolaccounting.util.DateUtils;
import lombok.Getter;
import lombok.Setter;

/**
 * @author by V.Sysoltsev
 */
public class DateRangeWidget extends LinearLayout {
    @Bind(R.id.btnQuickLastWeek) ImageButton btnQuickPrevWeek;
    @Bind(R.id.btnQuickThisWeek) ImageButton btnQuickThisWeek;
    @Bind(R.id.btnDateFrom) Button btnDateFrom;
    @Bind(R.id.btnDateTo) Button btnDateTo;
    @Bind(R.id.btnActionView) ImageButton btnActionView;
    @Bind(R.id.btnActionExport) ImageButton btnActionExport;

    private final static SimpleDateFormat calDateFormat = new SimpleDateFormat("EEEE dd MMM");
    private AnimatorSet animRefresh;

    @Getter
    private DateRangeFilter model;

    @Getter @Setter
    private Runnable onBtnApply;

    @Getter @Setter
    private Runnable onBtnExportToCsv;

    private static class SavedState extends View.BaseSavedState {
        @Getter
        private Date from;
        @Getter
        private Date to;

        public SavedState(Parcelable source, DateRangeFilter model) {
            super(source);
            this.from = model.getFrom().getTime();
            this.to = model.getTo().getTime();
        }
        public SavedState(Parcel source) {
            super(source);
            String v = source.readString();
            try {
                this.from = DateUtils.dfDate.parse(v);
            } catch (ParseException e) {
                Log.w(Main.getTag(), String.format("Could not restore 'from' Date from %s", v));
            }
            v = source.readString();
            try {
                this.to = DateUtils.dfDate.parse(source.readString());
            } catch (ParseException e) {
                Log.w(Main.getTag(), String.format("Could not restore 'to' Date from %s", v));
            }
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeString(DateUtils.dfDate.format(from));
            out.writeString(DateUtils.dfDate.format(to));
        }

        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    };

    public DateRangeWidget(Context context) {
        super(context, null, R.attr.roomStyle);
        init(null, 0);
    }

    public DateRangeWidget(Context context, AttributeSet attrs) {
        super(context, attrs, R.attr.roomStyle);
        init(attrs, 0);
    }

    public DateRangeWidget(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        return new SavedState(superState, model);
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        model.setFrom(DateUtils.fromDate(ss.getFrom()));
        model.setTo(DateUtils.fromDate(ss.getTo()));
    }

    private static interface ICalendarChange {
        public void onChange(Calendar cal);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // init this
        View view = inflate(getContext(), R.layout.date_range_widget, this);
        ButterKnife.bind(this, view);

        model = new DateRangeFilter();
        onBtnThisWeek();

        btnQuickPrevWeek.setOnClickListener(event -> onBtnPrevWeek());
        btnQuickThisWeek.setOnClickListener(event -> onBtnThisWeek());
        btnDateFrom.setOnClickListener(v -> {
            showDatePicker(btnDateFrom, model.getFrom(), cal -> model.setFrom(cal));
        });
        btnDateTo.setOnClickListener(v -> {
            showDatePicker(btnDateTo, model.getTo(), cal -> model.setTo(cal));
        });

//        // Load attributes
//        final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.RoomView, defStyle, 0);
//        colorBorder = a.getColor(R.styleable.RoomView_colorBorder, colorBorder);
//        colorBackground = a.getColor(R.styleable.RoomView_colorBackground, colorBackground);
//        a.recycle();

        syncModelWithUI(null);

        model.addChangeListener((event) -> {
            syncModelWithUI(event);
        });

        // actions
        btnActionView.setOnClickListener(v -> onBtnView());
        btnActionExport.setOnClickListener(v -> onBtnExport());
    }

    private void onBtnPrevWeek() {
        Calendar end = DateUtils.roundTo(GregorianCalendar.getInstance(), Calendar.DAY_OF_WEEK);
        Calendar start = DateUtils.thisPlus(end, Calendar.WEEK_OF_YEAR, -1);
        end.add(Calendar.DAY_OF_MONTH, -1);
        model.setFrom(start);
        model.setTo(end);
    }

    private void onBtnThisWeek() {
        Calendar start = DateUtils.roundTo(GregorianCalendar.getInstance(), Calendar.DAY_OF_WEEK);
        Calendar end = DateUtils.roundTo(GregorianCalendar.getInstance(), Calendar.HOUR_OF_DAY);
        model.setFrom(start);
        model.setTo(end);
    }

    private void showDatePicker(View forView, Calendar calendar, ICalendarChange listener) {
        Log.d(Main.getTag(), "Show date picker");
        Calendar now = Calendar.getInstance();
        DatePicker popupContent = new DatePicker(getContext());
        popupContent.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), (view, year, month, day) -> {
            Calendar val = new GregorianCalendar(year, month, day);
            Log.d(Main.getTag(), String.format("Calendar value changes to %s", DateUtils.dateToString(val)));
            listener.onChange(val);
        });

        PopupWindow popup = new PopupWindow(popupContent, LayoutParams.WRAP_CONTENT,  LayoutParams.WRAP_CONTENT);
        popup.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.popup_calendar_background));
        popup.setOutsideTouchable(true);
        popup.setFocusable(true);
        popup.showAsDropDown(forView, 50, 50);
    }

    private void syncModelWithUI(PropertyChangeEvent event) {
        Log.d(Main.getTag(), String.format("Sync with %s", model));
        if (event == null || Objects.equals(event.getPropertyName(), "from"))
            btnDateFrom.setText(DateUtils.dateToString(model.getFrom(), calDateFormat));
        if (event == null || Objects.equals(event.getPropertyName(), "to"))
            btnDateTo.setText(DateUtils.dateToString(model.getTo(), calDateFormat));
        // animate refresh button
        if (event != null) {
            if (animRefresh == null) {
                final Drawable src = btnActionView.getDrawable();
                ObjectAnimator fadeOut = ObjectAnimator.ofInt(src, "alpha", 255, 0);
                fadeOut.setDuration(1500);
                ObjectAnimator fadeIn = ObjectAnimator.ofInt(src, "alpha", 0, 255);
                fadeIn.setDuration(1500);
                AnimatorSet anim = new AnimatorSet();
                anim.playSequentially(fadeOut, fadeIn);
                anim.setInterpolator(new AccelerateDecelerateInterpolator());
                anim.addListener(new AnimatorListenerAdapter() {
                    private boolean cancelled = false;

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        cancelled = true;
                        super.onAnimationCancel(animation);
                        src.setAlpha(255);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (!cancelled)
                            anim.start();
                    }
                });
                animRefresh = anim;
            }
            if (!animRefresh.isRunning())
                animRefresh.start();
        }
    }

    private void onBtnView() {
        if (animRefresh != null && animRefresh.isRunning()) {
            animRefresh.cancel();
            animRefresh = null;
        }

        if (onBtnApply != null)
            onBtnApply.run();
    }

    private void onBtnExport() {
        onBtnView();
        if (onBtnExportToCsv != null)
            onBtnExportToCsv.run();
    }
}
