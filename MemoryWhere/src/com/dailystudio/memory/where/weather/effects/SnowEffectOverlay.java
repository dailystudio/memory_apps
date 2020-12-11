package com.dailystudio.memory.where.weather.effects;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.dailystudio.memory.where.weather.OpenWeatherMapData.Weather;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

public class SnowEffectOverlay extends View implements EffectOverlayInterface {
	
	private static final int PARTICLE_SPEED_FACTOR = 5;
	private static final int PARTICLE_MAX_RADIUS = 10;
	private static final int PARTICLE_COLOR = 0xA0FFFFFF;
	private static final int DEFAULT_PARTICLE_COUNT = 25;
	
	private class Particle {
		
		private int x;
		private int y;
		private int r;
		private int d;
		
		@Override
		public String toString() {
			return String.format("%s(0x%08x): [%-3dx%-3d, r: %d], spd = %d",
					getClass().getSimpleName(),
					hashCode(),
					x, y, r, d);
		}
		
	}

	protected Context mContext;
	
	private Random mRandom = new Random();

	private int mParticleCount = DEFAULT_PARTICLE_COUNT;
	private List<Particle> mParticles = new ArrayList<Particle>();
	private int mParticleSize = PARTICLE_MAX_RADIUS;
	
	private Canvas mCanvas;
	private Bitmap mBitmap;
	private Paint mPaint;
	private Paint mBitmapPaint;
	
	private float mAngle = 0.f;
	
	public SnowEffectOverlay(Context context) {
        this(context, null);
    }

    public SnowEffectOverlay(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SnowEffectOverlay(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        
		mContext = context;
		
		initMembers();
    }

	private void initMembers() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(PARTICLE_COLOR);
        mPaint.setStyle(Paint.Style.FILL);
 
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
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

	@Override
	public boolean isWeatherMatched(Weather w) {
		if (w == null) {
			return false;
		}
		
//		return true;
		return (w.id / 100 == 6);
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
	}
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		
		createParticles();
	}

	private void createParticles() {
		Particle p = null;
		
		final int W = getWidth();
		final int H = getHeight();
		
		for (int i = 0; i < mParticleCount; i++) {
			p = new Particle();
			
			p.x = (int)(mRandom.nextFloat() * W);
			p.y = (int)(mRandom.nextFloat() * H);
			p.r = (int)(mRandom.nextFloat() * mParticleSize + 1);
			p.d = (int)(mRandom.nextFloat() * mParticleCount);
			
			mParticles.add(p);
		}
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
		
		Particle p = null;
		for(int i = 0; i < mParticleCount; i++) {
			p = mParticles.get(i);
			mCanvas.drawCircle(p.x, p.y, p.r, mPaint);
		}
	}
	
	private void clearOffscreen() {
		if (mCanvas == null) {
			return;
		}
		
		mCanvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
	}

	protected void updateParticles() {
		mAngle += 0.01;
		
		final int W = getWidth();
		final int H = getHeight();
		
		Particle p = null;
		for(int i = 0; i < mParticleCount; i++) {
			p = mParticles.get(i);
			
			p.y += (Math.cos(mAngle + p.d) + 1 + p.r / 2) * PARTICLE_SPEED_FACTOR;
			p.x += Math.sin(mAngle) * 2;
			
			if(p.x > W + 5 || p.x < -5 || p.y > H) {
				if(i % 3 > 0)  {
					p.x = (int)(mRandom.nextFloat() * W);
					p.y = (int)(-10);
					p.r = p.r;
					p.d = p.d;
				} else {
					if(Math.sin(mAngle) > 0) {
						p.x = (int)(-5);
						p.y = (int)(mRandom.nextFloat() * H);
						p.r = p.r;
						p.d = p.d;
					} else {
						p.x = (int)(W + 5);
						p.y = (int)(mRandom.nextFloat() * H);
						p.r = p.r;
						p.d = p.d;
					}
				}
			}
			
//			Logger.debug("p[%d]: %s", i, p);
		}
	}
	
	
	private Runnable mDrawingRunnable = new Runnable() {
		
		@Override
		public void run() {
			drawOffscreen();
			
			invalidate();
			
			updateParticles();
			
			mHandler.removeCallbacks(mDrawingRunnable);
			mHandler.postDelayed(this, 50);
		}
		
	};
	
	private Handler mHandler = new Handler();

}
