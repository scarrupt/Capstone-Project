package com.codefactoring.android.backlogtracker.view.issue.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

public class BarChartView extends View {

    private static final int DEFAULT_TEXT_SIZE = 14;
    private static final int DEFAULT_PADDING = 10;
    private static final int DEFAULT_LEFT_MARGIN = 10;
    private static final int DEFAULT_TOP_MARGIN = 80;
    private static final int DEFAULT_BOTTOM_MARGIN = 25;
    private static final int COLOR_RED = 0xFFF44336;
    private static final int COLOR_BLUE = 0xFF448AFF;
    private static final int COLOR_GREEN = 0xFF8BC34A;

    private final Paint mStatusLinePaint;
    private final Paint mBoxOpenPaint;
    private final Paint mLabelOpenPaint;
    private final Paint mCountOpenPaint;

    private final Paint mBoxInProgressPaint;
    private final Paint mLabelInProgressPaint;
    private final Paint mCountInProgressPaint;

    private final Paint mBoxResolvedPaint;
    private final Paint mLabelResolvedPaint;
    private final Paint mCountResolvedPaint;

    private final float mScaleFactor;

    private int mCountOpenIssues;
    private int mCountInProgressIssues;
    private int mCountResolvedIssues;

    public BarChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        final DisplayMetrics metrics = getResources().getDisplayMetrics();
        mScaleFactor = metrics.density;

        final float textSize = DEFAULT_TEXT_SIZE * mScaleFactor;

        mStatusLinePaint = new Paint();

        mBoxOpenPaint = new Paint();
        mBoxOpenPaint.setColor(COLOR_RED);
        mLabelOpenPaint = new Paint();
        mLabelOpenPaint.setTextSize(textSize);
        mCountOpenPaint = new Paint();
        mCountOpenPaint.setTextSize(textSize);

        mBoxInProgressPaint = new Paint();
        mBoxInProgressPaint.setColor(COLOR_BLUE);
        mLabelInProgressPaint = new Paint();
        mLabelInProgressPaint.setTextSize(textSize);
        mCountInProgressPaint = new Paint();
        mCountInProgressPaint.setTextSize(textSize);

        mBoxResolvedPaint = new Paint();
        mBoxResolvedPaint.setColor(COLOR_GREEN);
        mLabelResolvedPaint = new Paint();
        mLabelResolvedPaint.setTextSize(textSize);
        mCountResolvedPaint = new Paint();
        mCountResolvedPaint.setTextSize(textSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        final int width = getWidth();
        final int height = getHeight();
        final int padding = (int) (DEFAULT_PADDING * mScaleFactor);
        final int leftMargin = (int) (DEFAULT_LEFT_MARGIN * mScaleFactor);
        final int topMargin = (int) (DEFAULT_TOP_MARGIN * mScaleFactor);
        final int bottomMargin = (int) (DEFAULT_BOTTOM_MARGIN * mScaleFactor);
        final int yStatusLine = height - bottomMargin;
        final int chartWidth = width - padding * 2;
        final int barWidth = (chartWidth - (padding * 4 )) / 3;
        final float barHeight = height - topMargin;

        final float maxBarHeight = Math.max(mCountOpenIssues, Math.max(mCountInProgressIssues,
                mCountResolvedIssues));

        drawStatusLine(canvas, yStatusLine, width, leftMargin);

        final float openStatusBarRight = drawBar(canvas, leftMargin,
                calculateBarHeight(mCountOpenIssues, barHeight, maxBarHeight),
                barWidth, yStatusLine, padding, mBoxOpenPaint,
                mLabelOpenPaint, "Open",
                mCountOpenPaint, mCountOpenIssues);

        final float inProgressStatusBarRight = drawBar(canvas, openStatusBarRight,
                calculateBarHeight(mCountInProgressIssues, barHeight, maxBarHeight),
                barWidth, yStatusLine, padding, mBoxInProgressPaint,
                mLabelInProgressPaint, "In Progress",
                mCountInProgressPaint, mCountInProgressIssues);

        drawBar(canvas, inProgressStatusBarRight,
                calculateBarHeight(mCountResolvedIssues, barHeight, maxBarHeight),
                barWidth, yStatusLine, padding, mBoxResolvedPaint,
                mLabelResolvedPaint, "Resolved",
                mCountResolvedPaint, mCountResolvedIssues);
    }

    private float calculateBarHeight(int countIssues, float barHeight, float maxBarHeight) {
        return countIssues == 0 ? 0 : countIssues / maxBarHeight * barHeight;
    }

    private float drawBar(Canvas canvas, float xStart, float barHeight, int barWidth, float yStatusLine,
                         int padding, Paint boxPaint,
                         Paint labelPaint, String label,
                         Paint countPaint, int count) {

        final float barBottom = yStatusLine - padding;
        final float barTop = barBottom - barHeight;
        final float barLeft = xStart + padding;
        final float barRight = barLeft + barWidth;
        final float halfWidthBar = barWidth / 2;
        final float halfLengthLabel = labelPaint.measureText(label) / 2;
        final String textCount = String.valueOf(count);
        final float halfLengthCount = countPaint.measureText(textCount) / 2;

        canvas.drawRect(barLeft, barTop, barRight, barBottom, boxPaint);
        canvas.drawText(label, barLeft + halfWidthBar - halfLengthLabel, getHeight() - padding, labelPaint);
        canvas.drawText(textCount, barLeft + halfWidthBar - halfLengthCount, barTop - padding, countPaint);

        return barRight;
    }

    private void drawStatusLine(Canvas canvas, int y, int width, int padding) {
        canvas.drawLine(padding, y, width - padding, y, mStatusLinePaint);
    }

    public void setCountOpenIssues(int countOpenIssues) {
        this.mCountOpenIssues = countOpenIssues;
        invalidate();
    }

    public void setCountInProgressIssues(int countInProgressIssues) {
        this.mCountInProgressIssues = countInProgressIssues;
        invalidate();
    }

    public void setCountResolvedIssues(int countResolvedIssues) {
        this.mCountResolvedIssues = countResolvedIssues;
        invalidate();
    }
}