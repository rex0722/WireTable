package com.study.application.animatedView;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

public class MorphView extends View {

    private static final String TAG = "MorphView";

    private int W_SIZE = 250;
    private int H_SIZE = 250;
    private final SvgData svgData;
    private final Paint contentPaint = new Paint();
    private final Paint circlePaint = new Paint();
    private DataPath path;
    private ValueAnimator pointAnimator;
    private int currentId;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        setMeasuredDimension(W_SIZE, H_SIZE);
    }

    @SuppressWarnings("ClickableViewAccessibility")
    MorphView(Context context, @Nullable AttributeSet attributeSet) {
        super(context, attributeSet);

        svgData = new SvgData(context);

        initPaint();
        initAnimator();
    }

    private void initPaint() {
        contentPaint.setAntiAlias(true);
        contentPaint.setStyle(Paint.Style.STROKE);
        contentPaint.setStrokeWidth(W_SIZE / 10);
        contentPaint.setStrokeCap(Paint.Cap.ROUND);
        contentPaint.setColor(Color.parseColor("#e3f2fd"));

        circlePaint.setAntiAlias(true);
        circlePaint.setStyle(Paint.Style.FILL);
        circlePaint.setStrokeWidth(W_SIZE / 10);
        circlePaint.setStrokeCap(Paint.Cap.ROUND);
        circlePaint.setColor(Color.parseColor("#00897b"));
    }

    private void initAnimator() {
        LinearInterpolator linearInterpolator = new LinearInterpolator();
        final long animationDuration = 170;

        pointAnimator = ValueAnimator.ofFloat(0, 1, 1.025f, 1.0125f, 1);
        pointAnimator.setDuration(animationDuration);
        pointAnimator.setInterpolator(linearInterpolator);
        pointAnimator.addUpdateListener(valueAnimator -> {
            path.reset();
            path = svgData.getMorphPath((float) valueAnimator.getAnimatedValue());
            invalidate();
        });
    }

    private void initPath(int id) {
        path = svgData.getPath(id, this);
        currentId = id;
    }

    public void performAnimation(int toId) {
        svgData.setMorphRes(currentId, toId, this);
        currentId = toId;
        pointAnimator.start();
    }

    public void setSize(int w, int h) {
        this.W_SIZE = w;
        this.H_SIZE = h;
    }

    public int getW_SIZE() {
        return W_SIZE;
    }

    public int getH_SIZE() {
        return H_SIZE;
    }

    public void setCurrentId(int id) {
        initPath(id);
        invalidate();
    }

    public void setPaintColor(String color) {
        contentPaint.setColor(Color.parseColor(color));
    }

    public void setPaintWidth(int w) {
        contentPaint.setStrokeWidth(w);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawCircle(W_SIZE / 2, H_SIZE / 2, W_SIZE * .42f, circlePaint);
        canvas.drawPath(path, contentPaint);
    }
}
