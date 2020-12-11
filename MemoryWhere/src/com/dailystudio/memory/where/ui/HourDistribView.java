package com.dailystudio.memory.where.ui;

import com.dailystudio.memory.ui.utils.ColorHelper;
import com.dailystudio.memory.where.R;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.util.TypedValue;
import android.view.View;

public class HourDistribView extends View {
	
	private final static int DEFAULT_LABEL_COUNT = 5;
	private final static int DEFAULT_LABEL_TEXT_SIZE = 20;
	private final static int MINIMUM_DISTRIB_BAR_HEIGHT = 45;
	
	private final static int HOURS_IN_DAY = 24;
	
	protected Context mContext;
	
	private Paint mFramePaint;
	private Paint mDistribPaint;
	private Paint mTextPaint;

	private Rect mTempRect = new Rect();
	private RectF mTempRectF = new RectF();
	
	private int[] mHourDistrib = new int[HOURS_IN_DAY];
	private int mMaxHourDistrib = 0;
	
	private int mLabelCount = DEFAULT_LABEL_COUNT;
	private int mDistribBarHeight = MINIMUM_DISTRIB_BAR_HEIGHT;
	private int mDistribBarColor = Color.BLUE;
	
	public HourDistribView(Context context) {
        this(context, null);
    }

    public HourDistribView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HourDistribView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        
		mContext = context;
		
		initMembers();
		
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.HourDistribView, defStyle, 0);
        
        int textColor = a.getColor(R.styleable.HourDistribView_textColor,
        		ColorHelper.getColorResource(mContext, R.color.black));
        setTextColor(textColor);

        float textSize = a.getDimensionPixelSize(R.styleable.HourDistribView_textSize, 0);
        if (textSize != 0) {
        	setTextSize(textSize);
        }

        int textStyle = a.getInt(R.styleable.HourDistribView_textStyle, 0);
        setTextStyle(textStyle);
        
        int barHeight = a.getDimensionPixelSize(
        		R.styleable.HourDistribView_distribBarHeight, MINIMUM_DISTRIB_BAR_HEIGHT);
        if (barHeight != 0) {
        	setDistributionBarHeight(barHeight);
        }
        
        int barColor = a.getColor(R.styleable.HourDistribView_distribBarColor,
        		ColorHelper.getColorResource(mContext, R.color.black));
        setDistributionBarColor(barColor);

        a.recycle();
    }

	private void initMembers() {
		mFramePaint = new Paint();
		mFramePaint.setStrokeWidth(2);
		mFramePaint.setAntiAlias(true);
		
		mDistribPaint = new Paint();
		mDistribPaint.setAntiAlias(true);
		mDistribPaint.setStyle(Style.FILL_AND_STROKE);
		mDistribPaint.setStrokeWidth(2);
		mDistribPaint.setColor(mDistribBarColor);
		mDistribPaint.setAlpha(0);
		
		mTextPaint = new Paint();
		mTextPaint.setAntiAlias(true);
		mTextPaint.setTextSize(DEFAULT_LABEL_TEXT_SIZE);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int pleft = getPaddingLeft();
        int pright = getPaddingRight();
        int ptop = getPaddingTop();
        int pbottom = getPaddingBottom();

        int w = pleft + pright;
        
        String label = null;
        int lw = 0;
		for (int i = 0; i < mLabelCount; i++) {
			label = String.valueOf(i * (HOURS_IN_DAY / (mLabelCount - 1)));
			
			lw = (int)Math.ceil(mTextPaint.measureText(label));
			w += lw;
		}
		
        int h = ptop + pbottom + (int)Math.ceil(mTextPaint.getTextSize()) + mDistribBarHeight;

		int widthSize = resolveSize(w, widthMeasureSpec);
		int heightSize = resolveSize(h, heightMeasureSpec);

		setMeasuredDimension(widthSize, heightSize);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		mFramePaint.setStyle(Style.STROKE);
		
		final int textHeight =
				(int)Math.ceil(mTextPaint.getTextSize());
		final int strokeWidth = 
				(int)Math.ceil(mFramePaint.getStrokeWidth());
		final int fStrokeWidth = 
				(int)Math.ceil(mDistribPaint.getStrokeWidth());
		
		final int paddingLeft = getPaddingLeft();
		final int paddingTop = getPaddingTop();
		final int paddingRight = getPaddingRight();
		final int paddingBottom = getPaddingBottom();
		final int w = getWidth();
		final int h = getHeight();
		
		final int l0w = (int)Math.ceil(
				mTextPaint.measureText(String.valueOf(0)));
		final int l24w = (int)Math.ceil(
				mTextPaint.measureText(String.valueOf(24)));
		
		final int frameLeft = paddingLeft + l0w / 2 + strokeWidth;
		final int frameTop = paddingTop + strokeWidth;
		final int frameRight = (w - paddingRight - l24w / 2 - strokeWidth);
		final int frameBottom = (h - paddingBottom - textHeight - strokeWidth);
		
/*		Logger.debug("padding[l: %d, t: %d, r: %d, b: %d], th: %d, size[%-3d x %-3d]",
				paddingLeft, paddingTop, paddingRight, paddingBottom,
				textHeight,
				w, h);
*/
		mTempRect.set(frameLeft, frameTop, frameRight, frameBottom);
		canvas.drawRect(mTempRect, mFramePaint);
		
		final float fXDelta = 
				((frameRight - frameLeft - strokeWidth) / (float)HOURS_IN_DAY);
/*		Logger.debug("[%d - %d - %d] / %d = %f",
				frameRight, frameLeft, strokeWidth,
				HOURS_IN_DAY,
				fXDelta);
*/
		final int fTop = frameTop + strokeWidth / 2 + fStrokeWidth / 2;
		final int fBottom = frameBottom - strokeWidth / 2 - fStrokeWidth / 2;
		float fStart = frameLeft + strokeWidth / 2.f + fStrokeWidth / 2.f;
		int alpha = 0xFF;
		for (int i = 0; i < HOURS_IN_DAY; i++) {
			mTempRectF.set(fStart, fTop,
					fStart + fXDelta - fStrokeWidth / 2, fBottom);
/*			Logger.debug("fill[%d]: [%f, %f]",
					i,
					fStart,
					fStart + fXDelta);
*/
			if (mMaxHourDistrib > 0) {
				alpha = (int)Math.round(mHourDistrib[i] * 0xFF / (float)mMaxHourDistrib);
			} else {
				alpha = 0x0;
			}
			
			mDistribPaint.setAlpha(alpha);
			canvas.drawRect(mTempRectF, mDistribPaint);
			
			fStart += fXDelta;
		}
		
		int lStartX = paddingLeft + l0w / 2;
		int lXDelta = (int)Math.ceil(((frameRight - frameLeft) / (mLabelCount - 1)));
		int lw = 0;
		String label = null;
		for (int i = 0; i < mLabelCount; i++) {
			label = String.valueOf(i * (HOURS_IN_DAY / (mLabelCount - 1)));
			
			lw = (int)Math.ceil(mTextPaint.measureText(label));
			canvas.drawText(label, 
					lStartX + lXDelta * i - (lw / 2) , (h - paddingBottom),
					mTextPaint);
			if (i != 0 && i != (mLabelCount - 1)) {
				canvas.drawLine(lStartX + lXDelta * i, frameTop, 
						lStartX + lXDelta * i, frameBottom, mFramePaint);
			}
		}
	}

	public void setDistribution(int[] result) {
		if (result == null || result.length < HOURS_IN_DAY) {
			reset();
			
			return;
		}
		
		mMaxHourDistrib = -1;
		for (int i = 0; i < HOURS_IN_DAY; i++) {
			mHourDistrib[i] = result[i];
			if (mMaxHourDistrib < result[i]) {
				mMaxHourDistrib = result[i];
			}
		}
		
		invalidate();
	}
	
	public void setDistributionBarHeight(int height) {
		if (mDistribBarHeight != height) {
			mDistribBarHeight = height;
		
			requestLayout();
			invalidate();
		}
	}

	private void setDistributionBarColor(int barColor) {
		if (mDistribBarColor != barColor) {
			mDistribBarColor = barColor;
			mDistribPaint.setColor(mDistribBarColor);
			
			invalidate();
		}
	}

	private void reset() {
		for (int i = 0; i < HOURS_IN_DAY; i++) {
			mHourDistrib[i] = 0;
		}

		mMaxHourDistrib = 0;
		
		invalidate();
	}
	
	public void setTextColor(int color) {
		mTextPaint.setColor(color);
		
		invalidate();
	}

	public void setTextSize(float size) {
		setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
	}
	
    public void setTextSize(int unit, float size) {
        Context c = getContext();
        Resources r;

        if (c == null)
            r = Resources.getSystem();
        else
            r = c.getResources();

        setRawTextSize(TypedValue.applyDimension(
            unit, size, r.getDisplayMetrics()));
    }

    private void setRawTextSize(float size) {
        if (size != mTextPaint.getTextSize()) {
            mTextPaint.setTextSize(size);

            requestLayout();
            invalidate();
        }
    }
	
	public void setTextStyle(int style) {
		Typeface tf = null;
		
		tf = mTextPaint.getTypeface();
			
		setTypeface(tf, style);
	}

	public void setTypeface(Typeface tf) {
		if (mTextPaint.getTypeface() != tf) {
			mTextPaint.setTypeface(tf);
			
			requestLayout();
			invalidate();
	    }
	}

	public void setTypeface(Typeface tf, int style) {
		if (style > 0) {
			if (tf == null) {
				tf = Typeface.defaultFromStyle(style);
			} else {
				tf = Typeface.create(tf, style);
			}

			setTypeface(tf);
			
			// now compute what (if any) algorithmic styling is needed
			int typefaceStyle = tf != null ? tf.getStyle() : 0;
			int need = style & ~typefaceStyle;
			mTextPaint.setFakeBoldText((need & Typeface.BOLD) != 0);
			mTextPaint.setTextSkewX((need & Typeface.ITALIC) != 0 ? -0.25f : 0);
		} else {
			mTextPaint.setFakeBoldText(false);
			mTextPaint.setTextSkewX(0);
			setTypeface(tf);
		}
	}
	
}
