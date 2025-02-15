package info.passdaily.camrelconvertapp.lib.result_progress;

import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import info.passdaily.camrelconvertapp.R;


public class SectorProgressView extends View {
    private int bgColor;
    private int fgColor;
    private Paint bgPaint;
    private Paint fgPaint;
    private RectF oval;

    private ObjectAnimator animator;

    public SectorProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.SectorProgressView,
                0,0);

        try {
            bgColor = a.getColor(R.styleable.SectorProgressView_bgColor, 0xffe5e5e5);
            fgColor = a.getColor(R.styleable.SectorProgressView_fgColor, 0xffff765c);
            percent = a.getFloat(R.styleable.SectorProgressView_percent, 0);
            startAngle = a.getFloat(R.styleable.SectorProgressView_startAngle, 0) + 270;

        } finally {
            a.recycle();
        }

        init();
    }

    private void init() {
        bgPaint = new Paint();
        bgPaint.setColor(bgColor);

        fgPaint = new Paint();
        fgPaint.setColor(fgColor);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        float xpad = (float)(getPaddingLeft()+getPaddingRight());
        float ypad = (float)(getPaddingBottom()+ getPaddingTop());

        float wwd = (float)w - xpad;
        float hhd = (float)h - ypad;

        oval = new RectF(getPaddingLeft(), getPaddingTop(), getPaddingLeft()+wwd, getPaddingTop()+hhd);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawArc(oval, 0, 360, true, bgPaint);
        canvas.drawArc(oval, startAngle, percent*3.6f, true, fgPaint);
    }

    public int getBgColor() {
        return bgColor;
    }

    public void setBgColor(int bgColor) {
        this.bgColor = bgColor;
        bgPaint.setColor(bgColor);
        refreshTheLayout();
    }

    public int getFgColor() {
        return fgColor;
    }

    public void setFgColor(int fgColor) {
        this.fgColor = fgColor;
        fgPaint.setColor(fgColor);
        refreshTheLayout();
    }


    private void refreshTheLayout() {
        invalidate();
        requestLayout();
    }

    private float percent;

    public float getStartAngle() {
        return startAngle;
    }

    public void setStartAngle(float startAngle) {
        this.startAngle = startAngle + 270;
        invalidate();
        requestLayout();
    }

    public float getPercent() {
        return percent;
    }

    public void setPercent(float percent) {
        this.percent = percent;
        invalidate();
        requestLayout();
    }

    private float startAngle;

    public void animateIndeterminate() {
        animateIndeterminate(800, new AccelerateDecelerateInterpolator());
    }

    public void animateIndeterminate(int durationOneCircle,
                                     TimeInterpolator interpolator) {
        animator = ObjectAnimator.ofFloat(this, "startAngle", getStartAngle(), getStartAngle() + 360);
        if (interpolator != null) animator.setInterpolator(interpolator);
        animator.setDuration(durationOneCircle);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.RESTART);
        animator.start();
    }

    public void stopAnimateIndeterminate() {
        if (animator != null) {
            animator.cancel();
            animator = null;
        }
    }
}
