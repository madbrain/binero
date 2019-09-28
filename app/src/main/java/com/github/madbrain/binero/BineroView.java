package com.github.madbrain.binero;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import java.util.ArrayList;

/**
 * from https://android.googlesource.com/platform/development/+/master/samples/training/InteractiveChart/src/com/example/android/interactivechart/InteractiveLineGraphView.java
 * 
 * https://developer.android.com/training/gestures/scale#java
 */
public class BineroView extends View {

    public interface Listener {
        void onSelectPoint(int i, int j);

        void onDeselectPoint();
    }

    private ArrayList<Listener> listeners;
    private Paint                mLinePaint;
    private Paint                mTextPaint;
    private Paint mFillPaint;

    private static final float AXIS_X_MIN       = 0f;
    private static final float AXIS_X_MAX       = 1f;
    private static final float AXIS_Y_MIN       = 0f;
    private static final float AXIS_Y_MAX       = 1f;

    private PointF currentViewportOrigin = new PointF(AXIS_X_MIN, AXIS_Y_MIN);
    private float currentViewportSize = AXIS_X_MAX;

    private Rect                 mContentRect = new Rect();
    private ScaleGestureDetector mScaleDetector;
    private GestureDetectorCompat mScrollDetector;

    private BineroGrid           grid;
    private Point mSelectedPoint;
    private ViewMode mode = ViewMode.HINT;

    public BineroView(Context context) {
        this(context, null, 0);
    }

    public BineroView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BineroView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initPaints(context);
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        mScrollDetector = new GestureDetectorCompat(context, new GestureListener());
    }

    private void initPaints(Context context) {
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextSize(40.0f);
        mTextPaint.setColor(Color.BLACK);
        mTextPaint.setTextAlign(Paint.Align.CENTER);

        mLinePaint = new Paint();
        mLinePaint.setStrokeWidth(2.0f);
        mLinePaint.setColor(Color.BLACK);
        mLinePaint.setStyle(Paint.Style.STROKE);

        mFillPaint = new Paint();
        mFillPaint.setColor(Color.YELLOW);
        mFillPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mContentRect.set(getPaddingLeft(), getPaddingTop(),
                getWidth() - getPaddingRight(), getHeight() - getPaddingBottom());
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        boolean retVal = mScaleDetector.onTouchEvent(ev);
        retVal |= mScrollDetector.onTouchEvent(ev);
        return retVal || super.onTouchEvent(ev);
    }

    private Rect mTextRect = new Rect();

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        float scale = 1f / currentViewportSize;

        canvas.save();
        canvas.translate(mContentRect.left, mContentRect.top);
        canvas.scale(scale, scale);
        float contentSize = Math.min(mContentRect.width(), mContentRect.height());
        canvas.translate(
            - currentViewportOrigin.x * contentSize,
            - currentViewportOrigin.y * contentSize);

        final float size = Math.min(mContentRect.width(), mContentRect.height());
        final float cellSize = size / this.grid.getSize();

        for (int i = 0; i <= this.grid.getSize(); ++i) {
            float y = i * cellSize;
            canvas.drawLine(0, y, size, y, mLinePaint);
            float x = i * cellSize;
            canvas.drawLine(x, 0, x, size, mLinePaint);
        }

        for (int i = 0; i < this.grid.getSize(); ++i) {
            float y = i * cellSize;
            for (int j = 0; j < this.grid.getSize(); ++j) {
                float x = j * cellSize;
                if (mSelectedPoint != null && mSelectedPoint.x == i && mSelectedPoint.y == j) {
                    canvas.drawRect(x, y, x + cellSize, y + cellSize, mFillPaint);
                }
                String str = displayOf(this.grid.get(i, j));
                if (str != null) {
                    mTextPaint.getTextBounds(str, 0, str.length(), mTextRect);
                    canvas.drawText(str, x + cellSize / 2, y + cellSize / 2 + mTextRect.height() / 2, mTextPaint);
                }
            }
        }

        canvas.restore();
    }
     
     private void constrainViewport() {
        currentViewportOrigin.x = Math.max(AXIS_X_MIN, currentViewportOrigin.x);
        currentViewportOrigin.y = Math.max(AXIS_Y_MIN, currentViewportOrigin.y);
        currentViewportSize = Math.min(currentViewportSize, 1f);
    }

    public void resetViewport() {
        currentViewportOrigin.set(0.0f, 0.0f);
        currentViewportSize = 1f;
        mSelectedPoint = null;
        invalidate();
    }

    private static String displayOf(BineroCell cell) {
        if (cell == BineroCell.ONE) {
            return "1";
        } else if (cell == BineroCell.ZERO) {
            return "0";
        } else {
            return null;
        }
    }

    public void setGrid(BineroGrid grid) {
        this.grid = grid;
        invalidate();
    }

    public void setMode(ViewMode mode) {
        this.mode = mode;
        this.mSelectedPoint = null;
        invalidate();
    }

    private void selectPoint(Point point) {
        mSelectedPoint = point;
        if (listeners != null) {
            for (int i = 0; i < listeners.size(); ++i) {
                Listener listener = listeners.get(i);
                if (point != null) {
                    listener.onSelectPoint(point.x, point.y);
                } else {
                    listener.onDeselectPoint();
                }
            }
        }
    }

    public void addListener(Listener listener) {
        if (this.listeners == null) {
            this.listeners = new ArrayList<>();
        }
        this.listeners.add(listener);
    }

    public class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        
        private PointF viewportFocus = new PointF();
        private float lastSpanX;
        private float lastSpanY;
        
        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            lastSpanX = detector.getCurrentSpanX();
            lastSpanY = detector.getCurrentSpanY();
            return true;
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float spanX = detector.getCurrentSpanX();
            float spanY = detector.getCurrentSpanY();
            
            float newSize = Math.max(lastSpanX / spanX, lastSpanY / spanY) * currentViewportSize;
            
            float focusX = detector.getFocusX();
            float focusY = detector.getFocusY();
            convertToViewport(focusX, focusY, viewportFocus);
            
            float left = viewportFocus.x
                            - newSize * (focusX - mContentRect.left) / mContentRect.width();
            float top = viewportFocus.y
                            - newSize * (focusY - mContentRect.top) / mContentRect.height();

            currentViewportOrigin.set(left, top);
            currentViewportSize = newSize;
  
            constrainViewport();

            invalidate();
            
            lastSpanX = spanX;
            lastSpanY = spanY;
            
            return true;
        }

        private void convertToViewport(float x, float y, PointF dest) {
            if (!mContentRect.contains((int) x, (int) y)) {
                return;
            }
            dest.set(
                    currentViewportOrigin.x
                            + currentViewportSize * (x - mContentRect.left) / mContentRect.width(),
                    currentViewportOrigin.y
                            + currentViewportSize * (y - mContentRect.top) / mContentRect.height());
        }
    }

    public class GestureListener extends GestureDetector.SimpleOnGestureListener {

        private PointF viewportPoint = new PointF();

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (hitTest(e.getX(), e.getY(), viewportPoint)) {
                int j = (int) (viewportPoint.x * grid.getSize());
                int i = (int) (viewportPoint.y * grid.getSize());
                if (i >= grid.getSize() || j >= grid.getSize()) {
                    selectPoint(null);
                } else {
                    selectPoint(new Point(i, j));
                }
                invalidate();
                return true;
            }
            return super.onSingleTapConfirmed(e);
        }

        private boolean hitTest(float x, float y, PointF dest) {
            if (!mContentRect.contains((int) x, (int) y)) {
                return false;
            }
            float size = Math.min(mContentRect.width(), mContentRect.height());
            dest.set(
                    currentViewportOrigin.x
                            + currentViewportSize * (x - mContentRect.left) / size,
                    currentViewportOrigin.y
                            + currentViewportSize * (y - mContentRect.top) / size);
            return true;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }
        
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            float viewportOffsetX = distanceX * currentViewportSize / mContentRect.width();
            float viewportOffsetY = distanceY * currentViewportSize / mContentRect.height();
            setViewportTopLeft(
                    currentViewportOrigin.x + viewportOffsetX,
                    currentViewportOrigin.y + viewportOffsetY);
            invalidate();
            return true;
        }
        
        private void setViewportTopLeft(float x, float y) {
            x = Math.max(AXIS_X_MIN, Math.min(x, AXIS_X_MAX - currentViewportSize));
            y = Math.max(AXIS_Y_MIN, Math.min(y, AXIS_Y_MAX - currentViewportSize));
            currentViewportOrigin.set(x, y);
        }

    }
}
