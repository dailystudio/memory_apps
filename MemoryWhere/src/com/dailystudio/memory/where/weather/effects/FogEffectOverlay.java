package com.dailystudio.memory.where.weather.effects;

import com.dailystudio.memory.where.R;
import com.dailystudio.memory.where.weather.OpenWeatherMapData.Weather;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.PorterDuff.Mode;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

public class FogEffectOverlay extends View implements EffectOverlayInterface {
	
	private Canvas mCanvas;
	private Bitmap mBitmap;
	private Paint mBitmapPaint;

	private Bitmap mFogBitmap;
	
	private int mFogOffset = 0;
	private Rect mSrcRect = new Rect();
	private Rect mDstRect = new Rect();
	
	public FogEffectOverlay(Context context) {
        this(context, null);
    }

    public FogEffectOverlay(Context context, AttributeSet attrs) {
        super(context, attrs);
        
        initMembers();
    }

    public FogEffectOverlay(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        
        initMembers();
    }
    
    private void initMembers() {
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);

        mFogBitmap = BitmapFactory.decodeResource(getResources(),
        		R.drawable.effect_fog);
    }

	@Override
	public boolean isWeatherMatched(Weather w) {
		if (w == null) {
			return false;
		}
		
//		return true;
		return (w.id / 100 == 7);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
	}
	
	private void drawOffscreen() {
		if (mCanvas == null) {
			return;
		}
		
		clearOffscreen();
		
		final int W = getWidth();
		final int H = getHeight();
		final int bw = mFogBitmap.getWidth();
		final int bh = mFogBitmap.getHeight();
		if (W <= 0 || H <= 0
				|| bw <= 0 || bh <= 0) {
			return;
		}
		
		final int hCount = W / bw + 2;
		final int vCount = H / bh + 1;
		
//		Logger.debug("W = %d, H = %d, bw = %d, bh = %d, hCount = %d, vCount = %d",
//				W, H, bw, bh, hCount, vCount);
		
		int xOffset;
		int yOffset;
		int srcOffset;
		int srcWidth;
		for (int v = 0; v < vCount; v++) {
			yOffset = bh * v;
			for (int h = 0; h < hCount; h++) {
				if (h == 0) {
					xOffset = 0;
					srcOffset = (bw - mFogOffset);
					srcWidth = mFogOffset;
				} else {
					xOffset = bw * (h - 1) + mFogOffset;
					srcOffset = 0;
					srcWidth = bw;
				}
				
				mSrcRect.set(srcOffset, 0, bw, bh);
				mDstRect.set(xOffset, yOffset,
						xOffset + srcWidth,
						yOffset + bh);

				mCanvas.drawBitmap(mFogBitmap,
						mSrcRect, mDstRect, mBitmapPaint);
			}
		}
		
	}
	
	private void clearOffscreen() {
		if (mCanvas == null) {
			return;
		}
		
		mCanvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
        
        mFogOffset = 0;
	}

	@Override
	public void startEffect() {
		mHandler.removeCallbacks(mDrawingRunnable);
		mHandler.post(mDrawingRunnable);
	}
	
	@Override
	public void stopEffect() {
		mHandler.removeCallbacks(mDrawingRunnable);
		clearOffscreen();
		invalidate();
	}
	
	private Runnable mDrawingRunnable = new Runnable() {
		
		@Override
		public void run() {
			drawOffscreen();
			
			invalidate();

			if (mFogBitmap == null) {
				return;
			}
			
			final int w = mFogBitmap.getWidth();
			
			if (w > 0) {
				mFogOffset = (mFogOffset + 5) % w; 
			}
			
			mHandler.removeCallbacks(mDrawingRunnable);
			mHandler.postDelayed(this, 50);
		}
		
	};
	
	private Handler mHandler = new Handler();

    
}
